package reUsables;


import dbAutomation.IFRS17.dbOperations.CohortTables;
import dbAutomation.IFRS17.dbOperations.PolicyTables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThreadForCSvWrite {
    List<HashMap<String, String>> data = new ArrayList<>();

    public static String fileName = "";
    public static int startLoop = 0;
    public static int endLoop = 0;
    public  static String newPolicy = "";

    public ThreadForCSvWrite(List<HashMap<String, String>> data, String fileName, int startLoop, int endLoop, String newPolicy) {
        this.fileName = fileName;
        this.data = CommonScripts.on().deepCloneListOfHashMap(data);
        this.startLoop = startLoop;
        this.endLoop = endLoop;
        this.newPolicy = newPolicy;
    }

    public boolean run() {
        boolean check=false;
        try {
            if(fileName.toUpperCase().contains("GROUP")||fileName.toUpperCase().contains("COHORT")){
                CohortTables cohort=new CohortTables();
                check=cohort.assignTables(data);

            }
            else{
                PolicyTables policy=new PolicyTables();
                policy.assignTables(data);
            }


        } catch (Exception e) {
            e.getMessage();
        }
        return check;
    }


    public static void loadCtlFIle(String tableName) throws Exception {
        CsvFileHandler csv = new CsvFileHandler();
        System.out.println("INside ctl Loader");
        String path = MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + tableName + "\\loadTable.ctl";
        String entry = "OPTIONS (SKIP=1) \n" +
                "LOAD DATA \n" +
                "INFILE '/scratch/$USER/DataExtrapolAtion/" + tableName + "/" + tableName + ".csv'\n" +
                "into table " + tableName + "\n" +
                "APPEND\n" +
                "FIELDS TERMINATED BY ','  OPTIONALLY ENCLOSED BY '\"'\n" +
                "TRAILING NULLCOLS \n" +
                "(\n";
        FileWriter fr = new FileWriter(new File(path), true);
        BufferedReader brTest = new BufferedReader(new FileReader(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + tableName + "\\" + tableName + ".csv"));
        String text = brTest.readLine().replaceAll("\"", "");
        entry = entry + text + "\n)";
        try {
            if (csv.checkPath(path)) {
                fr.write(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fr.close();
            brTest.close();
        }
        System.out.println("Ctl File Has been Created " + path);
    }
}
