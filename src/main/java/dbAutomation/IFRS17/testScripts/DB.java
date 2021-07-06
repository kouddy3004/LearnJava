package dbAutomation.IFRS17.testScripts;

import com.relevantcodes.extentreports.LogStatus;
import dbAutomation.IFRS17.dbOperations.CfLoaderValidation;
import dbAutomation.IFRS17.dbOperations.Discountengine;
import dbAutomation.IFRS17.dbOperations.MigrateData;
import org.apache.log4j.Logger;
import reUsables.FileHandler;
import reUsables.MasterDriver;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    static DB db = new DB();
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    public static String tableName = "";

    private DB() {
    }

    public static DB on() {
        return db;
    }

    public boolean migrateDate() {
        boolean status = false;
        String[] sourceDb = MasterDriver.testData.get("SourceDBData").split(";");
        FileHandler.on().cleanifFolder(MasterDriver.properties.getProperty("ResultPath"));
        try (Connection sourceConn = DriverManager.getConnection(sourceDb[2],
                sourceDb[0], sourceDb[1]);) {
            MigrateData d = MigrateData.on().fetchTableList();
            for (String table : d.tableList) {
                APP_LOGS.info("For the Table " + table);
                status = MigrateData.on().setTargetData(MasterDriver.conn, sourceConn, table);
            }
        } catch (Exception e) {
            e.printStackTrace();
            APP_LOGS.info(e.getMessage());
        }
        return status;
    }

    public boolean validateCfLoader() {
        boolean status = false;
        try {
            APP_LOGS.info("CF Data Loader Validation");
            status = CfLoaderValidation.on().validateCfLoaderBatch();
        } catch (Exception e) {
            e.printStackTrace();
            APP_LOGS.info(e.getMessage());
        }
        return status;
    }

    public boolean validateDiscountEngine() {
        boolean status = false;
        String[] ficMisDate = MasterDriver.testData.get("FIC_MIS_DATE").split(",");

        for (int i = 0; i < ficMisDate.length; i++) {
            if (MasterDriver.testData.get("Checking Policy").toUpperCase().contains("Y")) {
                MasterDriver.test.log(LogStatus.INFO, "Validation of FSI to FSI Policy Table Validation : " +
                        "for Fic_mis_date " + ficMisDate[i].toUpperCase());
                tableName = "FSI_INS_LC_CNTRCT_";
                if (MasterDriver.testData.get("Re-Insurance").toUpperCase().contains("Y")) {
                    tableName = "FSI_RI_LC_CNTRCT_";
                }
                tableName += MasterDriver.testData.get("LC_objectId");
                if (MasterDriver.testData.get("PolicyNumber").split(",").length == 0) {
                    try {
                        Discountengine.on().fetchAccountNumberandPolicyId(ficMisDate[i].toUpperCase());
                    } catch (Exception e) {
                        APP_LOGS.info(e.getMessage());
                    }
                }
                try {
                    Discountengine.on().validateFsitoFsiwithexcelDB(ficMisDate[i].toUpperCase());
                } catch (Exception e) {
                    e.printStackTrace();
                    APP_LOGS.info(e.getMessage());
                }
            }
            if (MasterDriver.testData.get("Checking Cohort").toUpperCase().contains("Y")) {
                MasterDriver.test.log(LogStatus.INFO, "Validation of FSI to FSI Group Table Validation  " +
                        "for Fic_mis_date " + ficMisDate[i].toUpperCase());
                tableName = "FSI_INS_LC_GRP_";
                if (MasterDriver.testData.get("Re-Insurance").toUpperCase().contains("Y")) {
                    tableName = "FSI_RI_LC_GRP_";
                }
                tableName += MasterDriver.testData.get("LC_objectId");

                if (MasterDriver.testData.get("PolicyNumber").split(",").length == 0) {
                    try {
                        Discountengine.on().fetchAccountNumberandPolicyId(ficMisDate[i].toUpperCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        APP_LOGS.info(e.getMessage());
                    }
                }
                try {
                    Discountengine.on().validateFsitoFsiwithexcelDB(ficMisDate[i].toUpperCase());
                } catch (Exception e) {
                    e.printStackTrace();
                    APP_LOGS.info(e.getMessage());
                }


            }
        }
        return status;
    }
}
