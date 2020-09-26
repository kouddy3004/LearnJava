package scripts.java.reusable;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class CsvFileHandler {

    String path = System.getProperty("user.dir") + "\\src\\main\\java";


    public Boolean checkPath(String filePath) throws Exception{
        Boolean check = false;
        if(!filePath.contains(System.getProperty("user.dir")+"\\src\\main\\java")) {
            filePath = path;
        }
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String paths[] = filePath.split(pattern);
        String folPath="";
        for (String folder : paths) {
            if(folPath.isEmpty()){
                folPath=folder;
            }
            else {
                folPath = folPath + "\\" + folder;
            }
            File file = new File(folPath);
            if (file.exists()==false && folder.contains(".")==false) {
                System.out.println(folder);
                file.mkdir();
            }
            else if(folder.contains(".")){
                file.createNewFile();
            }
            if(file.exists()){
                check=true;
            }
            else{
                check=false;
            }

        }


        return check;
    }

    public Boolean deletePath(String filePath) {
        Boolean check = false;
        if(!filePath.contains(System.getProperty("user.dir")+"\\src\\main\\java")) {
            filePath = path;
        }
        String paths[] = filePath.split("/");
        for (String folder : paths) {
            File file = new File(folder);
            if (folder.contains(".csv")&&file.exists()) {
                file.delete();
                check = true;
            }
        }

        return check;
    }





    public static CsvFileHandler on() {
        return new CsvFileHandler();
    }

    public void createCsv(String filePath, List<HashMap<String, String>> data,Boolean replace) {
        File file = new File(filePath);
        int startFrom=0;
        try {
            CSVWriter csvWriter = null;
            if(replace) {
                file.createNewFile();
                csvWriter = new CSVWriter(new FileWriter(file));
            }
            else{
                csvWriter = new CSVWriter(new FileWriter(file,true));
            }
            if(file.length()<=0) {
                String[] header = getHeaders(data);
                csvWriter.writeNext(header);
            }
            for (int row = 0; row < data.size(); row++) {
                String[] data1 = getValues(data.get(row));
                csvWriter.writeNext(data1);
            }

            csvWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getHeaders(List<HashMap<String, String>> data) {
        List<String> headerList = new ArrayList<>();
        for (Map.Entry<String, String> entry : data.get(0).entrySet()) {
            headerList.add(entry.getKey());
        }
        String[] headers = new String[headerList.size()];
        for (int row = 0; row < headerList.size(); row++) {
            headers[row] = headerList.get(row);
        }
        return headers;
    }

    private String[] getValues(HashMap<String, String> data) {
        Collection<String> setvalues = data.values();
        ArrayList<String> listOfValues = new ArrayList<String>(setvalues);
        String[] values = new String[listOfValues.size()];
        for (int row = 0; row < listOfValues.size(); row++) {
            values[row] = listOfValues.get(row);
        }
        return values;
    }

    public String compareCsv(String sourcePath, String targetPath, String outputPath) {
        String status = "PASS";
        String[] checkStatus = {"PASS", "PASS"};
        try {
            List<HashMap<String, String>> errorList = new ArrayList<>();
            Reader source = Files.newBufferedReader(Paths.get(sourcePath));
            CSVReader csvReader = new CSVReader(source);
            List<String[]> sourceList = csvReader.readAll();
            Reader target = Files.newBufferedReader(Paths.get(targetPath));
            csvReader = new CSVReader(target);
            List<String[]> targetList = csvReader.readAll();
            if (checkPath(outputPath)) {
                HashMap<String, String> errorMap = new HashMap<>();
                for (int row = 0; row < sourceList.size(); row++) {
                    int errSize = 1;
                    for (int column = 0; column < sourceList.get(0).length; column++) {
                        if (!sourceList.get(row)[column].equals(targetList.get(row)[column])) {
                            errorMap.put(sourceList.get(row)[0] + "- ErrorNo : " + errSize,
                                    "Source : " + sourceList.get(0)[column] + " --> " + sourceList.get(row)[column] +
                                            "\nTarget : " + targetList.get(0)[column] + " --> " + targetList.get(row)[column]);
                            errSize = errSize + 1;

                            checkStatus[1] = "FAIL";
                        } else {
                            if (row > 0) {

                            }
                        }
                        errorList.add(errorMap);
                    }
                }

            }
            if (checkStatus[1].equalsIgnoreCase("fail")) {
                Set<HashMap> s = new LinkedHashSet<HashMap>(errorList);
                errorList.clear();
                for (HashMap x : s) errorList.add(x);
                status = "FAIL";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public String[] listToArray(List list) {
        String[] array = new String[list.size()];
        for (int row = 0; row < list.size(); row++) {
            array[row] = list.get(row).toString();
        }
        return array;
    }


}
