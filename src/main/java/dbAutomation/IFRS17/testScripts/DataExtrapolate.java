package dbAutomation.IFRS17.testScripts;

import com.relevantcodes.extentreports.LogStatus;
import reUsables.ThreadForCSvWrite;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import reUsables.CsvFileHandler;
import reUsables.DbService;
import reUsables.MasterDriver;
import webAutomation.functionLib.CommonScripts;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataExtrapolate {
    static DataExtrapolate exp = new DataExtrapolate();
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

    private DataExtrapolate() {
    }

    public static DataExtrapolate on() {
        return exp;
    }

    public boolean extraPolateData() {
        boolean status = false;
        MasterDriver.test.log(LogStatus.INFO, "Started For Table " + MasterDriver.testData.get("TableName"));
        String resultPath = "";
        resultPath = MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + MasterDriver.testData.get("TableName") + "\\";
        int endLoop = Integer.parseInt(MasterDriver.testData.get("End Loop"));
        int getPartiotion = Integer.parseInt(MasterDriver.testData.get("Partition BY"));
        int partitionstart = 1;
        int partitionEnd = 0;
        try {
            if (endLoop % getPartiotion == 0) {
                File file = new File(resultPath);
                if (file.exists()) {
                    FileUtils.cleanDirectory(new File(resultPath));
                }
                System.out.println(resultPath);
                System.out.println("Working for " + Integer.toString(endLoop - (Integer.parseInt(MasterDriver.testData.get("Start Loop")) - 1)) + " Reps");
                MasterDriver.test.log(LogStatus.INFO, "Working for " + endLoop + " Reps");
                String query = "select * from " + MasterDriver.testData.get("TableName") + " "
                        + MasterDriver.testData.get("Where Clause");
                List<HashMap<String, String>> dbValues = DbService.on().select(MasterDriver.conn, query, "dd-MMM-yy");
                System.out.println("No of Values fetched from DB is " + dbValues.size() + " at " + java.time.LocalTime.now());
                MasterDriver.test.log(LogStatus.INFO, "No of Values fetched from DB is " + dbValues.size() + " at " + java.time.LocalTime.now());
                List<HashMap<String, String>> repliCaValues = new ArrayList<>();
                //ThreadForCSvWrite[] threads=new ThreadForCSvWrite[endLoop/getPartiotion];
                if (dbValues.size() > 0 && dbValues.get(0).containsKey("error") == false) {
                    CsvFileHandler csv = new CsvFileHandler();
                    for (int i = 1; i <= endLoop / getPartiotion; i++) {
                        if (partitionEnd < endLoop) {
                            int setPartition = getPartiotion;
                            if ((endLoop / setPartition) > setPartition) {
                                setPartition = endLoop / setPartition;
                            }
                            partitionEnd = i * (setPartition);
                            if (partitionEnd >= Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                                System.out.println("Partition Started For " + i);
                                ThreadForCSvWrite thread = new ThreadForCSvWrite(dbValues, MasterDriver.testData.get("TableName"),
                                        partitionstart, partitionEnd, MasterDriver.testData.get("Replacing Policy"));
                                thread.run();
                            }
                            partitionstart = partitionEnd + 1;
                        } else {
                            break;
                        }
                    }
                    int dbsize = dbValues.size();
                    int csvSize = (CommonScripts.on().getLatsRow(resultPath + "\\" + MasterDriver.testData.get("TableName") + ".csv") - 1);
                    if (Integer.parseInt(MasterDriver.testData.get("Start Loop")) != 1) {
                        dbsize = (dbsize * ((endLoop - Integer.parseInt(MasterDriver.testData.get("Start Loop"))) + 1));
                    } else {
                        dbsize = dbsize * (partitionEnd);
                    }
                    APP_LOGS.info("CSV File Size = " + csvSize);
                    APP_LOGS.info("DB Size = " + dbsize);
                    if (csvSize == dbsize) {
                        System.out.println(dbsize + " Values are Loaded at " + java.time.LocalTime.now());
                        MasterDriver.test.log(LogStatus.PASS, dbsize + " Values are Loaded at " + java.time.LocalTime.now());
                        status = true;
                    } else {
                        System.out.println(" Values are not loaded As we Expected " + java.time.LocalTime.now());
                        MasterDriver.test.log(LogStatus.FAIL, " Values are not loaded As we Expected " + java.time.LocalTime.now());
                    }
                } else {
                    System.out.println("Unable to Fetch Sample Data for the query:\n" + query);
                    MasterDriver.test.log(LogStatus.FAIL, "Unable to Fetch Sample Data for the query:\n" + query);
                }
            } else {
                System.out.println("Unable to Partition. Your End Loop(" + endLoop + ") and partition(" + getPartiotion + ") should be  divisible. So not able to generate CSV");
                MasterDriver.test.log(LogStatus.FAIL,
                        "Unable to Partition. Your End Loop(" + endLoop + ") and partition(" + getPartiotion + ") should be  divisible. So not able to generate CSV");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

}
