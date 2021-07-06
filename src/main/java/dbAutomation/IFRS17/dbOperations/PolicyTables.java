package dbAutomation.IFRS17.dbOperations;

import reUsables.CommonScripts;
import reUsables.CsvFileHandler;
import reUsables.MasterDriver;
import reUsables.ThreadForCSvWrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PolicyTables {
    CsvFileHandler csv;
    public PolicyTables(){csv = new CsvFileHandler();}

    public List<HashMap<String, String>> assignTables(List<HashMap<String, String>> mainList){
        boolean check=false;
        List<HashMap<String, String>> copiedList=new ArrayList<>();
        for (int i = ThreadForCSvWrite.startLoop; i <= ThreadForCSvWrite.endLoop; i++) {
            if (i >= Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                check = true;
                List<HashMap<String, String>> copyValues = new ArrayList<>();
                copyValues = CommonScripts.on().deepCloneListOfHashMap(mainList);
                for (int row = 0; row < mainList.size(); row++) {
                    if (ThreadForCSvWrite.fileName.toUpperCase().contains("STG_LIFE_INS_POLICY_TXNS")
                            || ThreadForCSvWrite.fileName.toUpperCase().contains("STG_ANNUITY_TXNS")
                            || ThreadForCSvWrite.fileName.toUpperCase().contains("STG_RETIREMENT_ACCOUNTS_TXNS")) {
                        String txn = mainList.get(row).get("v_txn_ref_no".toUpperCase());
                        copyValues.get(row).put("v_account_number".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                        copyValues.get(row).put("v_txn_ref_no".toUpperCase(), "ext_reins_" + txn + "_" + Integer.toString(i));
                    } else if (ThreadForCSvWrite.fileName.toUpperCase().contains("STG_INS_POLICY_CASH_FLOW")) {
                        copyValues.get(row).put("v_policy_code".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    } else if (ThreadForCSvWrite.fileName.toUpperCase().contains("STG_POLICY_COVERAGES")) {
                        copyValues.get(row).put("v_account_number".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    } else if (ThreadForCSvWrite.fileName.toUpperCase().contains("stg_life_ins_contracts".toUpperCase())) {
                        copyValues.get(row).put("v_account_number".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    }
                }

                try {
                    if (csv.checkPath(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv")) {
                        csv.createCsv(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv", copyValues, false);
                    }
                    if (i == Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                        ThreadForCSvWrite.loadCtlFIle(ThreadForCSvWrite.fileName);
                    }
                }

                catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
        if (check) {
            System.out.println("Finished " + Thread.currentThread().getName()
                    + " and for Partition " + ThreadForCSvWrite.startLoop + " and " + ThreadForCSvWrite.endLoop + " at " + java.time.LocalTime.now());
        }
        return copiedList;
    }
}
