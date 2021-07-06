package reUsables;

import io.cucumber.java.tr.Ve;

import java.io.*;
import java.util.Properties;

public class PropertyHandler {
    static PropertyHandler obj=new PropertyHandler();
    private PropertyHandler(){}
    private static String productName,moduleName,appName,versionID,testType;
    public static PropertyHandler on(String productName,String appName, String versionId,String testType){
        PropertyHandler.productName=productName;
        PropertyHandler.moduleName=moduleName;
        PropertyHandler.appName=appName;
        PropertyHandler.versionID= versionId;
        PropertyHandler.testType=testType;
        return obj;}
    String projectPath=System.getProperty("user.dir");

    public Properties readProperties(String propertyPath)  {
        setProperties(propertyPath);
        Properties prop = null;
        try(FileInputStream fis = new FileInputStream(propertyPath)) {
            prop = new Properties();
            prop.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }

    public void setProperties(String propertyPath){
        Properties properties = new Properties();
        properties.setProperty("ProjectPath", projectPath);
        properties.setProperty("DatabankPath", projectPath + "\\src\\main\\java\\dataBank\\"+productName);
        properties.setProperty("ExtentReportPath", projectPath + "\\ExtentReports");
        properties.setProperty("driverPath", projectPath + "\\src\\main\\resources\\webDrivers");
        properties.setProperty("TestApplicationName", appName);
        properties.setProperty("VersionID", versionID);
        properties.setProperty("PageDirectory",projectPath + "\\src\\main\\java\\"+testType+"\\"
                + productName+"\\pages");
        properties.setProperty("WalletPath",projectPath + "\\src\\main\\resources\\lib\\Wallet_fsmumadw1");
        properties.setProperty("ResultPath", projectPath + "\\src\\main\\java\\"+testType+
                "\\"+productName+"\\result");
        properties.setProperty("JiraDetailsPath", projectPath + "\\src\\main\\java\\jiraDetails");
        properties.setProperty("DbValidationPath",projectPath + "\\src\\main\\java\\"+testType+"\\"
                + productName+"\\validationFiles");
        properties.setProperty("webValidationPath",projectPath + "\\src\\main\\java\\"+testType+"\\"
                + productName+"\\validationFiles");
        try{properties.store(new FileOutputStream(projectPath+ "/src/main/resources/config.properties"), null);}
        catch (IOException e){};
    }



}
