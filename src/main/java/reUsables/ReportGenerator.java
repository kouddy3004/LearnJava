package reUsables;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.log4j.Logger;
import org.testng.ITestResult;

import java.io.File;

public class ReportGenerator {
    final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    static ReportGenerator obj=new ReportGenerator();;
    private ReportGenerator(){}
    String projectPath=System.getProperty("user.dir");
    public static ReportGenerator on(){ return obj;}

    public ExtentReports createExtentReport(String suiteName){
        MasterDriver.reportPath=MasterDriver.properties.getProperty("ExtentReportPath")+"\\"+suiteName+"_Report.html";
        ExtentReports extentReports = new ExtentReports(MasterDriver.reportPath);
        extentReports
                .addSystemInfo("Environment", "Automation Testing")
                .addSystemInfo("User Name", "Koushik Subramanian");
        extentReports.loadConfig(new File(projectPath + "/src/main/resources/lib/extent-config.xml"));
        return extentReports;
    }

    public void updateStatusinReport(ITestResult result, ExtentTest test){
        if(result.getStatus() == ITestResult.FAILURE){
            test.log(LogStatus.FAIL, "Test Case Failed in "+result.getName());
            test.log(LogStatus.FAIL, "Test Case Failed in "+result.getThrowable());
        }else if(result.getStatus() == ITestResult.SKIP){
            test.log(LogStatus.SKIP, "Test Case Skipped in "+result.getName());
        }
    }
}
