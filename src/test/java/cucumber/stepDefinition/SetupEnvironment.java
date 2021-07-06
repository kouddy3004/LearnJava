package cucumber.stepDefinition;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class SetupEnvironment {
    public static String setupEnv="No need of Setup";

    @Before("@Calculator")
    public void setCalculator(Scenario scenario){
        System.out.println("No Setup has been needed for Calculator App "+scenario.getName().toString() );
    }
}
