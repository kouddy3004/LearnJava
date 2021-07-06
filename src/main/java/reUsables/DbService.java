package reUsables;

import com.relevantcodes.extentreports.LogStatus;

import org.apache.log4j.Logger;
import org.testng.Assert;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbService {
    final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    Statement statement = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd = null;
    static DbService dbService = new DbService();

    private DbService() {
    }

    public static DbService on() {
        return dbService;
    }

    public List select(Connection con, String Query) throws Exception {
        List<HashMap<String, String>> dbValues = new ArrayList<>();

        try {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "Connected to database");
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Query \n" + Query);
            statement = con.createStatement();
            rs = statement.executeQuery(Query);
            rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount = 0;
            while (rs.next()) {
                HashMap row1 = new HashMap(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    String nullVal = rs.getString(i);
                    if (nullVal == null) {
                        nullVal = "null";
                        row1.put(rsmd.getColumnName(i), nullVal);
                    } else {
                        row1.put(rsmd.getColumnName(i), rs.getString(i));
                    }
                    rowCount = rowCount + 1;
                }//end of while
                dbValues.add(row1);
            }//while
        }//try
        catch (Exception e) {
            String errorMessage = e.getMessage();
            String exceptionTitle = e.getClass().getName();
            APP_LOGS.info(errorMessage);
            APP_LOGS.info(exceptionTitle);
            if (exceptionTitle.equalsIgnoreCase("java.sql.SQLSyntaxErrorException")) {
                if (errorMessage.contains("invalid identifier")) {
                    HashMap<String, String> error = new HashMap<String, String>();
                    error.put("error", "Invalid column");
                    dbValues.add(error);
                } else {
                    MasterDriver.test.log(LogStatus.FAIL, "Failed because of Wrong Query Syntax " + Query);
                    APP_LOGS.info(errorMessage);
                    Assert.fail();
                }
            } else {
                e.printStackTrace();
            }
        } finally {
            rs.close();
            statement.close();

        }
        return dbValues;
    }

    public List select(Connection con, String Query, String dateFormat) throws Exception {
        List<HashMap<String, String>> dbValues = new ArrayList<>();
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(Query);
            rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount = 0;
            while (rs.next()) {
                HashMap row1 = new HashMap(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    String nullVal = rs.getString(i);
                    if (nullVal == null) {
                        nullVal = "null";
                        row1.put(rsmd.getColumnName(i), nullVal);
                    } else {
                        int columnType = rsmd.getColumnType(i);
                        if (columnType == Types.TIMESTAMP || columnType == Types.DATE) {
                            if (!dateFormat.isEmpty()) {
                                Date aDate = rs.getDate(i);
                                SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                                row1.put(rsmd.getColumnName(i), formatter.format(rs.getDate(i)));
                            }
                        } else {
                            row1.put(rsmd.getColumnName(i), rs.getString(i));
                        }
                    }
                    rowCount = rowCount + 1;
                }//end of while
                dbValues.add(row1);
            }//while
        }//try
        catch (Exception e) {
            String errorMessage = e.getMessage();
            String exceptionTitle = e.getClass().getName();
            System.out.println(errorMessage);
            System.out.println(exceptionTitle);
            if (exceptionTitle.equalsIgnoreCase("java.sql.SQLSyntaxErrorException")) {
                if (errorMessage.contains("invalid identifier")) {
                    HashMap<String, String> error = new HashMap<String, String>();
                    error.put("error", "Invalid column");
                    dbValues.add(error);
                    System.out.println(dbValues);
                } else {
                    System.out.println(errorMessage);
                }
            } else {
                e.printStackTrace();
            }
        } finally {
            rs.close();
            statement.close();
            System.out.println(dbValues);
        }
        return dbValues;
    }


    public String createView(String query, String name) throws Exception {
        query = "Create or replace view " + name + " as " + query;
        String view = "Select * from " + name;
        try {
            select(MasterDriver.conn, query);
            APP_LOGS.info("View " + name + " Successfully created");
        } catch (Exception e) {
            APP_LOGS.info("Issue in " + name + "Query:\n" + query);
            MasterDriver.test.log(LogStatus.FAIL, "Issue in \n" + query);
            e.printStackTrace();
            Assert.fail();
        }
        return view;
    }

    public boolean checkValueInDb(Connection conn, String tableName, String columnName, String value) throws Exception {
        boolean check = false;
        String query = "select " + columnName + " from " + tableName + " where to_char(" + columnName + ")='" + value + "'";
        if (select(conn, query).size() > 0) {
            check = true;
        }
        return check;
    }

    public boolean executeDmlStatment(Connection conn, String query) throws SQLException {
        boolean check = false;
        APP_LOGS.info(query);
        Statement statement1 = null;
        try {
            statement1 = conn.createStatement();
            APP_LOGS.info("Number of rows updated : " + statement1.executeUpdate(query));
            check = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            statement1.close();
        }
        return check;
    }

    public void dropView(String viewName) throws Exception {
        String query = "drop view " + viewName;
        try {
            select(MasterDriver.conn, query);
            APP_LOGS.info("View " + viewName + " Successfully dropped");
        } catch (Exception e) {
            APP_LOGS.info("Issue in dropping view " + viewName);
            MasterDriver.test.log(LogStatus.FAIL, "Issue in dropping view " + viewName);
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void executeAndCommit(Connection con, String query) throws Exception {
        Statement statement = null;
        try {
            statement = con.createStatement();
            statement.execute(query);
            con.commit();
        } catch (Exception e) {
            APP_LOGS.info(e.getMessage());
        } finally {
            statement.close();
        }

    }

    public List getColumnNames(Connection con, String Query) throws Exception {
        List<String> dbValues = new ArrayList<>();
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(Query);
            rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount = 0;
            HashMap row1 = new HashMap(0);
            for (int i = 1; i <= columnCount; i++) {
                dbValues.add(rsmd.getColumnName(i));
                rowCount = rowCount + 1;
            }//end of for
        }//try
        catch (Exception e) {
            String errorMessage = e.getMessage();
            String exceptionTitle = e.getClass().getName();
            System.out.println(errorMessage);
            System.out.println(exceptionTitle);
            e.printStackTrace();
        } finally {
            rs.close();
            statement.close();

        }
        return dbValues;
    }


    public boolean insertByMap(Connection conn, List<HashMap<String, String>> data, String table) throws Exception {
        boolean check = false;
        statement = conn.createStatement();
        try {
            for (HashMap<String, String> insert : data) {
                String query = "insert into "
                        + table + "(";
                for (String key : insert.keySet()) {
                    query = query + key + ",";
                }
                query = query.substring(0, query.length() - 1) + ") values(";
                for (String key : insert.keySet()) {
                    query = query + "'" + insert.get(key) + "',";
                }
                query = query.substring(0, query.length() - 1) + ")";
                statement.addBatch(query);
            }
            statement.executeBatch();
            check = true;
            APP_LOGS.info("Inserted data into table " + table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
        }
        return check;
    }


}
