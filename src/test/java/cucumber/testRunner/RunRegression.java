package cucumber.testRunner;



import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        features = ".\\src\\test\\java\\featureFile\\",
        glue= "stepDefinition",
        monochrome = true,
        plugin = {"pretty","html:target/cucumber-reports"}
)
public class RunRegression {

}
