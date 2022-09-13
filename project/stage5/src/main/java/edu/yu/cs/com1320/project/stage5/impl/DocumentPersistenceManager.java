package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private File baseDir;

    public DocumentPersistenceManager(File baseDir){
        if(baseDir == null){
            this.baseDir = new File(System.getProperty("user.dir"));
        } else {
            this.baseDir = baseDir;
        }
    }

    private class DocumentSerializer implements com.google.gson.JsonSerializer<DocumentImpl>{
        @Override
        public JsonElement serialize(DocumentImpl document, Type type, JsonSerializationContext jsonSerializationContext) {
            DocumentImpl doc = document;
            Gson gson = new GsonBuilder().create();
            JsonObject obj = new JsonObject();
            if(doc.getDocumentTxt() == null){
                String jsonByteArray = DatatypeConverter.printBase64Binary(doc.getDocumentBinaryData());
                JsonArray array=new JsonArray(10);
                array.add(jsonByteArray);
                obj.add("binaryData", array);
            } else {
                obj.addProperty("text", doc.getDocumentTxt());
                obj.addProperty("wordCounts", gson.toJson(doc.getWordMap()));
            }
            obj.addProperty("uri", doc.getKey().toASCIIString());
            return obj;
        }
    }
    @Override
    public void serialize(URI uri, Document val) throws IOException {
        DocumentSerializer serializer = new DocumentSerializer();
        DocumentImpl doc = (DocumentImpl) val;
        Gson gson = new Gson();
        String jsonString = null;
        JsonElement json = null;
        if(doc.getDocumentBinaryData() == null){
            jsonString = gson.toJson(val);
        } else {
             json = serializer.serialize(doc, null, null);
        }
        String file = this.chopOffTopOfURI(uri);
        File f = new File(file);
        if (!f.getParentFile().exists()){
            f.getParentFile().mkdirs();
        }
        f.createNewFile();
        FileWriter writer = new FileWriter(f);
        if(jsonString != null){
            writer.write(jsonString);
        }
        if(json != null) {
            writer.write(json.toString());
        }
        //writer.write("\n" + gson.toJson(val));
        writer.close();
    }

    private class MapDeserializer implements JsonDeserializer<HashMap<String, Integer>>{

        @Override  @SuppressWarnings("unchecked")
        public HashMap<String, Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return (HashMap<String, Integer>) read(json);
        }

        public Object read(JsonElement in) {

            if(in.isJsonObject()){
                HashMap<String, Integer> map = new HashMap<>();
                JsonObject obj = in.getAsJsonObject();
                Set<HashMap.Entry<String, JsonElement>> entitySet = obj.entrySet();
                for(Map.Entry<String, JsonElement> entry: entitySet){
                    map.put(entry.getKey(), (Integer) read(entry.getValue()));
                }
                return map;
            }else if( in.isJsonPrimitive()){
                JsonPrimitive prim = in.getAsJsonPrimitive();
                Number num = prim.getAsNumber();
                // here you can handle double int/long values
                // and return any type you want
                // this solution will transform 3.0 float to long values
                return num.intValue();
            }
            return null;
        }
    }


    private class DocumentDeserializer implements com.google.gson.JsonDeserializer<DocumentImpl>{
        @Override
        public DocumentImpl deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            if(obj.get("text") == null){
                Gson gson = new Gson();
                URI uri = URI.create(obj.get("uri").getAsString());
                byte[] base64Decoded = DatatypeConverter.parseBase64Binary(String.valueOf(obj.getAsJsonArray("binaryData")));
                DocumentImpl doc = new DocumentImpl(uri, base64Decoded);
                return doc;
            } else {
                Gson gson = new Gson();
                URI uri = URI.create(obj.get("uri").getAsString());
                String str = obj.get("text").getAsString();
                Gson gsonBoi = new GsonBuilder().registerTypeAdapter(new TypeToken <HashMap<String, Integer>>(){}.getType(), new MapDeserializer()).create();
                HashMap<String, Integer> wordMap = gsonBoi.fromJson(obj.get("wordCounts"), new TypeToken <HashMap<String, Integer>>(){}.getType());
                DocumentImpl doc = new DocumentImpl(uri, str, wordMap);
                return doc;
            }
        }
    }
    @Override
    public Document deserialize(URI uri) throws IOException {
        String file = this.chopOffTopOfURI(uri);
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new DocumentDeserializer()).create();
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
        JsonReader jReader = new JsonReader(new StringReader(output.toString()));
        return gson.fromJson(jReader, DocumentImpl.class);
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
        //this.deleteDirectory();
        return myObj.delete();
    }

    private void deleteDirectory() {
        File folder = new File(System.getProperty("user.dir"));
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                deleteEmptyDirectoriesOfFolder(fileEntry);
                if(fileEntry.listFiles().length == 0){
                    fileEntry.delete();
                }
            }
        }

    }

    private void deleteEmptyDirectoriesOfFolder(final File folder) {
        if(folder.listFiles().length == 0){
            folder.delete();
        }else {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    deleteEmptyDirectoriesOfFolder(fileEntry);
                    if(fileEntry.listFiles() == null){
                        fileEntry.delete();
                    }
                }
            }
        }

    }

    private String chopOffTopOfURI(URI uri){
        String scheme = uri.getSchemeSpecificPart();
        if(Character.compare(scheme.charAt(0), '/') == 0 &&  (Character.compare(scheme.charAt(1), '/') == 0)){
            scheme = scheme.substring(1);
        }
        return baseDir + scheme + ".json";
    }
}
