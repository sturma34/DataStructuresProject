package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.stage2.Document;

import java.net.URI;
import java.util.Arrays;

public class DocumentImpl implements Document {
    private String text;
    private URI uri;
    private byte[] binaryData;
    public DocumentImpl(URI uri, String txt){
        if (uri == null || txt == null || txt.length() == 0 || uri.toASCIIString().isBlank() || txt.isBlank()){
            throw new IllegalArgumentException("Please provide the correct parameters for the constructor.");
        }
        this.text = txt;
        this.uri = uri;
    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null || binaryData == null || binaryData.length == 0 || uri.toASCIIString().isBlank()){
            throw new IllegalArgumentException("Please provide the correct parameters for the constructor.");
        }
        this.binaryData = binaryData;
        this.uri = uri;
    }
    /**
     * @return content of text document
     */
    public String getDocumentTxt(){
        return this.text;
    }

    /**
     * @return content of binary data document
     */
    public byte[] getDocumentBinaryData(){
        if(this.binaryData == null){
            return null;
        } else {
            byte[] copy = this.binaryData.clone();
            return copy;
        }
    }


    /**
     * @return URI which uniquely identifies this document
     */
    public URI getKey(){
        return this.uri;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0); result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass()!=obj.getClass()){
            return false;
        }
        Document document = (Document) obj;
        return document.hashCode() == this.hashCode();
    }


}
