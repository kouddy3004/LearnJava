package dbAutomation.IFRS17.dbOperations;

import com.relevantcodes.extentreports.LogStatus;
import dbAutomation.IFRS17.testScripts.DB;
import org.apache.log4j.Logger;
import org.testng.Assert;
import reUsables.CommonScripts;
import reUsables.DbService;
import reUsables.JsonFileHandler;
import reUsables.MasterDriver;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Discountengine {
    static Discountengine discount = new Discountengine();
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    public static String excelUseCase = "0";
    private String accountNumber = "";
    private String policyId = "";
    int row = 0;
    int statusflag = 0;
    String v_assumption_scenario_type = "B";
    String n_ins_Scenario_id = "0";
    String n_projection_index = "0";
    String N_ACTUARIAL_ASSUMPTN_SKEY = "-1";

    private Discountengine() {
    }

    public static Discountengine on() {
        return discount;
    }




    public void fetchAccountNumberandPolicyId(String ficMisDate) throws Exception {
        String query = JsonFileHandler.on().readValueFromJson(
                MasterDriver.properties.get("DbValidationPath")+"\\MiscQueries.json", "FetchingAccountNumberAndPolicyIDFromSTG");
        String le = "select legal_entity_id as LE from dim_legal_entity_tl where legal_entity_name='" + MasterDriver.testData.get("LE") + "'";
        List<HashMap<String, String>> dbForAccandID = DbService.on().select(MasterDriver.conn, le);
        le = dbForAccandID.get(0).get("LE");
        String lob = "select lob_id as LOB from dim_lob_tl where lob_name='" + MasterDriver.testData.get("LOB") + "'";
        dbForAccandID = DbService.on().select(MasterDriver.conn, lob);
        lob = dbForAccandID.get(0).get("LOB");
        String cover = "select n_coverage_type_skey as CVG from dim_coverage_type where v_coverage_type='" + MasterDriver.testData.get("Coverage")+ "'";
        dbForAccandID = DbService.on().select(MasterDriver.conn, cover);
        cover = dbForAccandID.get(0).get("CVG");
        query = query + " where (A.LEGAL_ENTITY_ID=" + le + ") and (A.LOB_ID=" + lob + ") and (A.ACCOUNT_NUMBER in(select AA.V_ACCOUNT_NUMBER \n" +
                "     from STG_POLICY_COVERAGES AA INNER JOIN \n" +
                "     DIM_COVERAGE_TYPE C on (AA.V_COVERAGE_TYPE=C.V_COVERAGE_TYPE  \n" +
                "     AND AA.FIC_MIS_DATE >= C.D_RECORD_START_DATE\n" +
                "     AND AA.FIC_MIS_DATE <=C.D_RECORD_END_DATE)\n" +
                "     where AA.FIC_MIS_DATE='" + ficMisDate + "' and a.as_of_date=aa.FIC_MIS_DATE and C.N_COVERAGE_TYPE_SKEY=" + cover + ")"
                + "     )order by A.policy_id desc\n";
        query = DbService.on().createView(query, "fetchAccountandPolicyID");
        dbForAccandID = DbService.on().on().select(MasterDriver.conn, query);
        APP_LOGS.info(query);
        System.out.println(dbForAccandID);
        if (!dbForAccandID.isEmpty()) {
            policyId = dbForAccandID.get(0).get("POLICY_ID");
            accountNumber = dbForAccandID.get(0).get("ACCOUNT_NUMBER");
        } else {
            APP_LOGS.info("No Values has been fetched from MISC Queries");
        }

    }   


    public boolean validateFsitoFsiwithexcelDB(String fic_mis_date) throws Exception {
        boolean status = false;
        String[] policyNumber=MasterDriver.testData.get("PolicyNumber").split(",");
        if (policyNumber.length > 0 && !CommonScripts.on().stringIsNullOrEmpty(policyNumber[0])) {
            for (int i = 0; i < policyNumber.length; i++) {
                String accountNumber = policyNumber[i];
                this.accountNumber = accountNumber;
                status = validateExcelAndDB(fic_mis_date);
            }
        } else {
            validateExcelAndDB(fic_mis_date);
        }
        if (statusflag != 1) {
            status = true;
        }
        APP_LOGS.info(status);
        return status;
    }


    public boolean validateExcelAndDB(String fic_mis_date) throws Exception {
        MasterDriver.test.log(LogStatus.INFO, "Checking for account Number " + accountNumber);
        int flag = 0;
        boolean status = false;
        String fileName = "InputCashFlows_discounting";
        String path = MasterDriver.properties.getProperty("DbValidationPath")+"//"+ fileName + ".xlsx";
        String sheetName = "";
        String amount = "";
        Date date = new SimpleDateFormat("dd-MMM-yy").parse(fic_mis_date);
        String date_s = new SimpleDateFormat("dd/MM/yyyy").format(date);
        if (CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("SheetName"))) {
            if (accountNumber.contains("GMM")) {
                sheetName = "GMM";
            } else if (accountNumber.contains("VFA")) {
                sheetName = "VFA";
            } else if (accountNumber.contains("PAA")) {
                sheetName = "PAA";
            } else if (accountNumber.contains("LDTI")) {
                sheetName = "LDTI";
            } else {
                APP_LOGS.info("Account Number doesn't contain sheet Name " + accountNumber);
                MasterDriver.test.log(LogStatus.FATAL,
                        "Account Number doesn't contain sheet Name " + accountNumber);
                Assert.fail("Failed in Account Number doesn't contain sheet Name " + accountNumber);
            }
        } else {
            sheetName = MasterDriver.testData.get("SheetName");
        }
        //String useCase = accountNumber.replaceAll("[^0-9]", "");
        APP_LOGS.info(sheetName);
        List<HashMap<String, String>> excelVales = CommonScripts.on().readExcel(path, sheetName);

        String query = "select * from " + DB.tableName;
        if (DB.tableName.contains("LC_GRP")) {
            query = query + " where  to_char(to_date(fic_mis_date,'YYYYMMDD')) ='" + fic_mis_date + "'";
            if (!CommonScripts.on().stringIsNullOrEmpty(accountNumber)) {
                query = query + " and v_cohort_id='" + accountNumber;
            }
        } else {
            query = query + " where to_char(to_date(fic_mis_date,'YYYYMMDD')) ='" + fic_mis_date + "'";
            if (!CommonScripts.on().stringIsNullOrEmpty(accountNumber)) {
                query = query + " and v_account_number ='" + accountNumber;
            }
        }
        query+="'";
        List<HashMap<String, String>> actualValues = DbService.on().on().select(MasterDriver.conn, query);
        if (actualValues.isEmpty()) {
            MasterDriver.test.log(LogStatus.ERROR,
                    "No Values are available for the particular account " + accountNumber
                            + "and fic_mis_date " + fic_mis_date
                            + " in " + DB.tableName + " Table");
            flag = 1;
        } else {
            for (int i = 0; i < excelVales.size(); i++) {
                if (excelVales.get(i).get("Report Date").equalsIgnoreCase(date_s)
                        && excelVales.get(i).get("Test Case").equalsIgnoreCase(MasterDriver.testCaseId)) {
                    excelUseCase = excelVales.get(i).get("UseCase");
                    String inputColumn = "";
                    if (!CommonScripts.on().stringIsNullOrEmpty(excelVales.get(i).get("Column Name"))) {
                        inputColumn = excelVales.get(i).get("Column Name");
                        if (!inputColumn.substring(0, 2).equalsIgnoreCase("N_")
                                && !CommonScripts.on().stringIsNullOrEmpty(inputColumn)) {
                            inputColumn = "N_" + excelVales.get(i).get("Column Name");
                        }
                        if (!CommonScripts.on().stringIsNullOrEmpty(inputColumn)) {
                            MasterDriver.test.log(LogStatus.INFO,
                                    "Validation for fic mis date " + fic_mis_date
                                            + " and for input Column " + inputColumn);
                            if (!CommonScripts.on().stringIsNullOrEmpty(excelVales.get(i).get("Execute"))) {
                                amount = excelVales.get(i).get("Execute");
                                if (CommonScripts.on().stringContainsNumber(amount) == true) {
                                    DecimalFormat df = new DecimalFormat("#.###");
                                    df.setRoundingMode(RoundingMode.HALF_DOWN);
                                    amount = String.valueOf(df.format(Double.parseDouble(amount)));
                                }
                                APP_LOGS.info(inputColumn + " : " + amount);
                                status = validateAmountandDB(amount, inputColumn, fic_mis_date);
                            } else {
                                amount = "0";
                                status = validateAmountandDB(amount, inputColumn, fic_mis_date);
                            }
                        }
                    }
                }
            }
        }
        if (flag != 1) {
            status = true;
            statusflag = 1;
        }
        if (!status) {
            status=false;
            MasterDriver.test.log(LogStatus.FAIL,
                    "Issue is Fic_mis Date " + date_s
                            + "(or) Test Case ID " + MasterDriver.testCaseId
                            + " is not matched with excel :  " + fileName);
        }
        APP_LOGS.info(status);
        return status;
    }

    public boolean validateAmountandDB(String amount, String inputColumn, String fic_mis_date) throws Exception {
        boolean status = false;
        int flag = 0;
        String query = fetchFSITableQuery(inputColumn, fic_mis_date);
        MasterDriver.test.log(LogStatus.INFO, "Query for fetching Amount : " + query);
        List<HashMap<String, String>> actualValues = DbService.on().on().select(MasterDriver.conn, query);
        if (actualValues.isEmpty()) {
            MasterDriver.test.log(LogStatus.ERROR,
                    "No Values are available for the particular account " + accountNumber
                            + " in " + DB.tableName + " Table");
            flag = 1;
        } else {
            APP_LOGS.info(actualValues);
            if (actualValues.get(0).containsKey("error")) {
                MasterDriver.test.log(LogStatus.ERROR,
                        "Input Column " + inputColumn
                                + " is not available in " + DB.tableName + " table");
                flag = 1;
            } else {
                if (!CommonScripts.on().stringIsNullOrEmpty(actualValues.get(0).get(inputColumn.toUpperCase()))) {
                    if (actualValues.get(0).get(inputColumn.toUpperCase()).equals(amount)) {
                        MasterDriver.test.log(LogStatus.PASS, "For Policy "
                                + actualValues.get(0).get("policy_id".toUpperCase()) + " and for Fic_MIS_DATE "
                                + actualValues.get(0).get("as_of_date".toUpperCase()) + " and for Input Variable " + inputColumn
                                + " the expected Amount " + actualValues.get(0).get(inputColumn.toUpperCase())
                                + " and the Actual amount " + amount + " has been matched");
                    } else {
                        MasterDriver.test.log(LogStatus.FAIL, "For Policy "
                                + actualValues.get(0).get("policy_id".toUpperCase()) + " and for Fic_MIS_DATE "
                                + actualValues.get(0).get("as_of_date".toUpperCase()) + " and for Input Variable " + inputColumn
                                + " the expected Amount " + amount
                                + " and the Actual amount " + actualValues.get(0).get(inputColumn.toUpperCase())
                                + " has not been matched");
                        flag = 1;
                    }
                } else {
                    MasterDriver.test.log(LogStatus.FAIL, "In DB,Only Null amount is available for the Policy "
                            + actualValues.get(0).get("policy_id".toUpperCase()) + " and for Fic_MIS_DATE "
                            + actualValues.get(0).get("as_of_date".toUpperCase())
                            + " and for Input Variable " + inputColumn.toUpperCase());
                    flag = 1;
                }
            }
        }

        if (flag != 1) {
            status = true;
            statusflag = 1;
        }
        APP_LOGS.info(status);
        return status;
    }

    public String fetchFSITableQuery(String inputColumn, String fic_mis_date) throws Exception {
        APP_LOGS.info(accountNumber);
        if(CommonScripts.on().stringIsNullOrEmpty(accountNumber)) {
            String policyIdQuery = "Select * from fetchAccountandPolicyID A " +
                    "where account_number='" + accountNumber + "' order by policy_id desc";
            MasterDriver.test.log(LogStatus.INFO, "Query for fetching Policy ID and Number : " + policyIdQuery);
        List<HashMap<String, String>> fetchPolicyId = DbService.on().on().select(MasterDriver.conn, policyIdQuery);
        policyId = fetchPolicyId.get(0).get("policy_id".toUpperCase());
        }
        String query = "select v_account_number as policy_id,to_char(to_date(fic_mis_date,'YYYYMMDD')) as as_of_date,"
                + inputColumn + " from " + DB.tableName
                + " a where to_char(to_date(fic_mis_date,'YYYYMMDD')) = '" + fic_mis_date + "'"
                + " and v_assumption_scenario_type='B' and N_projection_index=0  and n_policy_id in";
        if (!CommonScripts.on().stringIsNullOrEmpty(policyId)) {
            query = query + " ('" + policyId + "')";
        } else {
            MasterDriver.test.log(LogStatus.INFO,
                    accountNumber + " is not available in fetchaccountNumberand id query");
            query = query + " (select max(temp.n_policy_id) from " + DB.tableName
                    + " temp where temp.v_account_number=a.v_account_number and temp.fic_mis_date=a.fic_mis_date)";
        }
        if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("n_ins_Scenario_id"))) {
            query = query + " and n_ins_scenario_id in (select temp.n_ins_scenario_id from " + DB.tableName
                    + " temp where temp.v_account_number=a.v_account_number and temp.fic_mis_date=a.fic_mis_date)";
        }
        if (!CommonScripts.on().stringIsNullOrEmpty(accountNumber)) {
            query = query + " and a.v_account_number='" + accountNumber + "'";
        }

        if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("v_assumption_scenario_type"))) {
            v_assumption_scenario_type = MasterDriver.testData.get("v_assumption_scenario_type");
        }
        if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("n_ins_Scenario_id"))) {
            n_ins_Scenario_id = MasterDriver.testData.get("n_ins_Scenario_id");
        }
        if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("n_projection_index"))) {
            n_projection_index = MasterDriver.testData.get("n_projection_index");
        }
        if (!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("N_ACTUARIAL_ASSUMPTN_SKEY"))) {
            N_ACTUARIAL_ASSUMPTN_SKEY = MasterDriver.testData.get("N_ACTUARIAL_ASSUMPTN_SKEY");
        }
        if (DB.tableName.contains("LC_GRP")) {
            query = "select v_cohort_id as policy_id,to_char(to_date(fic_mis_date,'YYYYMMDD')) as as_of_date," + inputColumn
                    + " from " + DB.tableName
                    + " where to_char(to_date(fic_mis_date,'YYYYMMDD')) = '" + fic_mis_date + "' " +
                    "and v_assumption_scenario_type='" + v_assumption_scenario_type + "'" +
                    " and n_ins_Scenario_id ='" + n_ins_Scenario_id + "'" +
                    " and n_projection_index = '" + n_projection_index + "'" +
                    " and N_ACTUARIAL_ASSUMPTN_SKEY='" + N_ACTUARIAL_ASSUMPTN_SKEY + "'";
        }
        if (!CommonScripts.on().stringIsNullOrEmpty(accountNumber)) {
            query = query + " and v_cohort_id='" + accountNumber + "'";
        }
        return query;
    }
}
