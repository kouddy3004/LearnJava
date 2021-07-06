package reUsables;

import HTTPClient.ParseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class JsonFileHandler {
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    static JsonFileHandler obj = new JsonFileHandler();
    ;

    private JsonFileHandler() {
    }

    public static JsonFileHandler on() {
        return obj;
    }

    public String readValueFromJson(String jsonPath, String key) {
        String values = "";
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(jsonPath)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            if(jsonObject.get(key)!=null) {
                values = jsonObject.get(key).toString();
            }
        } catch (Exception e) {
            APP_LOGS.info(e.getMessage());
            e.printStackTrace();
        }
        return values;
    }

    public HashMap<String, String> readValueFromJsoninMap(String jsonPath, String key) {
        HashMap<String, String> values = new HashMap<>();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(jsonPath)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            values = (HashMap<String, String>) jsonObject.get(key);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public void writeIntoJson(HashMap<String, String> input, String path) {
        try (Writer writer = new FileWriter(path)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(input, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> readValueFromJsoninMap(String jsonPath) {
        HashMap<String, String> values = new HashMap<>();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(jsonPath)) {
            //Read JSON file
            if (Files.size(Paths.get(jsonPath)) > 0) {
                Object obj = jsonParser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;
                values = (HashMap<String, String>) jsonObject;
            }
            else{
                values.put("error","File is empty");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }



    public Object parseJson(org.json.JSONObject jsonObject, String jsonField) throws ParseException {

        Object fieldname = null;
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String obj = iterator.next();
            if (jsonObject.get(obj) instanceof JSONArray) {
                System.out.println("Here1:" + obj.toString());
                getArray(jsonObject.get(obj), jsonField);
            } else {
                if (jsonObject.get(obj) instanceof org.json.JSONObject) {
                    parseJson((org.json.JSONObject) jsonObject.get(obj), jsonField);
                } else {
                    System.out.println("Here3:" + obj.toString() + "**" + "\t" + jsonObject.get(obj));
                }
            }

            System.out.println("obj:" + obj.toString());
            if (obj.toString().equalsIgnoreCase(jsonField)) {

                fieldname = jsonObject.get(obj);
                System.out.println("fieldname:" + fieldname);
            }
        }
        return fieldname;
    }

    private void getArray(Object object2, String jsonField) throws ParseException {

        JSONArray jsonArr = (JSONArray) object2;

        for (int k = 0; k < jsonArr.length(); k++) {

            if (jsonArr.get(k) instanceof org.json.JSONObject) {
                parseJson((org.json.JSONObject) jsonArr.get(k), jsonField);
            } else {
                System.out.println(jsonArr.get(k));
            }

        }
    }
}
