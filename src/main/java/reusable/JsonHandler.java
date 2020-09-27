package reusable;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;

public class JsonHandler {

    public String readJsonasString(String filePath,String key){
        String jsonValues="";
        JSONParser jsonParser = new JSONParser();
        if(!filePath.contains(".json")){
            filePath=filePath+".json";
        }
        try (FileReader reader = new FileReader(filePath))
        {
            //Read JSON file
            Object obj =jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            jsonValues= (String.valueOf(jsonObject.get(key)));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonValues;
    }

}
