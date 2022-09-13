package edu.yu;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage1.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
public class StageOneUnitTests {

    @Test
    public void nullTXTDocumentConstructor1(){
        assertThrows(IllegalArgumentException.class, () -> new DocumentImpl(null, "AAAAAA"));
    }

    @Test
    public void nullTXTDocumentConstructor2(){
        assertThrows(IllegalArgumentException.class, () -> new DocumentImpl(URI.create("EEE"), (String) null));
    }

    @Test
    public void nullByteDocumentConstructor1(){
        byte[] myvar = "Any String you want".getBytes();
        
    }

    @Test
    public void nullByteDocumentConstructor2(){
        assertThrows(IllegalArgumentException.class, () -> new DocumentImpl(URI.create("EEE"), (String) null));
    }

//    @Test
//    public void hashTableSize() {
//        HashTableImpl<String, String> hashTable = new HashTableImpl<>();
//        assertEquals(5, hashTable.getArrayLength(), "array length should be five");
//    }
//
//    @Test
//    public void genericsCheck() {
//        HashTableImpl<Object, Object> hashTable = new HashTableImpl<>();
//        assertEquals(5, hashTable.getArrayLength(), "array length should be five");
//        HashTableImpl<Integer, Random> hashTable1 = new HashTableImpl<>();
//        assertEquals(5, hashTable1.getArrayLength(), "array length should be five");
//        HashTableImpl<Document, Document> hashTable2 = new HashTableImpl<>();
//        assertEquals(5, hashTable2.getArrayLength(), "array length should be five");
//        HashTableImpl<URI, URI> hashTable3 = new HashTableImpl<>();
//        assertEquals(5, hashTable3.getArrayLength(), "array length should be five");
//        HashTableImpl<Document, URI> hashTable4 = new HashTableImpl<>();
//        assertEquals(5, hashTable.getArrayLength(), "array length should be five");
//    }

    @Test
    public void docStorePutNoPrev() throws IOException {
        String myString = "EEE";
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        InputStream is = new ByteArrayInputStream(myString.getBytes() );
        int zero = docStore.putDocument(is, URI.create(myString), DocumentStore.DocumentFormat.TXT);
        assertEquals(0, zero, "return value should be zero");
    }

    @Test
    public void docStoreGetWithOne() throws IOException {
        String myString = "EEE";
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        InputStream is = new ByteArrayInputStream( myString.getBytes() );
        URI putURI = URI.create(myString);
        int zero = docStore.putDocument(is, putURI, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, zero);
        Document gotDoc = docStore.getDocument(putURI);
        URI gotURI = gotDoc.getKey();
        assertEquals(putURI, gotURI, "URI should be the same as the original URI");
    }

    @Test
    public void separateChaining1(){
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
    }

    @Test
    public void separateChaining2(){
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        hashTable.put(15, "GGGGG");
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        String gggg = hashTable.get(15);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
        assertEquals("GGGGG", gggg);
    }

    @Test
    public void separateChaining3(){
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        hashTable.put(15, "GGGGG");
        hashTable.put(20, "HHHHH");
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        String gggg = hashTable.get(15);
        String hhhh = hashTable.get(20);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
        assertEquals("GGGGG", gggg);
        assertEquals("HHHHH", hhhh);
        hashTable.put(6, "OOOOO");
        hashTable.put(11, "EEEEEE");
        hashTable.put(16, "GGGGG");
        hashTable.put(21, "HHHHH");
        String oooo1 =  hashTable.get(6);
        String eeee1 = hashTable.get(11);
        String gggg1 = hashTable.get(16);
        String hhhh1 = hashTable.get(21);
        assertEquals("OOOOO", oooo1);
        assertEquals("EEEEEE", eeee1);
        assertEquals("GGGGG", gggg1);
        assertEquals("HHHHH", hhhh1);
    }

    @Test
    public void documentConstruction1(){
        URI uri  = URI.create("OOOO");
        DocumentImpl doc = new DocumentImpl(uri,"OOOO" );
        String str = doc.getDocumentTxt();
        URI gotURI = doc.getKey();
        assertEquals("OOOO", str);
        assertEquals(uri, gotURI);
    }

    @Test
    public void documentConstruction2(){
        URI uri  = URI.create("OOOO");
        byte[] myvar = "Any String you want".getBytes();
        DocumentImpl doc = new DocumentImpl(uri,myvar );
        byte[] bytes = doc.getDocumentBinaryData();
        URI gotURI = doc.getKey();
        assertEquals(Arrays.toString(myvar), Arrays.toString(bytes));
        assertEquals(uri, gotURI);
    }

    @Test
    public void putNullDeleteDoc() throws IOException {
        String myString = "EEE";
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        InputStream is = new ByteArrayInputStream( myString.getBytes() );
        URI putURI = URI.create(myString);
        int zero = docStore.putDocument(is, putURI, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, zero);
        assertEquals(myString, docStore.getDocument(putURI).getDocumentTxt());
        int docHash = docStore.getDocument(putURI).hashCode();
        int hash = docStore.putDocument(null, putURI, DocumentStore.DocumentFormat.TXT);
        assertEquals(docHash, hash);
        Document nullDoc = docStore.getDocument(putURI);
        assertNull(nullDoc);
    }

    @Test
    public void putDocExceptions() throws IOException {
        String myString = "EEE";
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        InputStream is = new ByteArrayInputStream( myString.getBytes() );
        URI putURI = URI.create(myString);
        assertThrows(IllegalArgumentException.class, () -> docStore.putDocument(is, putURI, null));
        assertThrows(IllegalArgumentException.class, () -> docStore.putDocument(is, null, DocumentStore.DocumentFormat.TXT));
    }

    @Test
    public void actualDelete() throws IOException {
        String myString = "EEE";
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        InputStream is = new ByteArrayInputStream( myString.getBytes() );
        URI putURI = URI.create(myString);
        int zero = docStore.putDocument(is, putURI, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, zero);
        assertEquals(myString, docStore.getDocument(putURI).getDocumentTxt());
        boolean deleted = docStore.deleteDocument(putURI);
        assertTrue(deleted);
        Document nullDoc = docStore.getDocument(putURI);
        assertNull(nullDoc);
    }

    @Test
    public void separateChainingDelete() throws IOException {
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        hashTable.put(15, "GGGGG");
        hashTable.put(20, "HHHHH");
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        String gggg = hashTable.get(15);
        String hhhh = hashTable.get(20);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
        assertEquals("GGGGG", gggg);
        assertEquals("HHHHH", hhhh);
        String eeeechecked = hashTable.put(10, null);
        assertEquals(eeee, eeeechecked);
        String eeeechecked1 = hashTable.get(10);
        assertNull(eeeechecked1);
        String oooo2 =  hashTable.get(5);
        String gggg2 = hashTable.get(15);
        String hhhh2 = hashTable.get(20);
        assertEquals("OOOOO", oooo2);
        assertEquals("GGGGG", gggg2);
        assertEquals("HHHHH", hhhh2);
    }

    @Test
    public void separateChainingDelete1() throws IOException {
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        hashTable.put(15, "GGGGG");
        hashTable.put(20, "HHHHH");
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        String gggg = hashTable.get(15);
        String hhhh = hashTable.get(20);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
        assertEquals("GGGGG", gggg);
        assertEquals("HHHHH", hhhh);
        String eeeechecked = hashTable.put(15, null);
        assertEquals(gggg, eeeechecked);
        String eeeechecked1 = hashTable.get(15);
        assertNull(eeeechecked1);
    }

    @Test
    public void separateChainingDelete2() throws IOException {
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        hashTable.put(15, "GGGGG");
        hashTable.put(20, "HHHHH");
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        String gggg = hashTable.get(15);
        String hhhh = hashTable.get(20);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
        assertEquals("GGGGG", gggg);
        assertEquals("HHHHH", hhhh);
        String eeeechecked = hashTable.put(5, null);
        assertEquals(oooo, eeeechecked);
        String eeeechecked1 = hashTable.get(5);
        assertNull(eeeechecked1);
    }

    @Test
    public void separateChainingDelete3() throws IOException {
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        hashTable.put(15, "GGGGG");
        hashTable.put(20, "HHHHH");
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        String gggg = hashTable.get(15);
        String hhhh = hashTable.get(20);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
        assertEquals("GGGGG", gggg);
        assertEquals("HHHHH", hhhh);
        String eeeechecked = hashTable.put(20, null);
        assertEquals(hhhh, eeeechecked);
        String eeeechecked1 = hashTable.get(20);
        assertNull(eeeechecked1);
    }

    @Test
    public void separateChainingDelete4() throws IOException {
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        hashTable.put(15, "GGGGG");
        hashTable.put(20, "HHHHH");
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        String gggg = hashTable.get(15);
        String hhhh = hashTable.get(20);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
        assertEquals("GGGGG", gggg);
        assertEquals("HHHHH", hhhh);
        String eeeechecked = hashTable.put(5, null);
        assertEquals(oooo, eeeechecked);
        String eeeechecked1 = hashTable.put(10, null);
        assertEquals(eeee, eeeechecked1);
        String eeeechecked2 = hashTable.put(15, null);
        assertEquals(gggg, eeeechecked2);
        String eeeechecked3 = hashTable.put(20, null);
        assertEquals(hhhh, eeeechecked3);
        String eeeechecked4 = hashTable.get(5);
        assertNull(eeeechecked4);
        String eeeechecked5 = hashTable.get(10);
        assertNull(eeeechecked5);
        String eeeechecked6 = hashTable.get(15);
        assertNull(eeeechecked6);
        String eeeechecked7 = hashTable.get(20);
        assertNull(eeeechecked7);
    }

    @Test
    public void replaceDoc() throws IOException {
        String myString = "EEE";
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        InputStream is = new ByteArrayInputStream(myString.getBytes() );
        Document doc1 = new DocumentImpl(URI.create(myString), myString);
        int hashCode1 = doc1.hashCode();
        URI uri = URI.create(myString);
        int zero = docStore.putDocument(is, uri, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, zero, "return value should be zero");
        String newString = "GGGGGG";
        InputStream isNew = new ByteArrayInputStream(newString.getBytes());
        Document doc2 = new DocumentImpl(URI.create(myString), newString);
        int oldHash = docStore.putDocument(isNew, uri, DocumentStore.DocumentFormat.TXT);
        assertEquals(hashCode1, oldHash, "return value should be zero");
    }

    @Test
    public void deleteNull (){
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        boolean falseCheck = docStore.deleteDocument(null);
        assertFalse(falseCheck);
    }

    @Test
    public void putNewNull(){
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();
        hashTable.put(5, "OOOOO");
        hashTable.put(10, "EEEEEE");
        hashTable.put(15, null);
        String oooo =  hashTable.get(5);
        String eeee = hashTable.get(10);
        String gggg = hashTable.get(15);
        assertEquals("OOOOO", oooo);
        assertEquals("EEEEEE", eeee);
        assertNull(gggg);
    }

    @Test
    public void nonExistentURI() throws IOException {
        String myString = "EEE";
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        InputStream is = new ByteArrayInputStream(myString.getBytes() );
        int zero = docStore.putDocument(is, URI.create(myString), DocumentStore.DocumentFormat.TXT);
        Document doc = docStore.getDocument(URI.create("AAA"));
        assertNull(doc);
    }

    @Test
    public void spaceStringDoc(){
        assertThrows(IllegalArgumentException.class, () -> new DocumentImpl(URI.create("EEE"),  "    "));
    }
}
