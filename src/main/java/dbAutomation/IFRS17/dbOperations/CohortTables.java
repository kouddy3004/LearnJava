package dbAutomation.IFRS17.dbOperations;


import org.apache.log4j.Logger;
import reUsables.*;

import java.util.*;

public class CohortTables {    
    CsvFileHandler csv;
    CommonCode cc = new CommonCode();
    final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());    

    public CohortTables() {
        csv = new CsvFileHandler();
    }

    public boolean assignTables(List<HashMap<String, String>> mainList) throws Exception {
        boolean status = false;
        List<HashMap<String, String>> copiedList = new ArrayList<>();
        if (ThreadForCSvWrite.fileName.equalsIgnoreCase("STG_INS_GROUP_DIMENSION_MAP")) {
            copiedList = CommonScripts.on().deepCloneListOfHashMap(STG_INS_GROUP_DIMENSION_MAP(mainList));
        } else if (ThreadForCSvWrite.fileName.equalsIgnoreCase("STG_INS_COHORT_ASSUMED_CFS")) {
            copiedList = CommonScripts.on().deepCloneListOfHashMap(STG_INS_COHORT_ASSUMED_CFS(mainList));
        } else if (ThreadForCSvWrite.fileName.equalsIgnoreCase("stg_cohort_master")) {
            copiedList = CommonScripts.on().deepCloneListOfHashMap(stg_cohort_master(mainList));
        } else if (ThreadForCSvWrite.fileName.equalsIgnoreCase("STG_INS_COHORT_ACTUALS")) {
            copiedList = CommonScripts.on().deepCloneListOfHashMap(STG_INS_COHORT_ACTUALS(mainList));
        }
        else if (ThreadForCSvWrite.fileName.equalsIgnoreCase("STG_INS_COHORT_ACTUALS")) {
            copiedList = CommonScripts.on().deepCloneListOfHashMap(STG_INS_COHORT_ACTUALS(mainList));
        }
        else if (ThreadForCSvWrite.fileName.equalsIgnoreCase("fsi_ins_group_input_detail")) {
            copiedList = CommonScripts.on().deepCloneListOfHashMap(fsi_ins_group_input_detail(mainList));
        }
        if (copiedList.size() > 0) {
            status = true;
        }
        return status;
    }

    public List<HashMap<String, String>> STG_INS_GROUP_DIMENSION_MAP(List<HashMap<String, String>> mainList) throws Exception{
        List<HashMap<String, String>> grp_dim_list = new ArrayList<>();
        boolean check = false;
        int distinctRI = 1;
        if (MasterDriver.testData.get("Re-Insurance").equals("1-M")) {
            String distinctRI_Query = "select distinct V_RI_GROUP_CODE from STG_INS_GROUP_DIMENSION_MAP " + MasterDriver.testData.get("Where Clause");
            distinctRI = DbService.on().select(MasterDriver.conn, distinctRI_Query+" and V_RI_GROUP_CODE<>'OTH'").size();
        }
        for (int i = ThreadForCSvWrite.startLoop; i <= ThreadForCSvWrite.endLoop; i++) {
            int part = 100, sq = part * part;
            int counter=0;
            if (i >= Integer.parseInt(MasterDriver.testData.get("Start Loop"))){
                check = true;
                List<HashMap<String, String>> copyValues = CommonScripts.on().deepCloneListOfHashMap(mainList);
                for (int row = 0; row < mainList.size(); row++) {
                    String le = mainList.get(row).get("V_LEGAL_ENTITY_CODE".toUpperCase());
                    String lob = mainList.get(row).get("V_LOB_CODE".toUpperCase());
                    String prod = mainList.get(row).get("V_PRODUCT_CODE".toUpperCase());
                    int leRow = 1, lobRow = 1, prRow = 1;
                    int inDig = (int) Math.floor(Math.log10(part)) + 1;
                    int inDigSq = (int) Math.floor(Math.log10(sq)) + 1;
                    if (i >= part) {
                        if (i > sq) {
                            leRow = (i / sq) + 1;
                        }
                        if (i % sq == 0) {
                            leRow = (i / sq);
                        }
                        int lastDig = Integer.parseInt(String.valueOf(i).substring(String.valueOf(i).length() - inDig));
                        lobRow = lastDig % part;
                        if (i % part == 0) {
                            lobRow = 100;
                        }
                        prRow = (i / part) + 1;
                        if (i % part == 0) {
                            prRow = i / part;
                        }
                        if (i > sq) {
                            int inDigPr = Integer.parseInt(String.valueOf(i).substring(String.valueOf(i).length() - (inDigSq - 1)));
                            prRow = (inDigPr / part) + 1;
                            if (inDigPr % part == 0) {
                                prRow = (inDigPr / part);
                            }
                            if (inDigPr == 0) {
                                prRow = 100;
                            }
                        }
                    } else {
                        lobRow = i;
                        prRow = (i / part) + 1;
                    }
                    copyValues.get(row).put("V_GROUP_CODE".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("FIC_MIS_DATE"))) {
                        copyValues.get(row).put("FIC_MIS_DATE".toUpperCase(), MasterDriver.testData.get("FIC_MIS_DATE"));
                    }
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("CoverageType"))) {
                        copyValues.get(row).put("V_COVERAGE_TYPE".toUpperCase(), MasterDriver.testData.get("CoverageType"));
                    }
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("StartDate"))) {
                        copyValues.get(row).put("D_START_DATE".toUpperCase(), MasterDriver.testData.get("StartDate"));
                    }
                    copyValues.get(row).put("V_LEGAL_ENTITY_CODE".toUpperCase(), "LE" + Integer.toString(leRow));
                    copyValues.get(row).put("V_LOB_CODE".toUpperCase(), "LOB" + Integer.toString(lobRow));
                    copyValues.get(row).put("V_PRODUCT_CODE".toUpperCase(), "PC" + Integer.toString(prRow));
                    if (MasterDriver.testData.get("Re-Insurance").equalsIgnoreCase("Y")) {
                        String reinusrancePolicy = "";
                        if (MasterDriver.testData.get("Re-InsuranceType").equalsIgnoreCase("1-O")) {
                            if (!copyValues.get(row).get("V_RI_GROUP_CODE").equals("OTH")) {
                                reinusrancePolicy = "R1_" + ThreadForCSvWrite.newPolicy + Integer.toString(i);
                                copyValues.get(row).put("V_RI_GROUP_CODE".toUpperCase(), reinusrancePolicy);
                            }
                        } else if (MasterDriver.testData.get("Re-InsuranceType").equalsIgnoreCase("1-M")) {
                            if (!copyValues.get(row).get("V_RI_GROUP_CODE").equals("OTH")) {
                                counter = counter + 1;
                                if (counter > distinctRI) {
                                    counter = 1;
                                }
                                reinusrancePolicy = "R" + (counter) + ThreadForCSvWrite.newPolicy + Integer.toString(i);
                                copyValues.get(row).put("V_RI_GROUP_CODE".toUpperCase(), reinusrancePolicy);
                            }
                        }

                    }
                }
                try {
                    if (csv.checkPath(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv")) {
                        csv.createCsv(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv", copyValues, false);
                    }
                    if (i == Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                        ThreadForCSvWrite.loadCtlFIle(ThreadForCSvWrite.fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        if (check) {
            System.out.println("Finished " + Thread.currentThread().getName()
                    + " and for Partition " + ThreadForCSvWrite.startLoop + " and " + ThreadForCSvWrite.endLoop + " at " + java.time.LocalTime.now());
        }

        return grp_dim_list;
    }

    public List<HashMap<String, String>> stg_cohort_master(List<HashMap<String, String>> mainList) {
        List<HashMap<String, String>> grp_dim_list = new ArrayList<>();
        boolean check = false;
        for (int i = ThreadForCSvWrite.startLoop; i <= ThreadForCSvWrite.endLoop; i++) {
            int part = 100, sq = part * part;
            if (i >= Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                check = true;
                List<HashMap<String, String>> copyValues = CommonScripts.on().deepCloneListOfHashMap(mainList);
                for (int row = 0; row < mainList.size(); row++) {
                    copyValues.get(row).put("V_COHORT_ID".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("FIC_MIS_DATE"))) {
                        copyValues.get(row).put("FIC_MIS_DATE".toUpperCase(), MasterDriver.testData.get("FIC_MIS_DATE"));
                    }
                    copyValues.get(row).put("V_COHORT_DESC".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));

                }
                try {
                    if (csv.checkPath(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv")) {
                        csv.createCsv(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv", copyValues, false);
                    }
                    if (i == Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                        ThreadForCSvWrite.loadCtlFIle(ThreadForCSvWrite.fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        if (check) {
            System.out.println("Finished " + Thread.currentThread().getName()
                    + " and for Partition " + ThreadForCSvWrite.startLoop + " and " + ThreadForCSvWrite.endLoop + " at " + java.time.LocalTime.now());
        }

        return grp_dim_list;
    }

    public List<HashMap<String, String>> STG_INS_COHORT_ASSUMED_CFS(List<HashMap<String, String>> mainList) throws Exception {
        List<HashMap<String, String>> grp_dim_list = new ArrayList<>();
        boolean check = false;
        int distinctRI = 1;
        Set<HashMap<String,String>> cohortSet = new HashSet<>();
        if (MasterDriver.testData.get("Re-InsuranceType").equals("1-M")) {
            String distinctRI_Query = "select distinct V_RI_COHORT_ID from STG_INS_COHORT_ASSUMED_CFS " + MasterDriver.testData.get("Where Clause");
            distinctRI = DbService.on().select(MasterDriver.conn, distinctRI_Query+" and v_ri_cohort_id<>'OTH'").size() ;
        }
        for (int i = ThreadForCSvWrite.startLoop; i <= ThreadForCSvWrite.endLoop; i++) {
            int part = 100, sq = part * part;
            if (i >= Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                check = true;
                List<HashMap<String, String>> copyValues = CommonScripts.on().deepCloneListOfHashMap(mainList);
                int counter = 0;
                for (int row = 0; row < mainList.size(); row++) {
                    HashMap<String,String> cohortMap=new HashMap<>();
                    copyValues.get(row).put("V_COHORT_ID".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    cohortMap.put("cohortId",ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    cohortMap.put("ficDate",copyValues.get(row).get("FIC_MIS_DATE"));
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("FIC_MIS_DATE"))) {
                        copyValues.get(row).put("FIC_MIS_DATE".toUpperCase(), MasterDriver.testData.get("FIC_MIS_DATE"));
                        cohortMap.put("ficDate",MasterDriver.testData.get("FIC_MIS_DATE"));
                    }
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("CoverageType"))) {
                        copyValues.get(row).put("V_COVERAGE_ID".toUpperCase(), MasterDriver.testData.get("CoverageType"));
                    }
                    if (MasterDriver.testData.get("Re-Insurance").equalsIgnoreCase("Y")) {
                        boolean cohortCheck = false;
                        String reinusrancePolicy = "";
                        if (MasterDriver.testData.get("Re-InsuranceType").equalsIgnoreCase("1-O")) {
                            reinusrancePolicy = "R1_" + ThreadForCSvWrite.newPolicy + Integer.toString(i);
                            copyValues.get(row).put("V_RI_COHORT_ID".toUpperCase(), reinusrancePolicy);
                        } else if (MasterDriver.testData.get("Re-InsuranceType").equalsIgnoreCase("1-M")) {
                            if (!copyValues.get(row).get("V_RI_COHORT_ID").equals("OTH")) {
                                counter = counter + 1;
                                if (counter > distinctRI) {
                                    counter = 1;
                                }
                                reinusrancePolicy = "R" + (counter) + ThreadForCSvWrite.newPolicy + Integer.toString(i);
                                copyValues.get(row).put("V_RI_COHORT_ID".toUpperCase(), reinusrancePolicy);
                            }
                        }
                        if (!CommonScripts.on().stringIsNullOrEmpty(reinusrancePolicy)) {
                            cohortMap.put("reInsu",reinusrancePolicy);
                        }
                    }
                    cohortSet.add(cohortMap);
                }
                try {
                    if (csv.checkPath(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv")) {
                        csv.createCsv(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv", copyValues, false);
                    }
                    if (i == Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                        ThreadForCSvWrite.loadCtlFIle(ThreadForCSvWrite.fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        if (check) {
            System.out.println("Finished " + Thread.currentThread().getName()
                    + " and for Partition " + ThreadForCSvWrite.startLoop + " and " + ThreadForCSvWrite.endLoop + " at " + java.time.LocalTime.now());
            if (cohortSet.size() > 0) {
                cc.enterCohortMaster(cohortSet);
            }
        }

        return grp_dim_list;
    }

    public List<HashMap<String, String>> STG_INS_COHORT_ACTUALS(List<HashMap<String, String>> mainList) throws Exception {
        List<HashMap<String, String>> grp_dim_list = new ArrayList<>();
        boolean check = false;
        int distinctRI = 1;
        if (MasterDriver.testData.get("Re-InsuranceType").equals("1-M")) {
            String distinctRI_Query = "select distinct V_RI_COHORT_ID from STG_INS_COHORT_ACTUALS " + MasterDriver.testData.get("Where Clause");
            distinctRI = DbService.on().select(MasterDriver.conn, distinctRI_Query+" and v_ri_cohort_id<>'OTH'").size();
        }
        Set<HashMap<String,String>> cohortSet = new HashSet<>();
        for (int i = ThreadForCSvWrite.startLoop; i <= ThreadForCSvWrite.endLoop; i++) {
            if (i >= Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                int counter = 0;
                check = true;
                List<HashMap<String, String>> copyValues = CommonScripts.on().deepCloneListOfHashMap(mainList);
                int extraPolateCashFlow = 0;
                for (int row = 0; row < mainList.size(); row++) {
                    HashMap<String,String> cohortMap=new HashMap<>();
                    copyValues.get(row).put("V_COHORT_ID".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    cohortMap.put("cohortId",ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    cohortMap.put("ficDate",copyValues.get(row).get("FIC_MIS_DATE"));
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("FIC_MIS_DATE"))) {
                        copyValues.get(row).put("FIC_MIS_DATE".toUpperCase(), MasterDriver.testData.get("FIC_MIS_DATE"));
                        cohortMap.put("ficDate",MasterDriver.testData.get("FIC_MIS_DATE"));
                    }
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("CoverageType"))) {
                        copyValues.get(row).put("V_COVERAGE_ID".toUpperCase(), MasterDriver.testData.get("CoverageType"));
                    }
                    copyValues.get(row).put("V_TXN_REF_NO".toUpperCase(), MasterDriver.testData.get("TXN_REF_NO") + Integer.toString(i) + "_" + Integer.toString(row));
                    if (MasterDriver.testData.get("Re-Insurance").equalsIgnoreCase("Y")) {
                        boolean cohortCheck = false;
                        String reInsurancePolicy = "";
                        if (MasterDriver.testData.get("Re-InsuranceType").equalsIgnoreCase("1-O")) {
                            if (!copyValues.get(row).get("V_RI_COHORT_ID").equals("OTH")) {
                                reInsurancePolicy = "R1_" + ThreadForCSvWrite.newPolicy + Integer.toString(i);
                                copyValues.get(row).put("V_RI_COHORT_ID".toUpperCase(), reInsurancePolicy);
                            }
                        } else if (MasterDriver.testData.get("Re-InsuranceType").equalsIgnoreCase("1-M")) {
                            if (!copyValues.get(row).get("V_RI_COHORT_ID").equals("OTH")) {
                                counter = counter + 1;
                                if (counter > distinctRI) {
                                    counter = 1;
                                }
                                reInsurancePolicy = "R" + counter + ThreadForCSvWrite.newPolicy + Integer.toString(i);
                                copyValues.get(row).put("V_RI_COHORT_ID".toUpperCase(), reInsurancePolicy);
                            }
                        }
                        if (!CommonScripts.on().stringIsNullOrEmpty(reInsurancePolicy)) {
                            cohortMap.put("reInsu",reInsurancePolicy);
                        }
                    }
                    cohortSet.add(cohortMap);
                }

                try {
                    if (csv.checkPath(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv")) {
                        csv.createCsv(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv", copyValues, false);
                    }
                    if (i == Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                        ThreadForCSvWrite.loadCtlFIle(ThreadForCSvWrite.fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        if (check) {
            System.out.println("Finished " + Thread.currentThread().getName()
                    + " and for Partition " + ThreadForCSvWrite.startLoop + " and " + ThreadForCSvWrite.endLoop + " at " + java.time.LocalTime.now());
            APP_LOGS.info("Set 2 " + cohortSet.size());
            if (cohortSet.size() > 0) {
                cc.enterCohortMaster(cohortSet);
            }
        }

        return grp_dim_list;
    }

    public List<HashMap<String, String>> fsi_ins_group_input_detail(List<HashMap<String, String>> mainList) throws Exception {
        List<HashMap<String, String>> grp_dim_list = new ArrayList<>();
        boolean check = false;
        Set<HashMap<String,String>> cohortSet = new HashSet<>();
        for (int i = ThreadForCSvWrite.startLoop; i <= ThreadForCSvWrite.endLoop; i++) {            
            if (i >= Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                check = true;
                List<HashMap<String, String>> copyValues = CommonScripts.on().deepCloneListOfHashMap(mainList);
                int counter = 0;
                for (int row = 0; row < mainList.size(); row++) {
                    HashMap<String,String> cohortMap=new HashMap<>();
                    copyValues.get(row).put("group_code".toUpperCase(), ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    cohortMap.put("cohortId",ThreadForCSvWrite.newPolicy + Integer.toString(i));
                    cohortMap.put("ficDate",copyValues.get(row).get("FIC_MIS_DATE"));
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("FIC_MIS_DATE"))) {
                        copyValues.get(row).put("FIC_MIS_DATE".toUpperCase(), MasterDriver.testData.get("FIC_MIS_DATE"));
                        cohortMap.put("ficDate",MasterDriver.testData.get("FIC_MIS_DATE"));
                    }
                    if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("Scenario Code"))) {
                        copyValues.get(row).put("v_ins_scenario_code".toUpperCase(), MasterDriver.testData.get("Scenario Code"));
                    }
                    cohortSet.add(cohortMap);
                }
                try {
                    if (csv.checkPath(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv")) {
                        csv.createCsv(MasterDriver.properties.getProperty("ResultPath") + "\\extraPolateData\\" + ThreadForCSvWrite.fileName + "\\" + ThreadForCSvWrite.fileName + ".csv", copyValues, false);
                    }
                    if (i == Integer.parseInt(MasterDriver.testData.get("Start Loop"))) {
                        ThreadForCSvWrite.loadCtlFIle(ThreadForCSvWrite.fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }

        if (check) {
            System.out.println("Finished " + Thread.currentThread().getName()
                    + " and for Partition " + ThreadForCSvWrite.startLoop + " and " + ThreadForCSvWrite.endLoop + " at " + java.time.LocalTime.now());
            if (cohortSet.size() > 0) {
                cc.enterCohortMaster(cohortSet);
            }
        }

        return grp_dim_list;
    }
}
