package webAutomation.retryAnalyzer;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryFailedTestCases implements IRetryAnalyzer {
    int counter = 1;
    int retryLimit = 2;
    @Override
    public boolean retry(ITestResult result) {
        System.out.println("Inside Retry Analyzer "+counter);

        if(counter < retryLimit)
        {
            counter++;
            return true;
        }
        return false;
    }

}
