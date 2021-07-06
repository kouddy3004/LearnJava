package dbAutomation.IFRS17.dbOperations;

import org.apache.log4j.Logger;
import reUsables.CommonScripts;
import reUsables.DbService;
import reUsables.JsonFileHandler;
import reUsables.MasterDriver;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MigrateData {
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

    private static MigrateData migrateData = new MigrateData();
    public String[] tableList;

    public static MigrateData on() {
        return migrateData;
    }

    public MigrateData fetchTableList() {
        tableList = JsonFileHandler.on().readValueFromJson(MasterDriver.properties.getProperty("DbValidationPath")
                + "\\table.json", MasterDriver.testCaseId).split(",");
        return migrateData;
    }


    public List<HashMap<String, String>> fetchTableData(Connection conn, String table) throws Exception {
        String query = "select * from " + table;
        List<HashMap<String, String>> data = DbService.on().select(conn, query);
        return data;
    }

    public boolean setTargetData(Connection conn, Connection sourceConn,String table) throws Exception {
        boolean status=false;
        List<HashMap<String, String>> sourceData =
                MigrateData.on().fetchTableData(sourceConn, table);
        String query = "select * from " + table;
        List<String> targetKey = DbService.on().getColumnNames(conn, query);
        List<HashMap<String, String>> targetdata = new ArrayList<>();
        int col=0;
        for (HashMap<String, String> sourceMap : sourceData) {
            HashMap<String, String> ha = new HashMap<>(targetKey.size());
                for (String tkey : targetKey) {
                    ha.put(tkey,"");
                    if (sourceMap.containsKey(tkey)) {
                        ha.put(tkey,sourceMap.get(tkey));
                    }
            }
            targetdata.add(col,ha);
            col++;
        }
        DbService.on().insertByMap(conn,targetdata,table);
        CommonScripts.on().writeExcel(targetdata,
                MasterDriver.properties.getProperty("ResultPath")+"\\targetData\\"+table+".xlsx",table);
        status=true;
        return status;
    }
}
