package dbAutomation.IFRS17.dbOperations;

import com.relevantcodes.extentreports.LogStatus;
import io.cucumber.java.pt.Mas;
import org.apache.log4j.Logger;
import reUsables.DbService;
import reUsables.JsonFileHandler;
import reUsables.MasterDriver;

import javax.management.Query;
import java.util.HashMap;
import java.util.List;

public class CfLoaderValidation {
    static CfLoaderValidation cfLoader = new CfLoaderValidation();
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

    private CfLoaderValidation() {
    }

    public static CfLoaderValidation on() {
        return cfLoader;
    }

    public boolean validateCfLoaderBatch(){
        boolean status=false;
        String queryPath= MasterDriver.properties.getProperty("DbValidationPath");
        String sourceQuery="";
        String targetQuery="";
        String sourceView = "Source_" + MasterDriver.testCaseId.replaceAll("[^a-zA-Z0-9]", ""),
                targetView = "Target_" + MasterDriver.testCaseId.replaceAll("[^a-zA-Z0-9]", "");
        try {
            DbService.on().createView(JsonFileHandler.on().readValueFromJson(queryPath+"\\SourceQueries.json"
                    ,MasterDriver.testData.get("QueryFileName")), sourceView);
            DbService.on().createView(JsonFileHandler.on().readValueFromJson(queryPath+"\\TargetQueries.json"
                    ,MasterDriver.testData.get("QueryFileName")), targetView);
            //Setting Stage Query
            sourceQuery = QueryHandler.on().addDBColumnValues(sourceView);
            sourceQuery = QueryHandler.on().useFicMisDate(sourceQuery, MasterDriver.testData.get("OrderBY"));
            List<HashMap<String, String>> sourceResult=DbService.on().select(MasterDriver.conn,sourceQuery);
            //Setting Target Query
            targetQuery = QueryHandler.on().addDBColumnValues(targetView);
            targetQuery = QueryHandler.on().useFicMisDate(targetQuery, MasterDriver.testData.get("OrderBY"));
            List<HashMap<String, String>> targetResult = DbService.on().select(MasterDriver.conn,targetQuery);
            MasterDriver.test.log(LogStatus.INFO, "Source Query : " + sourceQuery);
            MasterDriver.test.log(LogStatus.INFO, "Target Query : " + targetQuery);
            //Checking Count
            String validatestatus ="FAIl";
            if(QueryHandler.on().checkCount(sourceQuery, targetQuery, "as_of_date")){
                MasterDriver.test.log(LogStatus.PASS, "Matched counts between Source tables and Target table");
                //Checking Data in source and Target
                String[] fic_mis_date = MasterDriver.testData.get("FIC_MIS_DATE").split(",");
                validatestatus = QueryHandler.on()
                        .checkData(sourceResult, targetResult, MasterDriver.testData.get("PrimaryKey"));
                if(validatestatus.equalsIgnoreCase("pass")) {
                    for (int i = 0; i < fic_mis_date.length; i++) {
                            validatestatus = QueryHandler.on().checkPolicyCashFlow
                                    (sourceView, fic_mis_date[i]);
                            MasterDriver.test.log(LogStatus.PASS, "Matched CASHFLOWS between Source tables and Target table");
                    }
                    MasterDriver.test.log(LogStatus.PASS, "Matched DATA between Source tables and Target table");
                }
            }
            if(validatestatus.equalsIgnoreCase("pass")) {
                status = true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }
}
