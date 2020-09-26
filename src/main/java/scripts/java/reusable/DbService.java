package scripts.java.reusable;

import org.apache.log4j.Logger;
import scripts.java.code.DataExtrapolation;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

public class DbService {

    Statement statement = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd = null;

    public List select(Connection con, String Query,String dateFormat) throws Exception {
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
                        row1.put(rsmd.getColumnName(i), null);
                    } else {
                        int columnType=rsmd.getColumnType(i);
                            if (columnType == Types.TIMESTAMP || columnType==Types.DATE) {
                                if (!dateFormat.isEmpty()) {
                                    Date aDate = rs.getDate(i);
                                    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                                    row1.put(rsmd.getColumnName(i), formatter.format(rs.getDate(i)));
                                }
                            }

                        else {
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
        }
       /* finally {
            rs.close();
            statement.close();
            System.out.println(dbValues);
        }*/
        return dbValues;
    }

    public String createView(String query, String name) throws Exception {
        query = "Create or replace view " + name + " as " + query;
        String view = "Select * from " + name;
        try {
            select(DataExtrapolation.conn, query,"");
            System.out.println("View " + name + " Successfully created");
        } catch (Exception e) {
            System.out.println("Issue in " + name + "Query:\n" + query);
            e.printStackTrace();

        }
        return view;
    }

    public boolean checkValueInDb(Connection conn, String tableName, String columnName, String value) throws Exception {
        boolean check = false;
        String query = "select " + columnName + " from " + tableName + " where to_char(" + columnName + ")='" + value + "'";
        if (select(conn, query,"").size() > 0) {
            check = true;
        }
        return check;
    }

    public boolean insert(Connection conn, String insertQuery) throws Exception {
        boolean check = false;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(insertQuery);
            check = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    public void dropView(String viewName) throws Exception {
        String query = "drop view " + viewName;
        try {
            select(DataExtrapolation.conn, query,"");
            System.out.println("View " + viewName + " Successfully dropped");
        } catch (Exception e) {
            System.out.println("Issue in dropping view " + viewName);
            e.printStackTrace();

        }
    }

    public void executeAndCommit(Connection con, String query) throws Exception {
        Statement statement = null;
        try {
            statement = con.createStatement();
            statement.execute(query);
            con.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            statement.close();
        }

    }

}
