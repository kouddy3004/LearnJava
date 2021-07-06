package baseTest;

import com.relevantcodes.extentreports.LogStatus;
import org.testng.Assert;
import org.testng.annotations.Test;
import reUsables.MasterDriver;


public class WebTestingSuite extends MasterDriver {

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

                    test.log(LogStatus.PASS, "Passed TestCase : " + testCaseId);
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

                    test.log(LogStatus.PASS, "Passed TestCase : " + testCaseId);

                }

            }
        }
    }


    @Test(groups = "Demo")
    public void demoFunction() {
        System.out.println(testData.get("FunctionName"));
        if (testData.isEmpty()) {
            APP_LOGS.info("Unable to Start Execution due to Data Bank ");
            Assert.fail("Failed in Test Case : " + testCaseId);
        } else {
            if (testData.get("TestType").equalsIgnoreCase("Demo")) {
                APP_LOGS.info("Executing Demo Testing ... ");
                test = extentReports.startTest(testCaseId);
                if (url.isEmpty()) {
                    test.log(LogStatus.FAIL, "Unable to Fetch data from the environment ");
                    APP_LOGS.info("Unable to Fetch data from the environment ");
                } else {

                    test.log(LogStatus.PASS, "Passed TestCase : " + testCaseId);

                }
            }
        }
    }

    @Test(groups = "Test")
    public void TestJira() {
        if (testData.isEmpty()) {
            APP_LOGS.info("Unable to Start Execution due to Data Bank ");
            Assert.fail("Failed in Test Case : " + testCaseId);
        } else {
            if (testData.get("TestType").equalsIgnoreCase("Test")) {
                APP_LOGS.info("Executing Test Testing ... ");
                test = extentReports.startTest(testCaseId);
                if (url.isEmpty()) {
                    test.log(LogStatus.FAIL, "Unable to Fetch data from the environment ");
                    APP_LOGS.info("Unable to Fetch data from the environment ");
                }
                test.log(LogStatus.PASS, "Passed TestCase : " + testCaseId);
            }

        }
    }

}
