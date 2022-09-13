package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, Document> hashTable;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<Document> trie;

    public DocumentStoreImpl(){
        this.hashTable = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
        this.trie = new TrieImpl<>();
    }
    /**
     * the two document formats supported by this document store.
     * Note that TXT means plain text, i.e. a String.
     */
    enum DocumentFormat{
        TXT,BINARY
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
        return this.trie.getAllSorted(keyword, comp);
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
        return this.trie.getAllWithPrefixSorted(keywordPrefix, comp);
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
            this.commandStack.pop();
        }
        return getUris(deletedDocs);
    }

    private Set<URI> getUris(HashSet<Document> deletedDocs) {
        HashSet<URI> deletedURIS = new HashSet<>();
        for (Document doc : deletedDocs){
            deletedURIS.add(doc.getKey());
        }
        if (deletedDocs.size() > 1) {
            this.commandStack.push(this.getCommandSetFromDocSet(deletedDocs));
        } else if (deletedDocs.size() == 1){
            for (Document doc : deletedDocs) {
                GenericCommand<URI> putDoc = new GenericCommand<>(doc.getKey(), URI -> {
                    this.hashTable.put(doc.getKey(), doc);
                    for (String word : doc.getWords()) {
                        this.trie.put(word, doc);
                    }
                    return true;
                });
                this.commandStack.push(putDoc);
            }
        }
        return deletedURIS;
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
        return getUris(deletedDocs);

    }

    private CommandSet<URI> getCommandSetFromDocSet(Set<Document> docSet){
        CommandSet<URI> docsToPutBack = new CommandSet<>();
        for (Document doc : docSet){
            GenericCommand<URI> putDoc = new GenericCommand<>(doc.getKey(), URI -> {
                this.hashTable.put(doc.getKey(), doc);
                for (String word : doc.getWords()){
                    this.trie.put(word, doc);
                }
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
        Document returnedDoc = this.hashTable.put(uri, doc);
        this.addDocWordsToTrie(doc);
        GenericCommand<URI> command = new GenericCommand(doc.getKey(), URI -> {
            this.deleteDocWordsFromTrie(doc);
            this.hashTable.put(doc.getKey(), returnedDoc);
            if(returnedDoc != null){
                this.addDocWordsToTrie(returnedDoc);
            }
            return true;
        });
        this.commandStack.push(command);
        if (returnedDoc == null){
            return 0;
        } else {
            return returnedDoc.hashCode();
        }
    }

    private void addDocWordsToTrie(Document doc){
        for(String word: doc.getWords()){
            this.trie.put(word, doc);
        }
    }

    private void deleteDocWordsFromTrie(Document doc){
        for(String word: doc.getWords()){
            this.trie.delete(word, doc);
        }
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri){
        return this.hashTable.get(uri);
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean deleteDocument(URI uri){
        Document doc = this.hashTable.put(uri, null);
        if (doc != null){
            for (String word : doc.getWords()){
                if(this.trie.getAllSorted(word, new DocumentWordCountComparator(word)).size() == 0){
                } else {
                    this.trie.delete(word, doc);
                }
            }
        }
        GenericCommand<URI> command;
        if (doc != null){
            command = new GenericCommand(doc.getKey(), URI -> {
                this.hashTable.put(doc.getKey(), doc);
                for (String word : doc.getWords()){
                    this.trie.put(word, doc);
                }
                return true;
            });
        } else {
            command = new GenericCommand(uri, URI -> {
                this.hashTable.put(uri, doc);
                return true;
            });
        }
        this.commandStack.push(command);
        return doc != null;
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

    private boolean checkUndoableForURI(Undoable undoable, URI uri){
        if(undoable instanceof GenericCommand){
            URI gotURI = ((GenericCommand<URI>) undoable).getTarget();
            return gotURI == uri;
        } else {
            CommandSet<URI> set = (CommandSet<URI>) undoable;
            return set.containsTarget(uri);
        }
    }
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
