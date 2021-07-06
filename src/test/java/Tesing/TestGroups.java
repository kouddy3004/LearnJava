package Tesing;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class TestGroups {
    public final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

    @Test(groups = {"Smoke"})
    public void SmokeTesting() {
        APP_LOGS.info("Smoke Testing");
    }

    @Test(groups = {"Regression"})
    public void RegressionTesting() {
        APP_LOGS.info("Regression Testing");
    }

    @Test(groups = {"Smoke","Regression"})
    public void bothTesting() {
        APP_LOGS.info("Regression and Smoke Testing");
    }


}
