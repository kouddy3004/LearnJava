package baseTest;

import com.relevantcodes.extentreports.LogStatus;
import dbAutomation.IFRS17.IFRS17_DB;
import org.testng.Assert;
import org.testng.annotations.Test;
import reUsables.MasterDriver;



public class DbTestingSuite extends MasterDriver {

    @Test(groups = "Smoke")
    public void SmokeTesting() throws InterruptedException {
        if (testData.isEmpty()) {
            APP_LOGS.info("Unable to Start Execution due to Data Bank ");
            Assert.fail("Failed in Test Case : " + testCaseId);
        } else {
            if (testData.get("TestType").equalsIgnoreCase("Smoke")) {
                APP_LOGS.info("Executing Smoke Testing ... ");
                test = extentReports.startTest(testCaseId);
                if (url.isEmpty()) {
                    test.log(LogStatus.FAIL, "Unable to Fetch data from the environment ");
                    APP_LOGS.info("Unable to Fetch data from the environment ");
                } else {
                    if (!IFRS17_DB.on().executeFunction()) {

                        Assert.fail("Failed in Test Case : " + testCaseId);
                    }
                    else{
                        test.log(LogStatus.PASS, "Passed TestCase : " + testCaseId);
                    }
                }

            }
        }

    }

    @Test(groups = "Regression")
    public void RegressionTesting() {
        if (testData.isEmpty()) {
            APP_LOGS.info("Unable to Start Execution due to Data Bank ");
            Assert.fail("Failed in Test Case : " + testCaseId);
        } else {
            if (testData.get("TestType").equalsIgnoreCase("Regression")) {
                APP_LOGS.info("Executing Regression Testing ... ");
                test = extentReports.startTest(testCaseId);
                if (url.isEmpty()) {
                    test.log(LogStatus.FAIL, "Unable to Fetch data from the environment ");
                    APP_LOGS.info("Unable to Fetch data from the environment ");
                } else {
                    if (!IFRS17_DB.on().executeFunction()) {
                        Assert.fail("Failed in Test Case : " + testCaseId);
                    } else {
                        test.log(LogStatus.PASS, "Passed TestCase : " + testCaseId);
                    }
                }

            }
        }
    }

}
