package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BTreeImplTest {

    private BTree<URI,String> tree;
    private URI uri1;
    private URI uri2;
    private URI uri3;
    private URI uri4;
    private URI uri5;
    private URI uri6;

    @BeforeEach
    public void initTable() throws URISyntaxException {
        this.tree = new BTreeImpl<>();
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.uri5 = new URI("http://edu.yu.cs/com1320/project/doc5");
        this.uri6 = new URI("http://edu.yu.cs/com1320/project/doc6");


        this.tree.put(this.uri1, "Value1");
        this.tree.put(this.uri2,"Value2");
        this.tree.put(this.uri3,"Value3");
        this.tree.put(this.uri4,"Value4");
        this.tree.put(this.uri5,"Value5");
        this.tree.put(this.uri6,"Value6");
    }

    @Test
    public void testGenericPM() throws Exception {
        this.initTable();
        this.tree.setPersistenceManager(new StringPersistenceManager(new File(System.getProperty("user.dir"))));
        this.tree.moveToDisk(this.uri1);
        assert Files.exists(Path.of(System.getProperty("user.dir") + "/edu.yu.cs/com1320/project/doc1.json"));
        this.tree.get(this.uri1);
        assertFalse(Files.exists(Path.of(System.getProperty("user.dir") + "/www.yu.edu/documents/boi/for/doc1.json")));
    }

    @Test
    public void testGet() {
        assertEquals("Value1",this.tree.get(this.uri1));
        assertEquals("Value2",this.tree.get(this.uri2));
        assertEquals("Value3",this.tree.get(this.uri3));
        assertEquals("Value4",this.tree.get(this.uri4));
        assertEquals("Value5",this.tree.get(this.uri5));
    }
    @Test
    public void testGetChained() {
        //second node in chain
        assertEquals("Value6",this.tree.get(this.uri6));
        //second node in chain after being modified
        this.tree.put(this.uri6,"Value6+1");
        assertEquals("Value6+1",this.tree.get(this.uri6));
        //check that other values still come back correctly
        testGet();
    }
    @Test
    public void testGetMiss() throws URISyntaxException {
        assertEquals(null,this.tree.get(new URI("Bah")));
    }
    @Test
    public void testPutReturnValue() throws URISyntaxException {
        assertEquals("Value3",this.tree.put(this.uri3,"Value3+1"));
        assertEquals("Value6",this.tree.put(this.uri6, "Value6+1"));
        assertEquals(null,this.tree.put(new URI("EEE"),"Value7"));
    }
    @Test
    public void testGetChangedValue () {
        BTreeImpl<String, String> table = new BTreeImpl<String, String>();
        String key1 = "hello";
        String value1 = "how are you today?";
        String value2 = "HI!!!";
        table.put(key1, value1);
        assertEquals(value1,table.get(key1));
        table.put(key1, value2);
        assertEquals(value2,table.get(key1));
    }
    @Test
    public void testDeleteViaPutNull() {
        BTreeImpl<String, String> table = new BTreeImpl<String, String>();
        String key1 = "hello";
        String value1 = "how are you today?";
        String value2 = null;
        table.put(key1, value1);
        table.put(key1, value2);
        assertEquals(value2,table.get(key1));
    }
    @Test
    public void testSeparateChaining () {
        BTreeImpl<Integer, String> table = new BTreeImpl<Integer, String>();
        for(int i = 0; i <= 23; i++) {
            table.put(i, "entry " + i);
        }
        assertEquals("entry 12",table.put(12, "entry 12+1"));
        assertEquals("entry 12+1",table.get(12));
        assertEquals("entry 23",table.get(23));
    }

    /**
     * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
     */
    public class StringPersistenceManager implements PersistenceManager<URI, String> {

        private File baseDir;

        public StringPersistenceManager(File baseDir){
            if(baseDir == null){
                this.baseDir = new File(System.getProperty("user.dir"));
            } else {
                this.baseDir = baseDir;
            }
        }

        @Override
        public void serialize(URI uri, String str) throws IOException {
            Gson gson = new Gson();
            String file = this.chopOffTopOfURI(uri);
            File f = new File(file);
            if (!f.getParentFile().exists()){
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(gson.toJson(str));
            //writer.write("\n" + gson.toJson(val));
            writer.close();
        }


        @Override
        public String deserialize(URI uri) throws IOException {
            Gson gson = new Gson();
            String file = this.chopOffTopOfURI(uri);
            FileReader reader;
            try {
                reader = new FileReader(file);
            }catch(FileNotFoundException e){
                return null;
            }
            StringBuilder output = new StringBuilder();
            int i;
            while((i=reader.read())!=-1){
                output = new StringBuilder(output.append((char) i));
            }
            return gson.fromJson(output.toString(), String.class);
        }

        @Override
        public boolean delete(URI uri) throws IOException {
            String scheme = uri.getSchemeSpecificPart();
            if(Character.compare(scheme.charAt(0), '/') == 0 &&  (Character.compare(scheme.charAt(1), '/') == 0)){
                scheme = scheme.substring(1);
            }
            int index = scheme.indexOf("/", scheme.indexOf("/") + 1) + 1;
            String subString = scheme.substring(0, index);
            //String file = baseDir + subString;
            String file = this.chopOffTopOfURI(uri);
            File myObj = new File(file);
            //this.deleteDirectory(myObj);
            return myObj.delete();
        }


        private String chopOffTopOfURI(URI uri){
            String scheme = uri.getSchemeSpecificPart();
            if(Character.compare(scheme.charAt(0), '/') == 0 &&  (Character.compare(scheme.charAt(1), '/') == 0)){
                scheme = scheme.substring(1);
            }
            return baseDir + scheme + ".json";
        }
    }

}