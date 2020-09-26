package scripts.java.reusable;




import com.csvreader.CsvReader;
import scripts.java.code.DataExtrapolation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ThreadForCSvWrite {
    List<HashMap<String, String>> data = new ArrayList<>();
    CommonScripts cs = new CommonScripts();
    String fileName = "";
    int startLoop = 0;
    int endLoop = 0;
    String newPolicy = "";
    CsvFileHandler csv;

    public ThreadForCSvWrite(List<HashMap<String, String>> data, String fileName, int startLoop, int endLoop, String newPolicy) {
        this.fileName = fileName;
        this.data = cs.deepCloneListOfHashMap(data);
        this.startLoop = startLoop;
        this.endLoop = endLoop;
        csv = new CsvFileHandler();
        this.newPolicy = newPolicy;
    }

    public void run() {
        boolean check = false;
        try {
            for (int i = startLoop; i <= endLoop; i++) {
                if (i >= DataExtrapolation.startLoop) {
                    check = true;
                    List<HashMap<String, String>> copyValues = new ArrayList<>();
                    copyValues = cs.deepCloneListOfHashMap(data);
                    for (int row = 0; row < data.size(); row++) {
                        if (fileName.toUpperCase().contains("STG_LIFE_INS_POLICY_TXNS")
                                || fileName.toUpperCase().contains("STG_ANNUITY_TXNS")
                                || fileName.toUpperCase().contains("STG_RETIREMENT_ACCOUNTS_TXNS")) {
                            String txn = data.get(row).get("v_txn_ref_no".toUpperCase());
                            copyValues.get(row).put("v_account_number".toUpperCase(), newPolicy + Integer.toString(i));
                            copyValues.get(row).put("v_txn_ref_no".toUpperCase(), "ext_reins_" + txn + "_" + Integer.toString(i));
                        } else if (fileName.toUpperCase().contains("STG_INS_POLICY_CASH_FLOW")) {
                            copyValues.get(row).put("v_policy_code".toUpperCase(), newPolicy + Integer.toString(i));
                        } else if (fileName.toUpperCase().contains("STG_POLICY_COVERAGES")) {
                            copyValues.get(row).put("v_account_number".toUpperCase(), newPolicy + Integer.toString(i));
                        }

                    }
                    String csvFile = fileName + "_" + startLoop + "_" + endLoop + ".csv";
              /*  if (csv.checkPath(DataExtrapolation.databank+fileName+"\\"+csvFile)) {
                    csv.createCsv(DataExtrapolation.databank+fileName+"\\"+csvFile, copyValues, false);
                }*/
                    if (csv.checkPath(DataExtrapolation.databank + fileName + "\\" + fileName + ".csv")) {
                        csv.createCsv(DataExtrapolation.databank + fileName + "\\" + fileName + ".csv", copyValues, false);
                    }
                    if (i == DataExtrapolation.startLoop) {
                        loadCtlFIle();
                    }
               /* System.out.println("For "+Thread.currentThread().getName()+" : "
                        +copyValues.get(i).get("v_policy_code".toUpperCase()));*/
                }
            }
            if (check) {
                System.out.println("Finished " + Thread.currentThread().getName()
                        + " and for Partition " + startLoop + " and " + endLoop + " at " + java.time.LocalTime.now());
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void loadCtlFIle() throws Exception {
        String path = DataExtrapolation.databank + fileName + "\\loadSql.ctl";
        String entry = "OPTIONS (SKIP=1) \n" +
                "LOAD DATA \n" +
                "INFILE '/scratch/qauser17/DataExtrapolAtion/STG_INS_POLICY_CASH_FLOW/" + fileName + ".csv'\n" +
                "into table STG_INS_POLICY_CASH_FLOW_spark\n" +
                "FIELDS TERMINATED BY ','  OPTIONALLY ENCLOSED BY '\"'\n" +
                "TRAILING NULLCOLS \n" +
                "(\n";
        FileWriter fr = new FileWriter(new File(path), true);
        BufferedReader brTest = new BufferedReader(new FileReader(DataExtrapolation.databank + fileName + "\\" + fileName + ".csv"));
        String text = brTest.readLine().replaceAll("\"","");
        text.replaceAll(",",",\n");
        entry=entry+text+"\n)";
        try {
            CsvFileHandler csv = new CsvFileHandler();
            if (csv.checkPath(path)) {
                fr.write(entry);
                fr.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            fr.close();
            brTest.close();
        }
    }

}
