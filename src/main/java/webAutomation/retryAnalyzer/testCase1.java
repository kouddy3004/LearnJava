package webAutomation.retryAnalyzer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

public class testCase1{

    public static WebDriver driver ;
    @BeforeSuite
    public void beforeS(){
        System.out.println("BeforeSuit");
    }
    @BeforeMethod
    public void before(){
        System.out.println("Before Test");
    }



    @Test
    public void test(){
        System.out.println("Inside Test" );
        System.setProperty("webdriver.chrome.driver", "src/main/java/lib/WebDrivers/chromedriver.exe");//for chrome
        driver=new ChromeDriver();
        driver.get("http://www.google.com");
        Assert.fail();

    }

    @AfterMethod
    public void after(){
        driver.quit();
        System.out.println("After Method");
        Runtime.getRuntime().gc();
    }

    @AfterSuite
    public void AfterS(){
        System.out.println("After Suite");
    }

        }