package webAutomation.driver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import reUsables.MasterDriver;

public class BrowserDriver {
    static BrowserDriver obj=new BrowserDriver();
    private BrowserDriver(){}
    public static BrowserDriver on(){return obj;}

    public WebDriver setWebDriver(String browser){
        WebDriver driver=null;
        switch (browser.toUpperCase()) {
            case "CHROME":
                if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
                        System.setProperty("webdriver.chrome.driver", MasterDriver.properties.getProperty("driverPath")
                                +"/chromedriver_linux64");//for chrome
                        //disable web security
                        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                        ChromeOptions options = new ChromeOptions();
                        options.addArguments("test-type");
                        options.addArguments("--disable-web-security");
                        options.addArguments("--allow-running-insecure-content");
                        capabilities.setCapability("chrome.binary", "src/main/java/lib/WebDrivers/chromedriver");
                        if (MasterDriver.url.toUpperCase().contains("HTTPS")) {
                            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                        }
                        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                        driver = new ChromeDriver(capabilities);
                }
                else {
                    System.setProperty("webdriver.chrome.driver", MasterDriver.properties.getProperty("driverPath")
                            +"/chromedriver.exe");
                        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                        ChromeOptions options = new ChromeOptions();
                        options.addArguments("test-type");
                        options.addArguments("--disable-web-security");
                        options.addArguments("--allow-running-insecure-content");
                        capabilities.setCapability("chrome.binary", "src/main/java/lib/WebDrivers/chromedriver.exe");
                        if (MasterDriver.url.toUpperCase().contains("HTTPS")) {
                            //capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                            options.addArguments("ignore-certificate-errors");

                        }
                        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                        driver = new ChromeDriver(capabilities);
                        System.out.println("Chrome Driver inside common funct : " + driver);

                }
                break;
            case "FIREFOX" :
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setCapability("marionette", true);
                FirefoxProfile firefoxProfile;

                if (System.getProperty("Trigger_ZAP_Scan").equalsIgnoreCase("YES")) {
                    Proxy proxy = new Proxy();
                    String proxyHost = "localhost";
                    int proxyPort = 8090;
                    proxy.setHttpProxy(proxyHost + ":" + proxyPort);
                    proxy.setFtpProxy(proxyHost + ":" + proxyPort);
                    proxy.setSslProxy(proxyHost + ":" + proxyPort);
                    DesiredCapabilities capabilities = new DesiredCapabilities();
                    capabilities.setCapability(CapabilityType.PROXY, proxy);

                    //omd.driver = new FirefoxDriver(capabilities);

                    firefoxProfile = new ProfilesIni().getProfile("Zap Profile");
                    System.out.println("Setting zap profle");
                } else {

                    firefoxProfile = new ProfilesIni().getProfile("default");
                    System.out.println("Profile selected");

                }
                System.setProperty("webdriver.gecko.driver", MasterDriver.properties.getProperty("driverPath")
                        + "/geckodriver.exe");

                firefoxOptions.setProfile(firefoxProfile);
                System.out.println("here");
                driver = new FirefoxDriver(firefoxOptions);
                break;

        }
        return driver;
    }
}
