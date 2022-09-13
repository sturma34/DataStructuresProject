package edu.yu.cs.com1320.project.stage2;

import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage2.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreImplTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    String txt2;

    private URI uri3;
    String txt3;

    private URI uri4;
    String txt4;

    private URI uri5;
    String txt5;

    private URI uri6;
    String txt6;

    private URI uri7;
    String txt7;

    private URI uri8;
    String txt8;

    private URI uri9;
    String txt9;

    private URI uri10;
    String txt10;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This is the text of doc1, in plain text. No fancy file format - just plain old String";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2. A plain old String.";

        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "Text for doc3. A plain old String.";

        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "Text for doc4. A plain old String.";

        this.uri5 = new URI("http://edu.yu.cs/com1320/project/doc5");
        this.txt5 = "Text for doc5. A plain old String.";

        this.uri6 = new URI("http://edu.yu.cs/com1320/project/doc6");
        this.txt6 = "Text for doc6. A plain old String.";

        this.uri7 = new URI("http://edu.yu.cs/com1320/project/doc7");
        this.txt7 = "Text for doc7. A plain old String.";

        this.uri8 = new URI("http://edu.yu.cs/com1320/project/doc8");
        this.txt8 = "Text for doc8. A plain old String.";

        this.uri9 = new URI("http://edu.yu.cs/com1320/project/doc9");
        this.txt9 = "Text for doc9. A plain old String.";

        this.uri10 = new URI("http://edu.yu.cs/com1320/project/doc10");
        this.txt10 = "Text for doc10. A plain old String.";
    }
    @Test public void testNonExistentURIUndo() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int returned1 = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int returned2 = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int returned3 = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int returned4 = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertThrows(IllegalStateException.class, () -> store.undo(uri5));
    }

    @Test public void testUndoOverwrite() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int returned1 = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int returned2 = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int returned3 = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int returned4 = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        store.undo(uri1);
        Document doc = store.getDocument(uri1);
        byte[] data = doc.getDocumentBinaryData();
        byte[] ogdata = this.txt1.getBytes();
        for (int i = 0; i < data.length; i++){
            assertEquals(ogdata[i], data[i]);
        }
        store.undo(uri1);
        Document doc2 = store.getDocument(uri1);
        assertNull(doc2);
    }

    @Test
    public void testUndoPut() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.undo();
        Document doc = store.getDocument(uri1);
        assertNull(doc);
    }

    @Test
    public void testTwoStepUndoPut() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int returned1 = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.undo(uri1);
        Document doc = store.getDocument(uri1);
        assertNull(doc);
        Document doc2 = store.getDocument(uri2);
        URI gotURI = doc2.getKey();
        assertEquals(uri2, gotURI);
    }

    @Test
    public void testUndoDelete() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.deleteDocument(uri1);
        Document doc = store.getDocument(uri1);
        assertNull(doc);
        store.undo();
        Document doc1 = store.getDocument(uri1);
        URI gotURI = doc1.getKey();
        assertEquals(uri1, gotURI);
    }

    @Test
    public void testMultiLayerUndoDelete() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        int returned1 = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int returned2 = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int returned3 = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int returned4 = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        store.deleteDocument(uri1);
        Document doc = store.getDocument(uri1);
        assertNull(doc);
        int returned5 = store.putDocument(new ByteArrayInputStream(this.txt5.getBytes()),this.uri5, DocumentStore.DocumentFormat.BINARY);
        int returned6 = store.putDocument(new ByteArrayInputStream(this.txt6.getBytes()),this.uri6, DocumentStore.DocumentFormat.BINARY);
        int returned7 = store.putDocument(new ByteArrayInputStream(this.txt7.getBytes()),this.uri7, DocumentStore.DocumentFormat.BINARY);
        store.deleteDocument(uri7);
        Document doc7 = store.getDocument(uri7);
        assertNull(doc7);
        int returned8 = store.putDocument(new ByteArrayInputStream(this.txt8.getBytes()),this.uri8, DocumentStore.DocumentFormat.BINARY);
        int returned9 = store.putDocument(new ByteArrayInputStream(this.txt9.getBytes()),this.uri9, DocumentStore.DocumentFormat.BINARY);
        int returned10 = store.putDocument(new ByteArrayInputStream(this.txt10.getBytes()),this.uri10, DocumentStore.DocumentFormat.BINARY);
        store.undo(uri1);
        store.undo(uri7);
        Document doc1b = store.getDocument(uri1);
        Document doc7b =store.getDocument(uri7);
        URI gotURI1 = doc1b.getKey();
        URI gotURI7 = doc7b.getKey();
        assertEquals(uri1, gotURI1);
        assertEquals(uri7, gotURI7);
        store.undo(uri1);
        store.undo(uri7);
        Document doc1c = store.getDocument(uri1);
        Document doc7c =store.getDocument(uri7);
        assertNull(doc1c);
        assertNull(doc7c);
    }

    @Test
    public void testPutBinaryDocumentNoPreviousDocAtURI() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        assertTrue(returned == 0);
    }

    @Test
    public void testPutTxtDocumentNoPreviousDocAtURI() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
    }

    @Test
    public void testPutDocumentWithNullArguments() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        try {
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), null, DocumentStore.DocumentFormat.TXT);
            fail("null URI should've thrown IllegalArgumentException");
        }catch(IllegalArgumentException e){}
        try {
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, null);
            fail("null format should've thrown IllegalArgumentException");
        }catch(IllegalArgumentException e){}
    }

    @Test
    public void testPutNewVersionOfDocumentBinary() throws IOException {
        //put the first version
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        assertTrue(returned == 0);
        Document doc1 = store.getDocument(this.uri1);
        assertArrayEquals(this.txt1.getBytes(),doc1.getDocumentBinaryData(),"failed to return correct binary text");

        //put the second version, testing both return value of put and see if it gets the correct text
        int expected = doc1.hashCode();
        returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);

        assertEquals(expected, returned,"should return hashcode of the old document");
        assertArrayEquals(this.txt2.getBytes(),store.getDocument(this.uri1).getDocumentBinaryData(),"failed to return correct data");
    }

    @Test
    public void testPutNewVersionOfDocumentTxt() throws IOException {
        //put the first version
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
        assertEquals(this.txt1,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");

        //put the second version, testing both return value of put and see if it gets the correct text
        returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(Utils.calculateHashCode(this.uri1, this.txt1,null) == returned,"should return hashcode of old text");
        assertEquals(this.txt2,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");
    }

    @Test
    public void testGetTxtDoc() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
        assertEquals(this.txt1,store.getDocument(this.uri1).getDocumentTxt(),"did not return a doc with the correct text");
    }

    @Test
    public void testGetTxtDocAsBinary() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
        assertNull(store.getDocument(this.uri1).getDocumentBinaryData(),"a text doc should return null for binary");
    }

    @Test
    public void testGetBinaryDocAsBinary() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(returned == 0);
        assertArrayEquals(this.txt2.getBytes(),store.getDocument(this.uri2).getDocumentBinaryData(),"failed to return correct binary array");
    }

    @Test
    public void testGetBinaryDocAsTxt() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(returned == 0);
        assertNull(store.getDocument(this.uri2).getDocumentTxt(),"binary doc should return null for text");
    }

    @Test
    public void testDeleteDoc() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.deleteDocument(this.uri1);
        assertNull(store.getDocument(this.uri1),"calling get on URI from which doc was deleted should've returned null");
    }

    @Test
    public void testDeleteDocReturnValue() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //should return true when deleting a document
        assertEquals(true,store.deleteDocument(this.uri1),"failed to return true when deleting a document");
        //should return false if I try to delete the same doc again
        assertEquals(false,store.deleteDocument(this.uri1),"failed to return false when trying to delete that which was already deleted");
        //should return false if I try to delete something that was never there to begin with
        assertEquals(false,store.deleteDocument(this.uri2),"failed to return false when trying to delete that which was never there to begin with");
    }
}