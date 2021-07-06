package dbAutomation.IFRS17.dbOperations;

import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import reUsables.CsvFileHandler;
import reUsables.MasterDriver;
import reUsables.ThreadForCSvWrite;
import webAutomation.functionLib.CommonScripts;

import java.io.File;
import java.util.*;

public class CommonCode {

    public void enterCohortMaster(Set<HashMap<String, String>> cohortID) throws Exception {
        CsvFileHandler csv = new CsvFileHandler();
        List<HashMap<String, String>> cohortList = new ArrayList<>();
        Set<HashMap<String, String>> cohortSet = new HashSet<>();
        boolean reinsCheck = false;
        int row=0;
        System.out.println("Cohort Master  to enter "+cohortID.size());
        for (Map set : cohortID) {
            HashMap<String, String> cohortMap = new HashMap<>();
            cohortMap.put("FIC_MIS_DATE", String.valueOf(set.get("ficDate")));
            cohortMap.put("V_COHORT_ID", String.valueOf(set.get("cohortId")));
            cohortMap.put("V_COHORT_DESC", "Describing " + String.valueOf(set.get("cohortId")));
            cohortSet.add(cohortMap);
            if (set.containsKey("reInsu")) {
                HashMap<String, String> insuMap = new HashMap<>();
                insuMap.put("FIC_MIS_DATE", String.valueOf(set.get("ficDate")));
                insuMap.put("V_COHORT_ID", String.valueOf(set.get("reInsu")));
                insuMap.put("V_COHORT_DESC", "Describing " + String.valueOf(set.get("reInsu")));
                row=row+1;
                cohortSet.add(insuMap);
            }
            row=row+1;
        }
        cohortList.addAll(cohortSet);
        try {
            String cohortMasterPath = MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + "stg_cohort_master\\";
            File file = new File(cohortMasterPath);
            if (file.exists()) {
                FileUtils.cleanDirectory(new File(cohortMasterPath));
            }
            if (csv.checkPath(cohortMasterPath + "\\" + "stg_cohort_master.csv")) {
                csv.createCsv(cohortMasterPath + "\\" + "stg_cohort_master.csv", cohortList, false);
                ThreadForCSvWrite.loadCtlFIle("stg_cohort_master");
            }


            int dbsize = cohortID.size();
            int csvSize = (CommonScripts.on().getLatsRow(cohortMasterPath + "\\" + "stg_cohort_master.csv") - 1);
            if (csvSize == dbsize) {
                System.out.println(dbsize + " Values are Loaded at Cohort Master " + java.time.LocalTime.now());
                MasterDriver.test.log(LogStatus.PASS, dbsize + " Values are Loaded at Cohort Master " + java.time.LocalTime.now());
            } else {
                if (!reinsCheck) {
                    System.out.println(" Values are not loaded at Cohort Master " + java.time.LocalTime.now());
                    MasterDriver.test.log(LogStatus.INFO, " Values are not loaded at Cohort Master " + java.time.LocalTime.now());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
