package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class HeapTest {
    private String txt10;
    private String txt11;
    private String txt12;
    private String txt13;
    private String txt14;
    private String txt15;
    private String txt16;
    private String txt17;
    private String txt18;

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;

    //variables to hold possible values for doc3
    private URI uri3;
    private String txt3;

    //variables to hold possible values for doc4
    private URI uri4;
    private String txt4;

    //variables to hold possible values for doc1
    private URI uri5;
    private String txt5;

    //variables to hold possible values for doc2
    private URI uri6;
    private String txt6;

    //variables to hold possible values for doc3
    private URI uri7;
    private String txt7;

    //variables to hold possible values for doc4
    private URI uri8;
    private String txt8;

    //variables to hold possible values for doc4
    private URI uri9;
    private String txt9;

    //variables to hold possible values for doc1
    private URI uri0;
    private String txt0;

    private HashSet<String> textSet;
    private HashSet<URI> uriSet;


    @BeforeEach
    public void init(){
        txt10 = "";
        txt11 = "A";
        txt12 = "AA";
        txt13 = "AAA";
        txt14 = "AAAA";
        txt15 = "AAAAA";
        txt16 = "AAAAAA";
        txt17 = "AAAAAAA";
        txt18 = "AAAAAAAA";
    }
    @BeforeEach
    public void init2() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "Doc1: the text of doc1, in plain text. No fancy file format - just plain old String. Computer. Headphones.";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Doc2: Text for doc2. A plain old String.";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "Doc3: the text of doc3, this is";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "Doc4: This is the text of doc4";

        //init possible values for doc4
        this.uri5 = new URI("http://edu.yu.cs/com1320/project/doc5");
        this.txt5 = "Doc5: This is the text of doc5: This is the text of doc5: This is the text of doc5";

        //init possible values for doc4
        this.uri6 = new URI("http://edu.yu.cs/com1320/project/doc6");
        this.txt6 = "Doc6: O' say can you see";

        //init possible values for doc4
        this.uri7 = new URI("http://edu.yu.cs/com1320/project/doc7");
        this.txt7 = "Doc7: By the dawn's early light";

        //init possible values for doc4
        this.uri8 = new URI("http://edu.yu.cs/com1320/project/doc8");
        this.txt8 = "Doc8: plain bagel with cream cheese";

        //init possible values for doc4
        this.uri9 = new URI("http://edu.yu.cs/com1320/project/doc9");
        this.txt9 = "Doc9: that's so boring";

        //init possible values for doc4
        this.uri0 = new URI("http://edu.yu.cs/com1320/project/doc9");
        this.txt0 = "Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi Big boi";

        textSet = new HashSet<>();
        this.textSet.add(this.txt1);
        this.textSet.add(this.txt2);
        this.textSet.add(this.txt3);
        this.textSet.add(this.txt4);
        this.textSet.add(this.txt5);
        this.textSet.add(this.txt6);
        this.textSet.add(this.txt7);
        this.textSet.add(this.txt8);
        this.textSet.add(this.txt9);
        this.textSet.add(this.txt0);

        uriSet = new HashSet<>();
        this.uriSet.add(this.uri1);
        this.uriSet.add(this.uri2);
        this.uriSet.add(this.uri3);
        this.uriSet.add(this.uri4);
        this.uriSet.add(this.uri5);
        this.uriSet.add(this.uri6);
        this.uriSet.add(this.uri7);
        this.uriSet.add(this.uri8);
        this.uriSet.add(this.uri9);
        this.uriSet.add(this.uri0);
    }


    private DocumentStoreImpl getStoreWithTextAdded() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt6.getBytes()),this.uri6, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt7.getBytes()),this.uri7, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt8.getBytes()),this.uri8, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt9.getBytes()),this.uri9, DocumentStore.DocumentFormat.TXT);
        return store;
    }

    private DocumentStoreImpl getStoreWithBytesAdded() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt5.getBytes()),this.uri5, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt6.getBytes()),this.uri6, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt7.getBytes()),this.uri7, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt8.getBytes()),this.uri8, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt9.getBytes()),this.uri9, DocumentStore.DocumentFormat.BINARY);
        return store;
    }

    private DocumentStoreImpl getStoreWithTextAddedFirstSix() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt6.getBytes()),this.uri6, DocumentStore.DocumentFormat.TXT);
        return store;
    }

    private void printMemoryCounts(){
        for(String string : this.textSet){
            System.out.println(string + ": " + string.getBytes().length);
        }
    }

//    private URI getURIFromHeap(DocumentStoreImpl store){
//        return ((Document)store.getHeap().remove()).getKey();
//    }

    @Test
    public void deleteBigDocThanReduceLimitAndUndo() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt0.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.deleteDocument(this.uri1);
        store.setMaxDocumentBytes(100);
        try{
            store.undo();
        } catch (IllegalArgumentException e){
            System.out.println("Exception has been caught.");
        }
    }
    @Test
    public void setMaxToZeroToPreventAnyAddition() throws IOException {
        DocumentStoreImpl store = this.getStoreWithBytesAdded();
        store.setMaxDocumentBytes(0);
        assertNull(store.getDocument(this.uri2));
        try{
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        } catch (IllegalArgumentException e){
            System.out.println("Exception caught.");
        }
    }
    /*
    @Test
    public void undoDeletePutsBackCorrectly() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.deleteDocument(this.uri1);
        store.undo();
        assertEquals(this.uri2, this.getURIFromHeap(store));
        assertEquals(this.uri3, this.getURIFromHeap(store));
        assertEquals(this.uri4, this.getURIFromHeap(store));
        assertEquals(this.uri5, this.getURIFromHeap(store));
        assertEquals(this.uri6, this.getURIFromHeap(store));
        assertEquals(this.uri7, this.getURIFromHeap(store));
        assertEquals(this.uri8, this.getURIFromHeap(store));
        assertEquals(this.uri9, this.getURIFromHeap(store));
        assertEquals(this.uri1, this.getURIFromHeap(store));
    }
    @Test
    public void getDocumentUpdatesTime() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.getDocument(this.uri1);
        assertEquals(this.uri2, this.getURIFromHeap(store));
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);

    }
    */
    @Test
    public void putMakesRoomIfSameURIEvenIfOverLimitBytesVersion() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.setMaxDocumentBytes(25);
        Set<URI> set = new HashSet<>(this.uriSet);
        set.remove(this.uri9);
        for(URI uri : set){
            assertNull(store.getDocument(uri));
        }
        assertEquals(this.uri9, store.getDocument(this.uri9).getKey());
        store.putDocument(new ByteArrayInputStream(this.txt6.getBytes()),this.uri6, DocumentStore.DocumentFormat.TXT);
        assertEquals(this.txt6, store.getDocument(this.uri6).getDocumentTxt());
    }

    @Test
    public void putReplacesDocWhenLimitIsReachedBytesVersion() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.setMaxDocumentBytes(25);
        Set<URI> set = new HashSet<>(this.uriSet);
        set.remove(this.uri9);
        for(URI uri : set){
            assertNull(store.getDocument(uri));
        }
        assertEquals(this.uri9, store.getDocument(this.uri9).getKey());
        store.putDocument(new ByteArrayInputStream(this.txt6.getBytes()),this.uri6, DocumentStore.DocumentFormat.TXT);
        assertNull(store.getDocument(this.uri9));
        assertEquals(this.uri6, store.getDocument(this.uri6).getKey());
    }

    @Test
    public void putMakesRoomIfSameURIEvenIfOverLimit() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.setMaxDocumentCount(1);
        Set<URI> set = new HashSet<>(this.uriSet);
        set.remove(this.uri9);
        for(URI uri : set){
            assertNull(store.getDocument(uri));
        }
        assertEquals(this.uri9, store.getDocument(this.uri9).getKey());
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri9, DocumentStore.DocumentFormat.TXT);
        assertEquals(this.txt2, store.getDocument(this.uri9).getDocumentTxt());
    }

    @Test
    public void putReplacesDocWhenLimitIsReached() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.setMaxDocumentCount(1);
        Set<URI> set = new HashSet<>(this.uriSet);
        set.remove(this.uri9);
        for(URI uri : set){
            assertNull(store.getDocument(uri));
        }
        assertEquals(this.uri9, store.getDocument(this.uri9).getKey());
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        assertNull(store.getDocument(this.uri9));
        assertEquals(this.uri2, store.getDocument(this.uri2).getKey());
    }
    /*
    @Test
    public void makeSurePutUpdatesTimesCorrectly() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        assertEquals(this.uri1, this.getURIFromHeap(store));
        assertEquals(this.uri2, this.getURIFromHeap(store));
        assertEquals(this.uri3, this.getURIFromHeap(store));
        assertEquals(this.uri4, this.getURIFromHeap(store));
        assertEquals(this.uri5, this.getURIFromHeap(store));
        assertEquals(this.uri6, this.getURIFromHeap(store));
        assertEquals(this.uri7, this.getURIFromHeap(store));
        assertEquals(this.uri8, this.getURIFromHeap(store));
        assertEquals(this.uri9, this.getURIFromHeap(store));
    }
    @Test
    public void makeSureUndoDeleteAllWithPrefixPutsEverythingBackRight () throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.deleteAllWithPrefix("strin");
        store.undo();
        store.search("doc3");
        store.search("doc4");
        store.search("doc5");
        store.search("doc6");
        store.search("doc7");
        store.search("doc8");
        store.search("doc9");
        assertEquals(1, store.search("doc2").size());
        assertEquals(this.uri1, this.getURIFromHeap(store));
    }

    @Test
    public void makeSureUndoDeleteAllPutsEverythingBackRight () throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.deleteAll("string");
        store.undo();
        store.search("doc3");
        store.search("doc4");
        store.search("doc5");
        store.search("doc6");
        store.search("doc7");
        store.search("doc8");
        store.search("doc9");
        store.search("doc2");
        assertEquals(this.uri1, this.getURIFromHeap(store));

    }
    @Test
    public void makeSureSearchByPrefixProperlyAdjustsTime () throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.searchByPrefix("stri");
        assertEquals(this.uri3, this.getURIFromHeap(store));
    }

    @Test
    public void makeSureSearchProperlyAdjustsTime () throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.search("string");
        assertEquals(this.uri3, this.getURIFromHeap(store));
    }

    @Test
    public void docIsRemovedFromCommandSetAndNoOthers() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.deleteAll("THE");
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.getDocument(this.uri2);
        store.getDocument(this.uri6);
        store.getDocument(this.uri8);
        store.getDocument(this.uri9);
        store.setMaxDocumentCount(4);
        assertNull(store.getDocument(this.uri1));
        store.undo(this.uri3);
        assertNull(store.getDocument(this.uri2));
        store.getDocument(this.uri6);
        store.getDocument(this.uri8);
        store.getDocument(this.uri9);
        assertEquals(this.uri3, this.getURIFromHeap(store));
    }

    @Test
    public void makeRoomForDocWithTheSameURI() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAddedFirstSix();
        store.setMaxDocumentCount(6);
        store.putDocument(new ByteArrayInputStream(this.txt8.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertEquals(this.uri2, ((Document)store.getHeap().remove()).getKey());
    }
    @Test
    public void makeSureCompletelyDeletesOnMemoryOverUse() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAddedFirstSix();
        store.deleteDocument(this.uri1);
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.getDocument(this.uri2);
        store.getDocument(this.uri3);
        store.getDocument(this.uri4);
        store.getDocument(this.uri5);
        store.getDocument(this.uri6);
        store.setMaxDocumentCount(5);
        assertNull(store.getDocument(this.uri1));
        assertEquals(0, store.search("fancy").size());
        try {
            store.undo(this.uri1);
            System.out.println("Oof");
        } catch(IllegalStateException e){
            System.out.println("The exception boi has been caught.");
        }
        assertEquals(this.uri2, this.getURIFromHeap(store));
    }
    @Test
    public void undoDeleteTest() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAddedFirstSix();
        store.deleteDocument(this.uri1);
        store.undo();
        assertEquals(this.uri2, (this.getURIFromHeap(store)));
        assertEquals(this.uri3, (this.getURIFromHeap(store)));
        assertEquals(this.uri4, (this.getURIFromHeap(store)));
        assertEquals(this.uri5, (this.getURIFromHeap(store)));
        assertEquals(this.uri6, (this.getURIFromHeap(store)));
        assertEquals(this.uri1, (this.getURIFromHeap(store)));
    }
    @Test
    public void makeRoomForBigDoc() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAddedFirstSix();
        store.setMaxDocumentBytes(408);
        store.putDocument(new ByteArrayInputStream(this.txt0.getBytes()),this.uri0, DocumentStore.DocumentFormat.TXT);
        HashSet<URI> setWithout0 = new HashSet<>(this.uriSet);
        setWithout0.remove(this.uri0);
        for(URI uri : setWithout0){
            assertNull(store.getDocument(uri));
        }
        assertEquals(this.uri0, ((Document)store.getHeap().remove()).getKey());
    }
    @Test
    public void clearMemoryThroughReducingMaxMemoryCount() throws IOException {
        this.printMemoryCounts();
        DocumentStoreImpl store = this.getStoreWithTextAddedFirstSix();
        store.setMaxDocumentBytes(320);
        store.putDocument(new ByteArrayInputStream(this.txt7.getBytes()),this.uri7, DocumentStore.DocumentFormat.TXT);
        assertEquals(this.uri2, ((Document)store.getHeap().remove()).getKey());
    }

    @Test
    public void clearMemoryThroughReducingMaxDocCount() throws IOException {
        this.printMemoryCounts();
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.setMaxDocumentCount(6);
        assertEquals(this.uri4, ((Document)store.getHeap().remove()).getKey());
    }

    @Test
    public void docLastUseTimeThroughSearchTest() throws IOException {
        DocumentStoreImpl store = this.getStoreWithTextAdded();
        store.search("format");
        store.search("for");
        store.search("doc3");
        assertEquals(this.uri4, ((Document)store.getHeap().remove()).getKey());
    }
    */
    @Test
    public void reheapifyTest(){
        MinHeapImpl<HeapTesterObject> heap = new MinHeapImpl<>();

        HeapTesterObject obj1 = new HeapTesterObject(this.txt11);
        HeapTesterObject obj2 = new HeapTesterObject(this.txt12);
        HeapTesterObject obj3 = new HeapTesterObject(this.txt13);
        HeapTesterObject obj4 = new HeapTesterObject(this.txt14);
        HeapTesterObject obj5 = new HeapTesterObject(this.txt15);
        HeapTesterObject obj6 = new HeapTesterObject(this.txt16);
        HeapTesterObject obj7 = new HeapTesterObject(this.txt17);

        heap.insert(obj3);
        heap.insert(obj7);
        heap.insert(obj4);
        heap.insert(obj5);
        heap.insert(obj1);
        heap.insert(obj6);
        heap.insert(obj2);

        obj4.changeString(this.txt10);
        heap.reHeapify(obj4);
        assertEquals(this.txt10, heap.remove().getString());
    }

    @Test
    public void reheapifyTestV2(){
        MinHeapImpl<HeapTesterObject> heap = new MinHeapImpl<>();

        HeapTesterObject obj1 = new HeapTesterObject(this.txt11);
        HeapTesterObject obj2 = new HeapTesterObject(this.txt12);
        HeapTesterObject obj3 = new HeapTesterObject(this.txt13);
        HeapTesterObject obj4 = new HeapTesterObject(this.txt14);
        HeapTesterObject obj5 = new HeapTesterObject(this.txt15);
        HeapTesterObject obj6 = new HeapTesterObject(this.txt16);
        HeapTesterObject obj7 = new HeapTesterObject(this.txt17);

        heap.insert(obj5);
        heap.insert(obj2);
        heap.insert(obj6);
        heap.insert(obj3);
        heap.insert(obj1);
        heap.insert(obj4);
        heap.insert(obj7);

        assertEquals(obj1, heap.remove());

        heap.insert(obj1);
        obj4.changeString(this.txt10);
        assertEquals(obj1, heap.remove());

        heap.insert(obj1);
        heap.reHeapify(obj4);
        assertEquals(obj4, heap.remove());
    }
}
