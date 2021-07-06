package dbAutomation.IFRS17.dbOperations;

import com.relevantcodes.extentreports.LogStatus;
import org.apache.log4j.Logger;
import reUsables.CommonScripts;
import reUsables.DbService;
import reUsables.JsonFileHandler;
import reUsables.MasterDriver;

import java.util.*;

public class QueryHandler {
    private static String queries = "";
    private final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    static QueryHandler queryHandler = new QueryHandler();

    private QueryHandler() {
    }

    public static QueryHandler on() {
        return queryHandler;
    }

    public boolean checkCount(String source, String target, String ficDateColumn) throws Exception {
        String sourceCounter, targetCounter;
        boolean status = false;
        List<HashMap<String, String>> sourceCount = new ArrayList<>();
        List<HashMap<String, String>> targetCount = new ArrayList<>();
        sourceCounter = "select count(*) as SOURCECOUNT from( " + source + " )";
        targetCounter = "select count(*) as TARGETCOUNT from( " + target + " )";
        sourceCount = DbService.on().on().select(MasterDriver.conn, sourceCounter);
        targetCount = DbService.on().on().select(MasterDriver.conn, targetCounter);
        String sourcePolicies = "select " + ficDateColumn + "," + MasterDriver.testData.get("PrimaryKey") +
                ",cash_flow_date from(" + source + ")";
        String targetPolicies = "select " + ficDateColumn + "," + MasterDriver.testData.get("PrimaryKey") +
                ",cash_flow_date from(" + target + ")";
        if (sourceCount.get(0).get("SOURCECOUNT").equalsIgnoreCase(targetCount.get(0).get("TARGETCOUNT"))) {
            if (sourceCount.get(0).get("SOURCECOUNT").equals("0")) {
                APP_LOGS.error("No DATA available in Source and FSI Tables");
                MasterDriver.test.log(LogStatus.FAIL, "No DATA available in Source and FSI Tables");
            } else {
                APP_LOGS.info(sourceCount.get(0).get("SOURCECOUNT"));
                APP_LOGS.info(targetCount.get(0).get("TARGETCOUNT"));
                MasterDriver.test.log(LogStatus.PASS, "Source Count : " + sourceCount.get(0).get("SOURCECOUNT"));
                MasterDriver.test.log(LogStatus.PASS, "Target Count : " + targetCount.get(0).get("TARGETCOUNT"));
                status=true;
            }
        } else {
            APP_LOGS.info("Failed in count between Stage and FSI table");
            APP_LOGS.info("Source Count : " + sourceCount.get(0).get("SOURCECOUNT"));
            APP_LOGS.info("Target Count : " + targetCount.get(0).get("TARGETCOUNT"));
            List<HashMap<String, String>> conflictPolicies = new ArrayList<>();
            conflictPolicies = DbService.on().select(MasterDriver.conn, sourcePolicies + " minus " + targetPolicies);
            MasterDriver.test.log(LogStatus.ERROR, "Failed in Count between Stage and FSI table");
            MasterDriver.test.log(LogStatus.FAIL, "Source Count : " + sourceCount.get(0).get("SOURCECOUNT"));
            MasterDriver.test.log(LogStatus.FAIL, "Target Count : " + targetCount.get(0).get("TARGETCOUNT"));
            for (int row = 0; row < conflictPolicies.size(); row++) {
                MasterDriver.test.log(LogStatus.ERROR, "Policies not available in Target table are : "
                        +"Fic_mis_date : "+conflictPolicies.get(row).get(ficDateColumn.toUpperCase()) + " and "
                        +"Account_number :"+conflictPolicies.get(row).get(MasterDriver.testData.get("PrimaryKey").toUpperCase())
                        +"Cash Flow Date : "+conflictPolicies.get(row).get("cash_flow_date".toUpperCase()));
            }
        }
        if (!status) {
            APP_LOGS.error("ERROR : Failed in Count Check");
        }

        return status;
    }

    public String useFicMisDate(String query, String orderByColumns) {
        String[] fic_mis_date = MasterDriver.testData.get("FIC_MIS_DATE").split(",");

        if (fic_mis_date.length == 1) {
            query = query + " where as_of_date = upper('" + fic_mis_date[0] + "') ";
        } else {
            query = query + " where as_of_date in ( upper('" + fic_mis_date[0];
            for (int i = 1; i <= fic_mis_date.length - 1; i++) {
                query = query + "'),upper('" + fic_mis_date[i];
            }
            query = query + "')) ";
        }
        if(!CommonScripts.on().stringIsNullOrEmpty(MasterDriver.testData.get("PolicyNumber"))) {
            String[] policyNumber= MasterDriver.testData.get("PolicyNumber").split(",");
            if (policyNumber.length > 0 && !CommonScripts.on().stringIsNullOrEmpty(policyNumber[0])) {
                query = query + " and " + MasterDriver.testData.get("PrimaryKey") + " in ('" + policyNumber[0];
                if (policyNumber.length > 1) {
                    for (int i = 1; i < policyNumber.length; i++) {
                        query = query + "','" + policyNumber[i];
                    }
                }
                query = query + "')";
            }
        }
        if (!(orderByColumns.equalsIgnoreCase("") || orderByColumns.equalsIgnoreCase(null))) {
            query = query + " order by " + orderByColumns;
        }
        APP_LOGS.info(query);
        return query;
    }

    public String addDBColumnValues(String view) {
        String[] columnCheck = MasterDriver.testData.get("ColumnCheck").split(",");
        APP_LOGS.info(columnCheck.length);
        if (columnCheck.length <= 1) {
            view = "select * from " + view;
        } else {
            view = "select distinct " + MasterDriver.testData.get("ColumnCheck") + " from " + view;
        }
        System.out.println(view);
        return view;
    }

    public String checkPolicyCashFlow(String sourceView,String ficMisDate) throws Exception {
        String[] status = {"PASS", "PASS"};
        String[] fic_mis_date = MasterDriver.testData.get("FIC_MIS_DATE").split(",");
        String[] policyNumber= MasterDriver.testData.get("PolicyNumber").split(",");
        String sourceQuery ="select * from "+sourceView+" where as_of_date=upper('"+ficMisDate+"') ";
        if(policyNumber.length>0) {
            sourceQuery = sourceQuery + " and "+MasterDriver.testData.get("PrimaryKey")+" in ('" +policyNumber[0];
            if (policyNumber.length > 1) {
                for (int i = 1; i < policyNumber.length; i++) {
                    sourceQuery=sourceQuery+"','"+policyNumber[i];
                }
            }
            sourceQuery=sourceQuery+"')";
        }
        sourceQuery= sourceQuery +" order by as_of_date,cash_flow_date";
        String targetQuery = "";
        List<HashMap<String, String>> sourceCashFlowCheck = new ArrayList<>();
        List<HashMap<String, String>> targetCashFlowCheck = new ArrayList<>();
        String jsonQueryKey=MasterDriver.testCaseId.split("_")[0];
        targetQuery = DbService.on().createView(JsonFileHandler.on().readValueFromJson(
                MasterDriver.properties.get("DbJsonPath")+"\\TargetQueries.json", jsonQueryKey + "-CashFlows"), "Target");
        sourceCashFlowCheck = DbService.on().select(MasterDriver.conn, sourceQuery);
        MasterDriver.test.log(LogStatus.INFO, "CashFLow Queries");
        for (int row = 0; row < sourceCashFlowCheck.size(); row++) {
            String targetCashFlow = "";
            String cashFlowColumns = setCashFlowColumn(sourceCashFlowCheck.get(row).get("CASHFLOWCOLUMN"));
            targetCashFlow = "select * from (select " + MasterDriver.testData.get("PrimaryKey") + ",to_char(to_date(as_of_date,'YYYYMMDD')) as as_of_date," +
                    "to_char(cash_flow_date) as cash_flow_date,to_char(" + cashFlowColumns +
                    ") as AMOUNT from (" + targetQuery + ") where " + MasterDriver.testData.get("PrimaryKey") + " ='"
                    + sourceCashFlowCheck.get(row).get(MasterDriver.testData.get("PrimaryKey"))
                    + "') where as_of_date='"+ sourceCashFlowCheck.get(row).get("as_of_date".toUpperCase())
                    + "' and cash_flow_date='" + sourceCashFlowCheck.get(row).get("cash_flow_date".toUpperCase())
                    + "' order by as_of_date,cash_flow_date";
            MasterDriver.test.log(LogStatus.INFO, "Source Query View : " + sourceQuery);
            MasterDriver.test.log(LogStatus.INFO, "Target Query View : " + targetCashFlow);
            targetCashFlowCheck = DbService.on().select(MasterDriver.conn, targetCashFlow);
            System.out.println(targetCashFlowCheck.size());
            int flag = 0;
            for (int column = 0; column < targetCashFlowCheck.size(); column++) {
                if (sourceCashFlowCheck.get(row).get("AMOUNT").replaceAll("[^0-9]", "")
                        .equals(targetCashFlowCheck.get(column).get("AMOUNT").replaceAll("[^0-9]", ""))) {
                    flag = 1;
                    MasterDriver.test.log(LogStatus.PASS, "CashFlow Check PASSED in --> "
                            + sourceCashFlowCheck.get(row).get(MasterDriver.testData.get("PrimaryKey")));
                    MasterDriver.test.log(LogStatus.PASS,
                            "Source CASHFLOW Columns --> " + sourceCashFlowCheck.get(row).get("CASHFLOWCOLUMN")
                                    + " Source CASHFLOW Amounts --> " + sourceCashFlowCheck.get(row).get("AMOUNT"));

                    MasterDriver.test.log(LogStatus.PASS,
                            "Target CASHFLOW Columns --> " + cashFlowColumns
                                    + " Target CASHFLOW Amounts --> " + targetCashFlowCheck.get(column).get("AMOUNT"));

                    APP_LOGS.info(sourceCashFlowCheck.get(row).get(MasterDriver.testData.get("PrimaryKey")) + " -- "
                            + sourceCashFlowCheck.get(row).get("AMOUNT"));
                    System.out.println("Target Values");
                    APP_LOGS.info(targetCashFlowCheck.get(column).get(MasterDriver.testData.get("PrimaryKey")) + " -- "
                            + targetCashFlowCheck.get(column).get("AMOUNT"));
//                    break;
                }
            }
            if (flag == 0) {
                APP_LOGS.info(sourceCashFlowCheck.get(row).get(MasterDriver.testData.get("PrimaryKey")) + " -- "
                        + sourceCashFlowCheck.get(row).get("AMOUNT"));
                System.out.println("Target Values");
                APP_LOGS.info(targetCashFlowCheck.get(0).get(MasterDriver.testData.get("PrimaryKey")) + " -- "
                        + targetCashFlowCheck.get(0).get("AMOUNT"));
                MasterDriver.test.log(LogStatus.ERROR, "Row : "+row+" CashFlow Check Failed --> "
                        + sourceCashFlowCheck.get(row).get(MasterDriver.testData.get("PrimaryKey"))
                        +" for ficMisDate "+sourceCashFlowCheck.get(row).get("as_of_date".toUpperCase())
                        +" for cashFlow Date : "+sourceCashFlowCheck.get(row).get("CASH_FLOW_DATE".toUpperCase()));
                MasterDriver.test.log(LogStatus.FAIL,
                        "CASHFLOW Columns --> " + cashFlowColumns
                                + " Expected CASHFLOW Amounts --> " + sourceCashFlowCheck.get(row).get("AMOUNT")
                                + " Actual CASHFLOW Amounts --> " + targetCashFlowCheck.get(0).get("AMOUNT"));

                status[1] = "FAIL";
            }
            if (status[1].equalsIgnoreCase("fail")) {
                APP_LOGS.error("ERROR : Failed in CASHFLOWCHECKS");
            }
        }
        return status[1];
    }

    public String setCashFlowColumn(String cashFlowColumn) {
        String[] cashFlows = cashFlowColumn.split(",");
        String column = cashFlows[0];
        for (int row = 1; row < cashFlows.length; row++) {
            column = column + "||','||" + cashFlows[row];
        }
        System.out.println(column);
        return column;
    }

    public HashMap<String, List<HashMap<String, String>>> fetchSumOfCashflowsbasedOnPolicy(String sourceView) throws Exception {
        HashMap<String, List<HashMap<String, String>>> cashflowValues = new HashMap<>();
        List<List> cashFlowValues = new ArrayList<>();
        String sourceQuery = useFicMisDate("select * from " + sourceView, "as_of_date,cash_flow_date,scenario_no");
        String targetQuery = "";
        List<HashMap<String, String>> sourceCashFlowCheck = new ArrayList<>();
        List<HashMap<String, String>> targetCashFlowCheck = new ArrayList<>();
        targetQuery = DbService.on().createView(
                JsonFileHandler.on().readValueFromJson(MasterDriver.properties.get("DbJsonPath")+"\\TargetQueries.json"
                ,MasterDriver.testData.get("QueryFileName") + "-CashFlows"), "Target");
        DbService.on().createView(MasterDriver.properties.get("DbJsonPath")+"\\SourceQueries.json", sourceView);
        sourceCashFlowCheck = DbService.on().select(MasterDriver.conn, sourceQuery);
        MasterDriver.test.log(LogStatus.INFO, "CashFLow Queries");
        for (int row = 0; row < sourceCashFlowCheck.size(); row++) {
            String targetCashFlow = "";
            String cashFlowColumns = setCashFlowColumn(sourceCashFlowCheck.get(row).get("CASHFLOWCOLUMN"));
            targetCashFlow = "select * from (select " + MasterDriver.testData.get("PrimaryKey") + ",to_char(to_date(as_of_date,'YYYYMMDD')) as as_of_date," +
                    "to_char(cash_flow_date) as cash_flow_date,to_char(" + cashFlowColumns +
                    ") as AMOUNT,scenario_no from (" + targetQuery + ") where " + MasterDriver.testData.get("PrimaryKey") + " ='"
                    + sourceCashFlowCheck.get(row).get(MasterDriver.testData.get("PrimaryKey"))
                    + "') where (amount is not null and amount <> ',') "
                    + " and cash_flow_date='" + sourceCashFlowCheck.get(row).get("cash_flow_date".toUpperCase())
                    + "' and scenario_no='" + sourceCashFlowCheck.get(row).get("scenario_no".toUpperCase())
                    + "' and as_of_date = '" + sourceCashFlowCheck.get(row).get("as_of_date".toUpperCase())
                    + "' order by as_of_date,cash_flow_date,scenario_no";
            MasterDriver.test.log(LogStatus.INFO, "Target Query View : " + targetCashFlow);
            targetCashFlowCheck = DbService.on().select(MasterDriver.conn, targetCashFlow);
            MasterDriver.test.log(LogStatus.INFO, "Target Cash Flow Column Values : " + targetCashFlowCheck);
            String key = targetCashFlowCheck.get(0).get("POLICY_ID") + "|SC-" + targetCashFlowCheck.get(0).get("SCENARIO_NO")
                    + "|" + targetCashFlowCheck.get(0).get("AS_OF_DATE")
                    + "|" + targetCashFlowCheck.get(0).get("CASH_FLOW_DATE");

            cashflowValues.put(key, targetCashFlowCheck);
        }

        return cashflowValues;
    }

    public HashMap<String, HashMap<String, String>> fetchSumOfCashflowsbasedOnCashFlowDate(String sourceView, String condition) throws Exception {
        HashMap<String, HashMap<String,String>> cashflowValues = new HashMap<>();
        List<List> cashFlowValues = new ArrayList<>();
        List<HashMap<String, String>> sourceCashFlowCheck = new ArrayList<>();
        String sourceQuery = useFicMisDate("select distinct cashflowcolumn from " + sourceView, "");
        sourceCashFlowCheck=DbService.on().select(MasterDriver.conn,sourceQuery);
        String cashFlowCOlumns=sourceCashFlowCheck.get(0).get("cashflowcolumn".toUpperCase());
        for (int i =1;i<sourceCashFlowCheck.size();i++){
            cashFlowCOlumns=cashFlowCOlumns+","+sourceCashFlowCheck.get(i).get("cashflowcolumn".toUpperCase());
        }
        cashFlowCOlumns=removeDuplicates(cashFlowCOlumns);
        APP_LOGS.info("CashFlowColumns are : "+cashFlowCOlumns);
        String targetQuery = DbService.on().createView(
                JsonFileHandler.on().readValueFromJson("TargetQueries"
                        ,MasterDriver.testData.get("QueryFileName") + "-CashFlows"), "Target");
        String sumOFcashFlowCOlumns=addSumofinColumns(cashFlowCOlumns);
        APP_LOGS.info("Sum of CashFlowColumns are : "+sumOFcashFlowCOlumns);
        String cashFlowsQuery="select "+MasterDriver.testData.get("PrimaryKey")+",as_of_date,cash_flow_date,"+sumOFcashFlowCOlumns
                +" from (select "+ MasterDriver.testData.get("PrimaryKey")
                +",to_char(to_date(as_of_date,'YYYYMMDD')) as as_of_date"
                +",to_char(cash_flow_date) as cash_flow_date,"+cashFlowCOlumns+" from("
                + targetQuery+"))";
        cashFlowsQuery=useFicMisDate(cashFlowsQuery,"")
                +"and "+condition+" group by "+MasterDriver.testData.get("PrimaryKey")+",as_of_date,cash_flow_date,"+cashFlowCOlumns;
        System.out.println(cashFlowsQuery);
        List<HashMap<String, String>> cashFlowAmount = new ArrayList<>();
        cashFlowAmount=DbService.on().select(MasterDriver.conn,cashFlowsQuery);
        for (int i =0;i<cashFlowAmount.size();i++){
            String key = cashFlowAmount.get(i).get("POLICY_ID")
                    +"|"+ cashFlowAmount.get(i).get("AS_OF_DATE")
                    + "|" + cashFlowAmount.get(i).get("CASH_FLOW_DATE");
            cashflowValues.put(key,cashFlowAmount.get(i));
        }
        return cashflowValues;
    }


    public String removeDuplicates(String duplicatecolumn) {
        String[] columns = duplicatecolumn.split(",");
        Set<String> hashSet = new HashSet<String>();
        for (int i = 0; i < columns.length; i++) {
            hashSet.add(columns[i]);
        }
        return String.join(",", hashSet);
    }

    public String addSumofinColumns(String column) {
        String[] columns = column.split(",");
        String sum = "sum(" + columns[0] + ")";
        for (int i = 1; i < columns.length; i++) {
            sum = sum + ",sum(" + columns[i] + ")";
        }
        return sum;
    }

    public String addNVLfinColumns(String column) {
        String[] columns = column.split(",");
        String sum = "nvl(" + columns[0] + ",0)";
        for (int i = 1; i < columns.length; i++) {
            sum = sum + ",nvl(" + columns[i] + ",0)";
        }
        return sum;
    }
    public String checkData(List<HashMap<String, String>> sourceList, List<HashMap<String, String>> targetList, String primaryKey) {
        String[] status = {"PASS", "PASS"};
        APP_LOGS.info("Data Check Started");
        for (int row = 0; row < sourceList.size(); row++) {
            if (sourceList.get(row).equals(targetList.get(row))) {
                MasterDriver.test.log(LogStatus.PASS, "Row no " + row  + " : Data Check PASSED in  -- > " + sourceList.get(row).get(primaryKey));
                APP_LOGS.info("Row no " + row  + " : Data Check PASSED in in  -- > " + sourceList.get(row).get(primaryKey)
                        +" for ficMisDate "+sourceList.get(row).get("as_of_date".toUpperCase())
                        +" for cashFlow Date : "+sourceList.get(row).get("CASH_FLOW_DATE".toUpperCase()));
                System.out.println("Source : " + sourceList.get(row).keySet() + " -- > " + sourceList.get(row).values());
                System.out.println("Target : " + targetList.get(row).keySet() + " -- > " + targetList.get(row).values());
                System.out.println("Result Matched: " + targetList.get(row).equals(sourceList.get(row)));
                MasterDriver.test.log(LogStatus.PASS, "Source : " + sourceList.get(row).keySet() + " -- > " + sourceList.get(row).values());
                MasterDriver.test.log(LogStatus.PASS, "Target : " + targetList.get(row).keySet() + " -- > " + targetList.get(row).values());
            } else {
                APP_LOGS.info(primaryKey.toUpperCase());
                MasterDriver.test.log(LogStatus.ERROR, "Row no " + row + "Data Check  Failed in  -- > "
                        + sourceList.get(row).get(primaryKey.toUpperCase())
                        +" for ficMisDate "+sourceList.get(row).get("as_of_date".toUpperCase())
                        +" for cashFlow Date : "+sourceList.get(row).get("CASH_FLOW_DATE".toUpperCase()));
                APP_LOGS.info("Row no " + row + " : Data Check Failed in -- > " + sourceList.get(row).get(primaryKey.toUpperCase()));
                System.out.println("Source : " + sourceList.get(row).keySet() + " -- > " + sourceList.get(row).values());
                System.out.println("Target : " + targetList.get(row).keySet() + " -- > " + targetList.get(row).values());
                System.out.println("Result Matched: " + sourceList.get(row).equals(targetList.get(row)));
                for (String key : sourceList.get(row).keySet()) {
                    if (!sourceList.get(row).get(key).equals(targetList.get(row).get(key))) {
                        MasterDriver.test.log(LogStatus.FAIL, sourceList.get(row).get(primaryKey)
                                + ": Expected Target Column :->  " + key + " -- > " + sourceList.get(row).get(key)
                                + "\nActual Target Column   :->  " + key + " -- > " + targetList.get(row).get(key));
                    }
                    status[1] = "FAIL";
                }
            }
        }
        if (status[1].equalsIgnoreCase("fail")) {
            APP_LOGS.error("Error : DATA mismatch between Source and Target");
        }
        return status[1];
    }


}
