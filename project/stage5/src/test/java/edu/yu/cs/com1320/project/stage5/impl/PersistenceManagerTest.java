package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class PersistenceManagerTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;

    //variables to hold possible values for doc2
    private URI uri3;
    private String txt3;

    //variables to hold possible values for doc2
    private URI uri4;
    private String txt4;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://www.yu.edu/documents/boi/for/doc1");
        this.txt1 = "the text of doc1, in plain text. No fancy file format - just plain old String. Computer. Headphones.";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2. A plain old String.";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "the text of doc3, this is";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "This is the text of doc4";
    }

    @Test
    public void serializeTest() throws IOException {
        DocumentImpl doc = new DocumentImpl(this.uri1, this.txt1, null);
        DocumentPersistenceManager boi = new DocumentPersistenceManager(null);
        boi.serialize(this.uri1, doc);
        assert Files.exists(Path.of(System.getProperty("user.dir") + "/www.yu.edu/documents/boi/for/doc1.json"));
        boi.delete(this.uri1);
        assertFalse(Files.exists(Path.of(System.getProperty("user.dir") + "/www.yu.edu/documents/boi/for/doc1.json")));
    }

    @Test
    public void deSerializeStringTest() throws IOException {
        DocumentImpl doc = new DocumentImpl(this.uri1, this.txt1, null);
        DocumentPersistenceManager boi = new DocumentPersistenceManager(null);
        boi.serialize(this.uri1, doc);
        Document returnedDoc = boi.deserialize(this.uri1);
        assertEquals(doc, returnedDoc);
        boi.delete(this.uri1);
    }

    @Test
    public void deSerializeByteArrayTests() throws IOException{
        DocumentImpl doc = new DocumentImpl(this.uri1, this.txt1.getBytes());
        DocumentPersistenceManager boi = new DocumentPersistenceManager(null);
        boi.serialize(this.uri1, doc);
        Document returnedDoc = boi.deserialize(this.uri1);
        assertEquals(doc, returnedDoc);
        boi.delete(this.uri1);
    }

    @Test
    public void deserializeByteArray2() throws IOException, URISyntaxException {
        DocumentImpl doc = new DocumentImpl(this.uri2, this.txt2.getBytes());
        DocumentPersistenceManager boi = new DocumentPersistenceManager(new File(("/Users/akivasturm/COM/Sturm_Akiva_3479316506/DataStructures/project/stage5/test/long")));
        boi.serialize(this.uri2, doc);
        Document returnedDoc = boi.deserialize(this.uri2);
        assertEquals(doc, returnedDoc);
        boi.delete(this.uri2);

    }


}
