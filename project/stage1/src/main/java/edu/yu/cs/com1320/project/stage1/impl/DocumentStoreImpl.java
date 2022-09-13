package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, Document> hashTable;

    public DocumentStoreImpl(){
        this.hashTable = new HashTableImpl<>();
    }
    /**
     * the two document formats supported by this document store.
     * Note that TXT means plain text, i.e. a String.
     */
    enum DocumentFormat{
        TXT,BINARY
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
            if (input == null){
                doc = null;
            } else {
                Scanner scanner = new Scanner(input).useDelimiter("\\A");
                String result = scanner.hasNext() ? scanner.next() : null;
                doc = new DocumentImpl(uri, result);
            }
        } else {
            byte[] data = input.readAllBytes();
            doc = new DocumentImpl(uri, data);
        }
        Document returnedDoc = this.hashTable.put(uri, doc);
        if (returnedDoc == null){
            return 0;
        } else {
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
        return doc != null;
    }
}
