package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.lang.System;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, Document> hashTable;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<Document> trie;
    private MinHeapImpl<Document> heap;
    private int maxDocumentCount;
    private int maxDocumentBytes;
    private int documentCount;
    private int documentBytesCount;
    private Set<URI> keySet;


    public DocumentStoreImpl(){
        this.hashTable = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.heap = new MinHeapImpl<>();
        this.maxDocumentCount = Integer.MAX_VALUE;
        this.maxDocumentBytes = Integer.MAX_VALUE;
        this.documentBytesCount = 0;
        this.documentCount = 0;
        this.keySet = new HashSet<>();
    }

//    public MinHeapImpl getHeap(){
//        return this.heap;
//    }

    /**
     * the two document formats supported by this document store.
     * Note that TXT means plain text, i.e. a String.
     */
    enum DocumentFormat{
        TXT,BINARY
    }

    /**
     * set maximum number of documents that may be stored
     * @param limit the max number of docs.
     */
    public void setMaxDocumentCount(int limit){
        if(limit < 0){
            throw new IllegalArgumentException("Please enter a valid maximum for the doc count.");
        }
        //If the current number of documents is greater than the limit the method is trying to set, delete docs from memory entirely.
        if(this.documentCount > limit){
            while(this.documentCount > limit){
                this.deleteDocumentFromMemory(this.heap.remove().getKey());
            }
        }
        this.maxDocumentCount = limit;
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    public void setMaxDocumentBytes(int limit){
        if(limit < 0){
            throw new IllegalArgumentException("Please enter a valid maximum for the doc count.");
        }
        if(this.documentBytesCount > limit){
            while(this.documentBytesCount > limit){
                this.deleteDocumentFromMemory(this.heap.remove().getKey());
            }
        }
        this.maxDocumentBytes = limit;
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> search(String keyword){
        keyword = keyword.toLowerCase();
        DocumentWordCountComparator comp = new DocumentWordCountComparator(keyword);
        List<Document> returnList =  this.trie.getAllSorted(keyword, comp);
        for(Document doc : returnList){
            doc.setLastUseTime(System.nanoTime());
            this.heap.reHeapify(doc);
        }
        return returnList;
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> searchByPrefix(String keywordPrefix){
        keywordPrefix = keywordPrefix.toLowerCase();
        DocumentPrefixCountComparator comp = new DocumentPrefixCountComparator(keywordPrefix);
        List<Document> returnList = this.trie.getAllWithPrefixSorted(keywordPrefix, comp);
        for(Document doc : returnList){
            doc.setLastUseTime(System.nanoTime());
            this.heap.reHeapify(doc);
        }
        return returnList;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAll(String keyword){
        keyword = keyword.toLowerCase();

        //Delete all instances of documents containing this keyword from the trie.
        HashSet<Document> deletedDocs = new HashSet<>(this.trie.deleteAll(keyword));

        //Iterate over all the documents that were deleted and remove them from any other places in the trie.
        for(Document doc : deletedDocs){
            this.deleteDocument(doc.getKey());
            //Delete method adds a command to the stack that we don't need because the command set will be added, so remove a command each time.
            //Not the most elegant solution, but it is the simplest.
            this.commandStack.pop();
        }
        this.pushUndoablesForDeletedSet(deletedDocs);
        return this.getUris(deletedDocs);
    }

    /**
     * Converts a set of docs into a set of their uris.
     * @param deletedDocs a set of docs that were just deleted.
     * @return a set of corresponding uris.
     */
    private Set<URI> getUris (Set<Document> deletedDocs){
        HashSet<URI> deletedURIS = new HashSet<>();
        for (Document doc : deletedDocs){
            deletedURIS.add(doc.getKey());
        }
        return deletedURIS;
    }

    /**
     *  Pushes undoables to the command stack to put a set of docs back that were deleted.
     * @param deletedDocs the set of docs that were deleted.
     */
    private void pushUndoablesForDeletedSet(HashSet<Document> deletedDocs) {
        if (deletedDocs.size() > 1) {
            this.commandStack.push(this.getCommandSetFromDocSet(deletedDocs));
        } else if (deletedDocs.size() == 1){
            for (Document doc : deletedDocs) {
                GenericCommand<URI> putDoc = new GenericCommand<>(doc.getKey(), URI -> {
                    this.hashTable.put(doc.getKey(), doc);
                    this.makeRoomForPut(doc);
                    this.keySet.add(doc.getKey());
                    for (String word : doc.getWords()) {
                        this.trie.put(word, doc);
                    }
                    this.insertAndReHeapify(doc);
                    this.documentBytesCount +=  doc.getDocumentTxt().getBytes().length;
                    this.documentCount++;
                    return true;
                });
                this.commandStack.push(putDoc);
            }
        }
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        keywordPrefix = keywordPrefix.toLowerCase();
        HashSet<Document> deletedDocs = new HashSet<>(this.trie.deleteAllWithPrefix(keywordPrefix));
        for(Document doc : deletedDocs){
            this.deleteDocument(doc.getKey());
            this.commandStack.pop();
        }
        this.pushUndoablesForDeletedSet(deletedDocs);
        return this.getUris(deletedDocs);
    }

    /**
     * Helper method that takes a given set of docs that were deleted, and returns a command set that would put all of them bcak.
     * @param docSet the docs that were deleted.
     * @return the commandset that would put them all back.
     */
    private CommandSet<URI> getCommandSetFromDocSet(Set<Document> docSet){
        CommandSet<URI> docsToPutBack = new CommandSet<>();
        for (Document doc : docSet){
            GenericCommand<URI> putDoc = new GenericCommand<>(doc.getKey(), URI -> {
                this.hashTable.put(doc.getKey(), doc);
                for (String word : doc.getWords()){
                    this.trie.put(word, doc);
                }
                this.makeRoomForPut(doc);
                this.keySet.add(doc.getKey());
                doc.setLastUseTime(System.nanoTime());
                this.insertAndReHeapify(doc);
                this.documentBytesCount +=  doc.getDocumentTxt().getBytes().length;
                this.documentCount++;
                return true;
            });
            docsToPutBack.addCommand(putDoc);
        }
        return docsToPutBack;
    }
//    public int getStackSize(){
//        return this.commandStack.size();
//    }

    /**
     * puts a doc into the heap and then calls reheapify to move it to the correct spot.
     * @param doc to be inserted.
     */
    private void insertAndReHeapify(Document doc){
        this.heap.insert(doc);
        doc.setLastUseTime(System.nanoTime());
        this.heap.reHeapify(doc);
    }

    /**
     * If there is not enough memory to put a given doc, this helper method makes room for it.
     * @param doc that needs to be added.
     */
    private void makeRoomForPut(Document doc){
        if(this.keySet.contains(doc.getKey())){
            if(this.documentBytesCount + this.getBothBytes(doc) > this.maxDocumentBytes){
                this.clearDocumentsMaxBytesProblem(doc);
            }
        } else if (this.documentBytesCount + this.getBothBytes(doc) > this.maxDocumentBytes){
            this.clearDocumentsMaxBytesProblem(doc);
        } else if(this.documentCount + 1 > this.maxDocumentCount){
            this.deleteDocumentFromMemory(this.heap.remove().getKey());
        }
    }
    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
    public int putDocument(InputStream input, URI uri, DocumentStore.DocumentFormat format) throws IOException{
        if (uri == null || format == null || uri.toASCIIString().isBlank()){
            throw new IllegalArgumentException("Either the URI or format is null.");
        }
        Document doc;
        if (format == DocumentStore.DocumentFormat.TXT){
            Scanner scanner = new Scanner(input).useDelimiter("\\A");
            doc = new DocumentImpl(uri, scanner.hasNext() ? scanner.next() : null);
        } else {
            doc = new DocumentImpl(uri, input.readAllBytes());
        }
        this.makeRoomForPut(doc);
        Document returnedDoc = this.hashTable.put(uri, doc);
        this.keySet.add(doc.getKey());
        if(returnedDoc != null){
            this.removeTracesOfDocument(returnedDoc);
        }
        this.addDocWordsToTrie(doc);
        this.insertAndReHeapify(doc);
        this.updateMemoryCounts(doc, 1);
        GenericCommand<URI> command = new GenericCommand(doc.getKey(), URI -> {
            this.removeTracesOfDocument(doc);
            this.hashTable.put(doc.getKey(), returnedDoc);
            if(returnedDoc != null){
                this.keySet.add(returnedDoc.getKey());
                this.addTracesOfDocument(returnedDoc);
            }
            return true;
        });
        this.commandStack.push(command);
        return returnedDoc == null ?  0 :  returnedDoc.hashCode();
    }

    /**
     * Helper method that adds a doc to the other data structures, once it's in the hashtable.
     * @param doc the doc to add.
     */
    private void addTracesOfDocument(Document doc){
        if (doc != null){
            this.addDocWordsToTrie(doc);
            this.updateMemoryCounts(doc, 1);
            this.insertAndReHeapify(doc);
        }
    }

    /**
     * Helper method that removes a doc from the other data structures, once it's gone from the hashtable.
     * @param doc the doc to remove.
     */
    private void removeTracesOfDocument(Document doc){
        if (doc != null){
            this.deleteWordsFromTrie(doc);
            this.updateMemoryCounts(doc, -1);
            this.deleteFromHeap(doc);
            this.deleteFromStack(doc);
        }
    }

    /**
     * Removes all the times this doc is stored in the trie at its given words.
     * @param doc the document to remove from the trie.
     */
    private void deleteWordsFromTrie(Document doc){
        for (String word : doc.getWords()){
            if(this.trie.getAllSorted(word, new DocumentWordCountComparator(word)).size() > 0) {
                this.trie.delete(word, doc);
            }
        }
    }

    /**
     * Remove any undo commands associated with a given doc from the command stack.
     * @param doc the doc whose associated uri will be purged from the stack.
     */
    private void deleteFromStack(Document doc){
        //Create a temp stack to hold the commands while looking through the main stack.
        StackImpl<Undoable> tempStack = new StackImpl<>();
        //Lool through the stack.
        while(this.commandStack.size() > 0){
            //If it's an individual command.
            if(this.commandStack.peek() instanceof GenericCommand){
                GenericCommand<URI> command = (GenericCommand<URI>) this.commandStack.peek();
                //If it's not the doc we are looking for, put it on the temp stack.
                if(command.getTarget() != doc.getKey()){
                    tempStack.push(this.commandStack.pop());
                } else {
                    //Otherwise, get rid of it.
                    this.commandStack.pop();
                }
            } //If it's a set of commands.
            else {
                CommandSet<URI> commandSet = (CommandSet<URI>) this.commandStack.peek();
                //Check each command in the set.
                for(GenericCommand<URI>command: commandSet){
                    //If it's the one we are looking for, remove it and leave the loop. By definition, there can only be one command with that uri in the stack.
                    if(command.getTarget() == doc.getKey()){
                        commandSet.remove(command);
                        break;
                    }
                }
                //If the set is now empty, dispose of it. Otherwise, save the rest of them on the temp stack.
                if(commandSet.isEmpty()){
                    this.commandStack.pop();
                } else {
                    tempStack.push(this.commandStack.pop());
                }
            }
        }
        //Refill the command stack.
        while(tempStack.size() > 0){
            this.commandStack.push(tempStack.pop());
        }
    }
    /**
     * Remove all traces of the doc at the given uri from memory, including hashtable, trie, and stack, when it's already gone from heap.
     * @param uri the unique identifier of the document to delete
     */
    private void deleteDocumentFromMemory(URI uri){
        //Remove the doc from the hashtable at the given uri.
        Document doc = this.hashTable.put(uri, null);
        this.keySet.remove(uri);
        //If something was indeed deleted, remove it from the trie, adjust the memory counts, and remove relevant commands from the stack.
        //Method is only called when it's already gone from the heap.
        if (doc != null){
            this.deleteWordsFromTrie(doc);
            this.updateMemoryCounts(doc, -1);
            this.deleteFromStack(doc);
        }
    }

    /**
     * Helper method that makes room for a doc when the memory limit prevents it from being added immediately.
     * @param doc the document to add.
     */
    private void clearDocumentsMaxBytesProblem(Document doc){
        if(this.getBothBytes(doc) > this.maxDocumentBytes){
            throw new IllegalArgumentException("The size of this document exceeds this DocStore's memory capabilities.");
        }
        while(this.documentBytesCount + this.getBothBytes(doc) > this.maxDocumentBytes){
            this.deleteDocumentFromMemory(this.heap.remove().getKey());
        }
    }

    /**
     * Stores this doc in the trie all of its given words.
     * @param doc the document to add to the trie.
     */
    private void addDocWordsToTrie(Document doc){
        for(String word: doc.getWords()){
            this.trie.put(word, doc);
        }
    }

    /**
     * Helper method that return a doc's byte count whether it's in binary or text form.
     * @param doc the document to check.
     * @return the number of bytes.
     */
    private int getBothBytes(Document doc){
        return doc.getDocumentBinaryData() == null ? doc.getDocumentTxt().getBytes().length : doc.getDocumentBinaryData().length;
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri){
        Document doc = this.hashTable.get(uri);
        if (doc != null){
            doc.setLastUseTime(System.nanoTime());
            this.heap.reHeapify(doc);
        }
        return doc;
    }

    /**
     * Helper method that removes a given doc from the heap.
     * @param doc to be removed.
     */
    private void deleteFromHeap(Document doc){
        doc.setLastUseTime(0);
        this.heap.reHeapify(doc);
        this.heap.remove();
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean deleteDocument(URI uri){
        Document doc = this.hashTable.put(uri, null);
        this.keySet.remove(uri);
        GenericCommand<URI> command;
        if (doc != null){
            this.deleteWordsFromTrie(doc);
            this.deleteFromHeap(doc);
            this.updateMemoryCounts(doc, -1);
            command = new GenericCommand(doc.getKey(), URI -> {
                this.makeRoomForPut(doc);
                this.hashTable.put(doc.getKey(), doc);
                this.keySet.add(doc.getKey());
                this.addDocWordsToTrie(doc);
                this.insertAndReHeapify(doc);
                this.updateMemoryCounts(doc, 1);
                return true;
            });
        } else {
            command = new GenericCommand(uri, URI -> true);
        }
        this.commandStack.push(command);
        return doc != null;
    }

    /**
     * Either add or subtract the given doc and it's size from the relevant counters.
     * @param doc whose memory values will be adjusted for.
     * @param add int value that indicates whether to add or subtract from memory counts.
     */
    private void updateMemoryCounts(Document doc, int add){
        if (add == 1){
            this.documentCount++;
            this.documentBytesCount += doc.getDocumentBinaryData() == null ? doc.getDocumentTxt().getBytes().length : doc.getDocumentBinaryData().length;
        } else {
            this.documentCount--;
            this.documentBytesCount -= doc.getDocumentBinaryData() == null ? doc.getDocumentTxt().getBytes().length : doc.getDocumentBinaryData().length;
        }
    }
    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    public void undo() throws IllegalStateException{
        if (this.commandStack.peek() == null){
            throw new IllegalStateException("There are no actions left to undo.");
        }
        Undoable command = this.commandStack.pop();
        command.undo();
    }

    /**
     * Checks if a given uri is within a given undoable.
     * @param undoable to check.
     * @param uri to check.
     * @return whether it was there or not.
     */
    private boolean checkUndoableForURI(Undoable undoable, URI uri){
        if(undoable instanceof GenericCommand){
            URI gotURI = ((GenericCommand<URI>) undoable).getTarget();
            return gotURI == uri;
        } else {
            CommandSet<URI> set = (CommandSet<URI>) undoable;
            return set.containsTarget(uri);
        }
    }

    /**
     * Checks if a given uri is found in the stack.
     * @param uri to look for.
     * @return whether it was there or not.
     */
    private boolean checkForURI(URI uri){
        StackImpl<Undoable> tempStack = new StackImpl<>();
        while (this.commandStack.size() != 0){
            if(!this.checkUndoableForURI(this.commandStack.peek(), uri)){
                Undoable movingCommand = this.commandStack.pop();
                tempStack.push(movingCommand);
                if (commandStack.peek() == null){
                    while(tempStack.size() != 0){
                        Undoable moveCommand = tempStack.pop();
                        this.commandStack.push(moveCommand);
                    }
                    return false;
                }
            } else {
                while(tempStack.size() != 0){
                    Undoable movingCommand = tempStack.pop();
                    this.commandStack.push(movingCommand);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException{
        if (this.commandStack.peek() == null){
            throw new IllegalStateException("There are no actions left to undo.");
        }
        if(!this.checkForURI(uri)){
            throw new IllegalStateException("There are no actions left to undo.");
        }
        StackImpl<Undoable> tempStack = new StackImpl<>();
        while (!this.checkUndoableForURI(this.commandStack.peek(), uri)){
            Undoable movingCommand = this.commandStack.pop();
            tempStack.push(movingCommand);
        }
        Undoable executeCommand = this.commandStack.pop();
        if(executeCommand instanceof GenericCommand){
            executeCommand.undo();
        } else {
            CommandSet<URI> commandSet = (CommandSet<URI>) executeCommand;
            commandSet.undo(uri);
            if(commandSet.size() != 0){
                this.commandStack.push(commandSet);
            }
        }
        while(tempStack.size() != 0){
            Undoable movingCommand = tempStack.pop();
            this.commandStack.push(movingCommand);
        }
    }

    private class DocumentWordCountComparator implements Comparator<Document> {
        private String keyword;
        public DocumentWordCountComparator(String keyword){
            this.keyword = keyword;
        }

        @Override
        public int compare (Document doc1, Document doc2){
            int doc1Count = doc1.wordCount(this.keyword);
            int doc2Count = doc2.wordCount(this.keyword);
            return Integer.compare(doc2Count, doc1Count);
        }
    }

    private class DocumentPrefixCountComparator implements Comparator<Document> {
        private String prefix;
        public DocumentPrefixCountComparator(String prefix){
            this.prefix = prefix;
        }

        @Override
        public int compare (Document doc1, Document doc2){
            int doc1Count = 0;
            for (String word : doc1.getWords()){
                if(word.startsWith(this.prefix)){
                    doc1Count += doc1.wordCount(word);
                }
            }
            int doc2Count = 0;
            for (String word : doc2.getWords()){
                if(word.startsWith(this.prefix)){
                    doc2Count += doc2.wordCount(word);
                }
            }
            return Integer.compare(doc2Count, doc1Count);
        }
    }
}
