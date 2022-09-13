package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;
import edu.yu.cs.com1320.project.stage2.impl.DocumentImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, Document> hashTable;
    private StackImpl<Command> commandStack;

    public DocumentStoreImpl(){
        this.hashTable = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
    }
    /**
     * the two document formats supported by this document store.
     * Note that TXT means plain text, i.e. a String.
     */
    enum DocumentFormat{
        TXT,BINARY
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
            String result = scanner.hasNext() ? scanner.next() : null;
            doc = new DocumentImpl(uri, result);
        } else {
            byte[] data = input.readAllBytes();
            doc = new DocumentImpl(uri, data);
        }
        Document returnedDoc = this.hashTable.put(uri, doc);
        if (returnedDoc == null){
            Command command = new Command(doc.getKey(), URI -> {
                this.hashTable.put(doc.getKey(), null);
                return true;
            });
            this.commandStack.push(command);
            return 0;
        } else {
            Command command = new Command(doc.getKey(), URI -> {
                this.hashTable.put(doc.getKey(), returnedDoc);
                return true;
            });
            this.commandStack.push(command);
            return returnedDoc.hashCode();
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
        Command command;
        if (doc != null){
            command = new Command(doc.getKey(), URI -> {
                this.hashTable.put(doc.getKey(), doc);
                return true;
            });
        } else {
            command = new Command(uri, URI -> {
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
        Command command = this.commandStack.pop();
        command.undo();
    }
    private boolean checkForURI(URI uri){
        StackImpl<Command> tempStack = new StackImpl<>();
        while (this.commandStack.peek().getUri() != uri){
            Command movingCommand = this.commandStack.pop();
            tempStack.push(movingCommand);
            if (commandStack.peek() == null){
                while(tempStack.size() != 0){
                    Command moveCommand = tempStack.pop();
                    this.commandStack.push(moveCommand);
                }
                return false;
            }
        }
        while(tempStack.size() != 0){
            Command movingCommand = tempStack.pop();
            this.commandStack.push(movingCommand);
        }
        return true;
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
        StackImpl<Command> tempStack = new StackImpl<>();
        while (this.commandStack.peek().getUri() != uri){
            Command movingCommand = this.commandStack.pop();
            tempStack.push(movingCommand);
        }
        Command executeCommand = this.commandStack.pop();
        executeCommand.undo();
        while(tempStack.size() != 0){
            Command movingCommand = tempStack.pop();
            this.commandStack.push(movingCommand);
        }
    }
}
