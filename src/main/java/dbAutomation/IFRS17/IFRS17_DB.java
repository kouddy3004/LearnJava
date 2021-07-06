package dbAutomation.IFRS17;


import dbAutomation.IFRS17.testScripts.DB;
import dbAutomation.IFRS17.testScripts.DataExtrapolate;
import org.apache.log4j.Logger;
import reUsables.MasterDriver;


public class IFRS17_DB {
    static IFRS17_DB app = new IFRS17_DB();
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

    private IFRS17_DB() {}

    public static IFRS17_DB on() {return app;}

    public boolean executeFunction(){
        boolean status=false;
        switch (MasterDriver.testData.get("FunctionName")) {
            case "FnMigrateData":
                status= DB.on().migrateDate();
                break;
            case "FnValidateCFLoader":
                status= DB.on().validateCfLoader();
                break;
            case "FnValidateDicountEngine":
                status= DB.on().validateDiscountEngine();
                break;
            case "FnExtraPolateData":
                status= DataExtrapolate.on().extraPolateData();
                break;
            default:
                APP_LOGS.info("Function not available. CHeck databank");
                break;
        }
        return status;

    }




   }
