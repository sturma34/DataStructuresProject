package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document {
    private String text;
    private byte[] binaryData;
    private URI uri;
    private Map<String, Integer> wordCounts;
    private transient long lastUsedTime;

    public DocumentImpl(URI uri, String txt,Map<String, Integer> wordCountMap){
        if (uri == null || txt == null || txt.length() == 0 || uri.toASCIIString().isBlank() || txt.isBlank()){
            throw new IllegalArgumentException("Please provide the correct parameters for the constructor.");
        }
        this.text = txt;
        this.uri = uri;
        if(wordCountMap == null){
            this.wordCounts = new HashMap<>();
            String cleanText = this.text.replaceAll("[^A-Za-z0-9_\\s]", "").toLowerCase();
            String[] inputWords = cleanText.split("\\s+");
            for(String inputWord : inputWords) {
                if (this.hasWord(inputWord)){
                    this.wordCounts.put(inputWord,this.wordCounts.get(inputWord)+1);
                } else {
                    this.wordCounts.put(inputWord,1);
                }
            }
        } else {
            this.wordCounts = wordCountMap;
        }

        this.lastUsedTime = 0;
    }

    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null || binaryData == null || binaryData.length == 0 || uri.toASCIIString().isBlank()){
            throw new IllegalArgumentException("Please provide the correct parameters for the constructor.");
        }
        this.binaryData = binaryData;
        this.uri = uri;
    }

    /**
     * This must set the word to count map during deserialization
     * @param wordMap
     */
    public void setWordMap(Map<String,Integer> wordMap){
        if(this.text == null){
            throw new IllegalStateException("This is a binary document.");
        }
        String cleanText = this.text.replaceAll("[^A-Za-z0-9_\\s]", "").toLowerCase();
        String[] inputWords = cleanText.split("\\s+");
        for(String inputWord : inputWords) {
            if (this.hasWord(inputWord)){
                this.wordCounts.put(inputWord,this.wordCounts.get(inputWord)+1);
            } else {
                this.wordCounts.put(inputWord,1);
            }
        }
    }

    /**
     * @return a copy of the word to count map so it can be serialized
     */
    public Map<String,Integer> getWordMap(){
        if(this.text == null){
            return new HashMap<String, Integer>();
        }
        HashMap<String, Integer> copyMap = new HashMap<>();
        for(String word : this.wordCounts.keySet()){
            copyMap.put(word, this.wordCounts.get(word));
        }
        return copyMap;
    }


    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    public long getLastUseTime(){
        return this.lastUsedTime;
    }
    public void setLastUseTime(long timeInNanoseconds){
        this.lastUsedTime = timeInNanoseconds;
    }

    private boolean hasWord(String word){
        return this.wordCounts.containsKey(word);
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
            return this.binaryData.clone();
        }
    }


    /**
     * @return URI which uniquely identifies this document
     */
    public URI getKey(){
        return this.uri;
    }

    /**
     * how many times does the given word appear in the document?
     * @param word word to check the number of times it appears
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    public int wordCount(String word){
        if (this.text == null){
            return 0;
        } else {
            word = word.toLowerCase();
            return this.wordCounts.getOrDefault(word, 0);
        }
    }

    /**
     * @return all the words that appear in the document
     */
    public Set<String> getWords(){
        if(this.binaryData == null){
            return this.wordCounts.keySet();
        } else {
            return new HashSet<>();
        }
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


    @Override
    public int compareTo(Document o) {
        return Long.compare(this.getLastUseTime(), o.getLastUseTime());
    }
}
