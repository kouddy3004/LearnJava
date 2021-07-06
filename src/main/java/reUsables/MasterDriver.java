package reUsables;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import reUsables.*;
import webAutomation.driver.BrowserDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Properties;


public class MasterDriver {
    public static Properties properties = new Properties();
    private String projectPath = System.getProperty("user.dir");
    private HashMap<String, String> envDetails = new HashMap<>();
    public static HashMap<String, String> testData = new HashMap<>();
    protected static ExtentReports extentReports = null;
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    public static ExtentTest test = null;
    public static HashMap<String, String> dataBank = new HashMap<>();
    public static WebDriver driver = null;
    public static String moduleName = "", appName = "", versionID = "", productName = "", testCaseId = "", testType = "";
    public static Connection conn = null, confConn = null;
    public static String AppServerpass = "";
    public static String AppLoginuser = "";
    public static String Approvepass = "";
    public static String configDBuser = "";
    public static String atomicDBuser = "";
    public static String Approveuser = "";
    public static String EmailSendTo = "";
    public static String Host = "";
    public static String atomicDBpass = "";
    public static String version = "";
    public static String url = "";
    public static String Segment = "";
    public static String sysadmnpass = "";
    public static String AppLoginpass = "";
    public static String sysadmn = "";
    public static String Infodom = "";
    public static String AppServeruser = "";
    public static String db_port = "";
    public static String EmailCCTo = "";
    public static String sysauth = "";
    public static String sysauthpass = "";
    public static String configDBpass = "";
    public static String JDBCString = "";
    public static String DB = "";
    public static String testgroup = "";
    public static String jiraDetailPath = "";
    public static String reportPath = "";

    @BeforeSuite(alwaysRun = true)
    @Parameters({"ProductNameTestNg", "TestApplicationName", "VersionID", "TestType"})
    public void beforeSuite(ITestContext t, String productName, String appName, String versionID, String testType) throws Exception {
        this.productName = productName;
        this.appName = appName;
        this.versionID = versionID;
        this.testType = testType;
        properties = PropertyHandler.on(productName, appName, versionID, testType)
                .readProperties(projectPath + "/src/main/resources/config.properties");
        envDetails = SftpHandler.on().setEnvironmentDetails();
        setEnvData();
        extentReports = ReportGenerator.on().createExtentReport(t.getSuite().getName());
        System.out.println("Wallet Location : " + properties.getProperty("WalletPath"));
        System.setProperty("oracle.net.tns_admin", properties.getProperty("WalletPath"));
        Class.forName("oracle.jdbc.driver.OracleDriver");
        conn = DriverManager.getConnection(envDetails.get("JDBCString"),
                envDetails.get("atomicDBuser"), envDetails.get("atomicDBpass"));
        confConn = DriverManager.getConnection(envDetails.get("JDBCString"),
                envDetails.get("configDBuser"), envDetails.get("configDBpass"));
        APP_LOGS.info("Connections established");

        if (System.getProperty("Trigger_JIRA").toUpperCase().contains("Y")) {
            jiraDetailPath = properties.getProperty("DatabankPath") + "\\jiraDetails.json";
           /* if (System.getProperty("InstanceName").toUpperCase().contains("Y")) {
                FileHandler.on().createFreshFileorFolder(jiraDetailPath, true);
                APP_LOGS.info("Clearing JIraDetails.json");
            }*/
        }
    }

    @Parameters({"browserTestNG", "ModuleNameTestNg", "TestcaseIDTestNG"})
    @BeforeMethod(alwaysRun = true)
    public void beforemETHOD(String browser, String moduleName, String testCaseId) {
        this.moduleName = moduleName;
        this.testCaseId = testCaseId;
        APP_LOGS.info("Fetching Test data for " + testCaseId);
        testData = CommonScripts.on().readExcelByKey(properties.getProperty("DatabankPath") + "\\dataBank.xlsx"
                , moduleName, "TestCaseID", testCaseId);
        testgroup = System.getProperty("groups");
        if (!testData.isEmpty()) {
            if (testgroup.equalsIgnoreCase(testData.get("TestType")) && testType.equals("webAutomation")) {
                driver = BrowserDriver.on().setWebDriver(browser);
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        if (driver != null) {
            driver.quit();
            APP_LOGS.info("Driver Closed");
        }
        ReportGenerator.on().updateStatusinReport(result, test);
        extentReports.endTest(test);
        if (System.getProperty("Trigger_JIRA").toUpperCase().contains("Y")
                && (testgroup.equalsIgnoreCase(testData.get("TestType")))) {
            APP_LOGS.info("Updating Jira");
            JiraHandler jiraHandler = new JiraHandler();
            if (jiraHandler.setJiraRestClient()) {
                String issueKey = "";
                int testResult = 2;
                if (System.getProperty("InstanceName").toUpperCase().contains("Y")) {
                    issueKey = jiraHandler.createIssueInJira();
                    if (!CommonScripts.on().stringIsNullOrEmpty(issueKey)) {
                        jiraHandler.addTestsToCycle(issueKey);
                        jiraHandler.moveCycleToFolder();
                        jiraHandler.putIssueKeyinDataBank(issueKey);
                        if (result.getStatus() == ITestResult.SUCCESS) {
                            testResult = 1;
                        }
                        jiraHandler.updateExecStatusInJIRA(issueKey, testResult);
                    }
                } else {
                    APP_LOGS.info("Only Updation Required " + testCaseId);
                    if (!CommonScripts.on().stringIsNullOrEmpty(testData.get("IssueKey"))) {
                        issueKey = testData.get("IssueKey");
                    } else {
                        issueKey = JsonFileHandler.on().readValueFromJson(jiraDetailPath, testCaseId);
                    }
                    if (!CommonScripts.on().stringIsNullOrEmpty(issueKey)) {
                        if (jiraHandler.addTestsToCycle(issueKey) && jiraHandler.moveCycleToFolder()) {
                            if (result.getStatus() == ITestResult.SUCCESS) {
                                testResult = 1;
                                jiraHandler.pushAttachmentToIssue(issueKey, reportPath);
                            }
                            System.out.println(issueKey + " --> " + testResult);
                            jiraHandler.updateExecStatusInJIRA(issueKey, testResult);
                        } else {
                            APP_LOGS.error("No Jira Details are available");
                        }
                        jiraHandler.logOutOfJira();
                    } else {
                        APP_LOGS.info("No updates in JIRA due to absence of issueKey in databank nor " + jiraDetailPath +
                                " for " + testCaseId);
                    }
                }
            }

        }
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {

    }


    @AfterSuite(alwaysRun = true)
    public void AfterSuite() throws Exception {
        conn.close();
        confConn.close();
        APP_LOGS.info("DB Connection Closed");
        extentReports.flush();
    }


    public void setEnvData() {
        AppServerpass = envDetails.get("AppServerpass");
        AppLoginuser = envDetails.get("AppLoginuser");
        Approvepass = envDetails.get("Approvepass");
        configDBuser = envDetails.get("configDBuser");
        atomicDBuser = envDetails.get("atomicDBuser");
        Approveuser = envDetails.get("Approveuser");
        EmailSendTo = envDetails.get("EmailSendTo");
        Host = envDetails.get("Host");
        atomicDBpass = envDetails.get("atomicDBpass");
        version = envDetails.get("version");
        url = envDetails.get("url");
        Segment = envDetails.get("Segment");
        sysadmnpass = envDetails.get("sysadmnpass");
        AppLoginpass = envDetails.get("AppLoginpass");
        sysadmn = envDetails.get("sysadmn");
        Infodom = envDetails.get("Infodom");
        AppServeruser = envDetails.get("AppServeruser");
        db_port = envDetails.get("db_port");
        EmailCCTo = envDetails.get("EmailCCTo");
        sysauth = envDetails.get("sysauth");
        sysauthpass = envDetails.get("sysauthpass");
        configDBpass = envDetails.get("configDBpass");
        JDBCString = envDetails.get("JDBCString");
        DB = envDetails.get("DB");
    }
}
