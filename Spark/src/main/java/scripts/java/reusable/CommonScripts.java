package scripts.java.reusable;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.util.*;

public class CommonScripts {

    public List deepCloneListOfHashMap(List<HashMap<String ,String>> source){
        List<HashMap<String,String>> result= new ArrayList<>();
        Iterator iterator=source.iterator();
        while(iterator.hasNext()){
            HashMap<String,String> mapp= (HashMap<String, String>) iterator.next();
            HashMap<String,String > tempMap=new HashMap<>();
            for(Map.Entry<String,String> entry: mapp.entrySet()){
                tempMap.put(entry.getKey(),entry.getValue());
            }
            result.add(tempMap);
        }
        return result;
    }

    public int getLatsRow(String filePath) throws Exception{
        File file = new File(filePath);
        int n_lines = 10;
        int counter = 0;
        if(file.exists()) {
            ReversedLinesFileReader object = new ReversedLinesFileReader(file);
            while (object.readLine()!=null) {
                counter++;
            }
        }
        return counter;
    }
}
