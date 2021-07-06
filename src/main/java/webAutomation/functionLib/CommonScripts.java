package webAutomation.functionLib;


import com.csvreader.CsvWriter;
import com.google.common.base.Throwables;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import reUsables.MasterDriver;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import org.openqa.selenium.htmlunit.HtmlUnitDriver;


public class CommonScripts {
    WebDriverWait wait;

    static {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
    }

    final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

    MasterDriver omd = new MasterDriver();

    public static String ReportScreenShot = "";
    public static String ReportSheetName = "";
    public static String filePath = "";
    public static String ScreenshotFileName = "";

    public static Assertion hardAssert = new Assertion();
    public static SoftAssert softAssert = new SoftAssert();

    public static StackTraceElement[] stack_elements = null;
    public static StackTraceElement stack_ele = null;

    public static String calling_class = null;

    public static String N_GL_AMOUNT_RCY = null;
    public static String N_GL_AMOUNT_LCY = null;
    ;




    private CommonScripts(){}
    static CommonScripts obj=new CommonScripts();
    public static CommonScripts on(){return obj;}
    /**
     * '###############################################################
     * 'Function Name        : FnGetRandomNumber
     * 'Function Description : To Create Random Numbers
     * 'Input Parameters     :
     * '                     :
     * 'Output Parameters    :
     * '################################################################
     */
    public int FnGetRandomNumber() {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(100);
        return randomInt;
    }

    /**
     * Establishes DB Connection and returns connection object
     *
     * @param DBUSERID
     * @param DBPASSWORD
     * @param Driver
     * @param JDBCString
     * @return connection Object
     */

    /**
     * '##############################################################################
     * ' 				         Function Name: FnGetCurrentTimeStamp
     * '			      Function Description: To get the current date
     * '        		      Input Parameters: NA
     * '				     Output Parameters: FormattedDate
     * '################################################################################
     */
    public String FnGetCurrentTimeStamp() throws Exception {

        Date date = new Date();
        SimpleDateFormat dfm = new SimpleDateFormat("dd/MMMM/yyyy hh:mm:ss");
        //APP_LOGS.info(dfm.format(date));
        String FormattedDate = dfm.format(date);
        return FormattedDate;
    }


    public void FnWrite(String sFileName, String sWrite) throws Exception {
        try {
            File oFile = new File(sFileName);
            /* Opening a file in append mode */
            FileWriter writer = new FileWriter(oFile, true);
            writer.append(sWrite);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            APP_LOGS.info("Exception in FnWrite");
            throw new Exception();
        }
    }

    public void clearCsv(String fileName) throws Exception {
        try {
            File oFile = new File(fileName);
            /* Opening a file in append mode */
            FileWriter writer = new FileWriter(oFile, false);
            PrintWriter pw = new PrintWriter(writer, false);
            pw.flush();
            pw.close();
            writer.close();
        } catch (IOException e) {
            APP_LOGS.info("Exception in FnWrite");
            throw new Exception();
        }
    }

    public void setCsvHeader(String fileName, String headers) {
        try {
            CsvWriter csvWriter = new CsvWriter(fileName);
            String[] header = headers.split(",");
            if (header.length > 1) {
                csvWriter.writeRecord(header);
            } else {
                csvWriter.write(header[0]);
            }
            csvWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * '###############################################################
     * 'Function Name        : FnGetUniqueIdSec
     * 'Function Description : Creates Unique ID
     * 'Input Parameters     :
     * '                     :
     * 'Output Parameters    :
     * <p>
     * '################################################################
     */
    public String FnGetUniqueIdSec() {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
        //ddMMyyHHmmss
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * '###############################################################
     * 'Function Name        : FnSetText
     * 'Function Description : inserts the text into the textbox
     * 'Input Parameters     : Xpath or by
     * '                     :
     * 'Output Parameters    :
     * <p>
     * '################################################################
     */
    public void FnSetText(By by_element, String value) throws Exception {
        try {
            //Initiate explicit wait
            wait = new WebDriverWait(omd.driver, 20);

            //############ Function implementation ###################

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            //Clear the text
            omd.driver.findElement(by_element).clear();
            //enter the text
            omd.driver.findElement(by_element).sendKeys(value);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {
            //omd.driver.close();
            //omd.driver.quit();
            //############ Common lines for each function-> Catch to report the exception with function name	##########
            ////Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            Assert.fail();
            throw new NoSuchElementException(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
        }

    }

    /**
     * '###############################################################
     * 'Function Name        : FnElementClick
     * 'Function Description : To click on a element
     * 'Input Parameters     : By of the element
     * 'Output Parameters    :	NA
     * <p>
     * '################################################################
     */

    public void FnElementClick(By by_element) throws Exception {

        //////////////////////////////////////////////////////////
        //Initiate explicit wait

        wait = new WebDriverWait(omd.driver, 20);
        try {

            //Explicit wait until the element is located  -->(wait.until(ExpectedConditions.elementToBeClickable(by_element))
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            omd.driver.findElement(by_element).click();
            //Thread.sleep(90);
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            ////Logger to log function name and status

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //softAssert.assertTrue(false, new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            Assert.fail();
            throw new NoSuchElementException(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
        }

    }

    /**
     * '###############################################################
     * 'Function Name        : FnElementDblClick
     * 'Function Description : To Double click on a element
     * 'Input Parameters     : By of the element
     * 'Output Parameters    :	NA
     * <p>
     * '################################################################
     */
    public void FnElementDblClick(By by_element) throws Exception {
        //////////////////////////////////////////////////////////
        //Initiate explicit wait

        wait = new WebDriverWait(omd.driver, 20);
        try {

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            Actions action = new Actions(omd.driver);
            action.moveToElement(omd.driver.findElement(by_element)).doubleClick().build().perform();
            Thread.sleep(90);
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            ////Logger to log function name and status

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //softAssert.assertTrue(false, new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            Assert.fail();
            throw new NoSuchElementException(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
        }
    }





    /**
     * '##############################################################################
     * 'Function Name        : FnClearText
     * 'Function Description : Clears the Text in the text box
     * 'Input Parameters     : Web Element(Which is a Text box)
     * '                     :
     * 'Output Parameters    :
     * <p>
     * '################################################################################
     *
     * @throws Exception
     * @throws SecurityException
     */
    public void FnClearText(By by_element) throws SecurityException, Exception {
        try {
            wait = new WebDriverWait(omd.driver, 20);
            wait.until(ExpectedConditions.elementToBeClickable(by_element));
            WebElement toClear = omd.driver.findElement(by_element);
            toClear.sendKeys(Keys.CONTROL + "a");
            toClear.sendKeys(Keys.DELETE);
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");


            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            Assert.fail();
            throw new NoSuchElementException(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
        }
    }

    /**
     * '##############################################################################
     * 'Function Name        : FnCheckObjectExists
     * 'Function Description : Checks the object existence
     * 'Input Parameters     : Web Element
     * '                     :
     * 'Output Parameters    :
     * <p>
     * '################################################################################
     */
    public boolean FnCheckObjectExists(By by_element) throws Exception {
        Boolean exists = false;
        try {

            //Initiate explicit wait
            wait = new WebDriverWait(omd.driver, 2);

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            exists = true;
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //FnTestCaseStatusReport("FAIL", new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //omd.test.log(LogStatus.FAIL,new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            Assert.fail();
            //throw new NoSuchElementException(new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
        } finally {
            return exists;
        }
    }

    /**
     * '##############################################################################
     * 'Function Name        : FnGetText
     * 'Function Description : Returns the text provided in the xpath or webelement
     * 'Input Parameters     : Web Element
     * '                     :
     * 'Output Parameters    :
     * <p>
     * '################################################################################
     */
    public String FnGetText(By by_element) throws Exception {
        String txt = "";
        try {
            wait = new WebDriverWait(omd.driver, 10);

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            txt = omd.driver.findElement(by_element).getText();
            APP_LOGS.info("txt is->" + txt);
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");


            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            Assert.fail();
            throw new NoSuchElementException(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
        }
        return txt;
    }

    /**
     * '##############################################################################
     * 'Function Name        : FnGetAttributeText
     * 'Function Description : Returns the text provided in the xpath or webelement
     * 'Input Parameters     : Web Element,attribute
     * '                     :
     * 'Output Parameters    :
     * <p>
     * '################################################################################
     */
    public String FnGetAttributeText(By by_element, String attribute) throws Exception {
        String txt = "";
        try {
            wait = new WebDriverWait(omd.driver, 20);

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            txt = omd.driver.findElement(by_element).getAttribute(attribute);
            APP_LOGS.info("txt is->" + txt);
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            Assert.fail();
            throw new NoSuchElementException(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
        }
        return txt;
    }

    /**
     * '###############################################################
     * 'Function Name        : FnSelectDropdown
     * 'Function Description : To select a value from drop down
     * 'Input Parameters     : By of the element(Select), Text to select from drop down
     * 'Output Parameters    :	NA
     * <p>
     * /*'###############################################################
     */
    public void FnSelectDropdown(By by_element, String text) throws Exception {
        try {
            wait = new WebDriverWait(omd.driver, 20);
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            APP_LOGS.info("Drop down txt==>" + text);
            Select select = new Select(omd.driver.findElement(by_element));
            select.selectByVisibleText(text);
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            ;

            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            Assert.fail();
            throw new NoSuchElementException(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
        }
    }

    /**
     * '###############################################################
     * 'Function Name        : FnSwitchToWindowInstance
     * 'Function Description : To switch to different windows
     * 'Input Parameters     : int:window instance
     * 'Output Parameters    :	NA
     * <p>
     * '################################################################
     */
    public void FnSwitchToWindowInstance(int window_instance) throws Exception {
        try {
            String[] windows = new String[5];

            //to get the current window instances
            Set<String> all_windows = omd.driver.getWindowHandles();

            int no_of_windws = all_windows.size();

            int i = 0;

            APP_LOGS.info("no_of_windws" + no_of_windws);

            //If window instance to switch is valid
            if (window_instance <= no_of_windws) {

                Iterator<String> it = all_windows.iterator();

                //Iterate to point to the required window instance
                while (it.hasNext() && i < no_of_windws) {

                    windows[i] = it.next();
                    i++;
                }
                APP_LOGS.info("i=>" + i);

                //Switch the driver to required instance
                omd.driver.switchTo().window(windows[window_instance - 1]);

                //Logger to log function name and status
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->pass");

            } else {

                //Logger to log function name and status
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->fail. Window insatnce " + window_instance + " is not present");

                Assert.fail();
            }

        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "-> Exception");


            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail();
        }
    }

    /**
     * '###############################################################
     * 'Function Name        : FnSwitchToWindow
     * 'Function Description : To switch to different windows
     * 'Input Parameters     : int:window instance
     * 'Output Parameters    :	NA
     * <p>
     * '################################################################
     */
    public String FnSwitchToWindow() throws Exception {
        try {
            for (String handle1 : omd.driver.getWindowHandles()) {
                omd.driver.switchTo().window(handle1);
                Thread.sleep(1000);
                APP_LOGS.info("Window name-->" + omd.driver.getTitle());
                omd.test.log(LogStatus.PASS, "Switched to window->" + omd.driver.getTitle() + "success");
            }
        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "-> Exception");

            //FnTestCaseStatusReport("FAIL", new Object (){}.getClass().getEnclosingMethod().getName() + "-> Failed");

            //omd.test.log(LogStatus.FAIL,"Switched to window->" + omd.driver.getTitle() + "Failed");
            //Assert.fail();
            //throw new NoSuchWindowException("Switched to window->" + omd.driver.getTitle() + "Failed");
        } finally {
            APP_LOGS.info("No More Windows to Switch >> Exception handled");
        }
        return omd.driver.getTitle();
    }

    /**
     * '###############################################################
     * 'Function Name        : FnSwitchToWindowUsingName
     * 'Function Description : To switch to different windows
     * 'Input Parameters     : int:window instance
     * 'Output Parameters    :	NA
     * <p>
     * '################################################################
     */
    public String FnSwitchToWindowUsingName(String windowName) throws Exception {
        try {
            for (String handle1 : omd.driver.getWindowHandles()) {

                if (omd.driver.switchTo().window(handle1).getTitle().equals(windowName)) {
                    omd.driver.switchTo().window(handle1);
                    Thread.sleep(1000);
                    APP_LOGS.info("Window name-->" + omd.driver.getTitle());
                    omd.test.log(LogStatus.PASS, "Switched to window->" + omd.driver.getTitle() + "success");
                    break;
                }
            }
        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "-> Exception");
        } finally {
            APP_LOGS.info("No More Windows to Switch >> Exception handled");
        }
        return omd.driver.getTitle();
    }

    /**
     * '###############################################################
     * 'Function Name        : FnOJETCalendar
     * 'Function Description : To switch to different windows
     * 'Input Parameters     : int:window instance
     * 'Output Parameters    :	NA
     * <p>
     * '################################################################
     */
    public void FnOJETCalendar() throws Exception {

    }

    /**
     * '###############################################################
     * 'Function Name        : FnSwitchToFrame
     * 'Function Description : To switch to different frame
     * 'Input Parameters     : String: frame_id
     * 'Output Parameters    :	NA
     * <p>
     * '################################################################
     */
    public void FnSwitchToFrame(By by_element_iframe) throws Exception {
        try {
            //Initiate explicit wait
            wait = new WebDriverWait(omd.driver, 20);

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element_iframe));


            //Switch the driver to required frame
            omd.driver.switchTo().frame(omd.driver.findElement(by_element_iframe));
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element_iframe + "->PASS");
            omd.test.log(LogStatus.PASS, "Switched to frame->" + by_element_iframe + "success");
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element_iframe + "->Object Not found");


            omd.test.log(LogStatus.FAIL, "Switched to frame->" + by_element_iframe + "Failed");

            Assert.fail();
            throw new NoSuchFrameException("Switched to frame->" + by_element_iframe + "Failed");
        }
    }


    public String SendMail() throws AddressException, Exception {
        String status = "";
        String host = "internal-mail-router.oracle.com";
        int port = 25;
        String auth = "true";
        final String user = "IFRS17_Automation@oracle.com";//change accordingly
        //final String password="Closeenough123";//change accordingly
        try {
            //String sendto=omd.esend;//change accordingly
            String sendto = "chippy.jacob@oracle.com";
            String name[] = sendto.split("@");
            //APP_LOGS.info("*********name is :"+name[0]+"************");

            //String cc = omd.eCCsend;
            //String cc="divya.srinivasan@oracle.com,neethu.puthalath@oracle.com,sreenivasula.yr@oracle.com,apoorva.jayanna@oracle.com,churchill.gaur@oracle.com,linoj.moopan@oracle.com,brinda.ganesh@oracle.com,deepthi.santosh@oracle.com,shilpashree.srinivas@oracle.com,cathrine.cruz@oracle.com,priya.k.kumari@oracle.com,koushik.subramanian@oracle.com,raghuveer.bh@oracle.com";
            String cc = "divya.srinivasan@oracle.com,neethu.puthalath@oracle.com,apoorva.jayanna@oracle.com,churchill.gaur@oracle.com,koushik.subramanian@oracle.com,raghuveer.bh@oracle.com";
            String[] ccrecipientList = cc.split(",");
            InternetAddress[] ccrecipientAddress = new InternetAddress[ccrecipientList.length];
            int counter = 0;
            for (String recipient : ccrecipientList) {
                ccrecipientAddress[counter] = new InternetAddress(recipient.trim());
                counter++;
            }

            //Get the session object
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", auth);
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.smtp.ssl.trust", "*");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, "Closeenough123");
                }
            });

            //Compose the message

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendto));
            message.addRecipients(Message.RecipientType.CC, ccrecipientAddress);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            message.setSubject("IFRS17 Automation :: Email Notification");
            // Now set the actual message
            // messageBodyPart.setText("This is message body");

            //message.setText("This is simple program of sending email using JavaMail API");
            messageBodyPart.setContent("<!DOCTYPE html>"
                            + " <html>"
                            + " <head>"
                            + " <style> h1 { color: block; text-align: center; } p {   font-family: \"Calibri\"; font-size: 18px; } </style>"
                            + " </head> "
                            + " <body>"
                            + " <h1>IFRS17 " + omd.versionID + " Automation Test Report</h1>"
                            + " <p>Hello , </p>"
                            + " <p> The enclosed document summarizes the IFRS17 " + omd.versionID + " Automation Test Run results.</p>"
                            //+ " <p>Smoke Testing  is being done whenever a Build is received (deployed into Test environment) for Testing to make sure the major functionality are working fine, Build can be accepted and Testing can start.</p>"
                            + " <p>JIRA Status Report: </p>"
                            + " <p>https://jira.oraclecorp.com/jira/projects/OFSIIA?jwupdated=41264&selectedItem=com.thed.zephyr.je:zephyr-tests-page#test-cycles-tab</p>"
                            + " <p>Confluence Dashboard Report: https://confluence.oraclecorp.com/confluence/display/I17A/TestDashboard</p>"


                            + " <p> p/s : This is an automated email , please do not reply</p> "
                            + " <p> Please find the env details : </p>"
                            + " <p>URL : " + omd.properties.getProperty("url") + "</p>"


                            + "  <p>Regards , </p>"
                            + " <p>IFRS17 UI Automation Team </p>"
                            + " </body>"
                            + " </html>"
                    , "text/html");

            // Create a multipar message*
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = MasterDriver.reportPath;
            DataSource source = new FileDataSource(filename);

            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

			/*if(status.equals("FAIL"))
         {
	        //attach log file also
	         Date date = new Date();
	 		 SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
	 		 //APP_LOGS.info(dfm.format(date));
	 		 String FormattedDate =dfm.format(date);

	 		 APP_LOGS.info("getAllAppenders-->OFSAA_AUTOMATION_LOG_"+FormattedDate+".log");
	 		 //org.apache.log4j.Logger.shutdown();

	 		 filename =omd.RepoName+"/logs/OFSAA_AUTOMATION_LOG_"+FormattedDate+".log";///OFSAA_AUTOMATION_CICD/target/surefire-reports/emailable-report.html
	         source = new FileDataSource(filename);

	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);
         }*/
            // Send the complete message parts
            message.setContent(multipart);

            //send the message
            Transport.send(message);

            APP_LOGS.info(" EMAIL message sent successfully...");
            status = "sent successfully";

        } catch (MessagingException e) {
            APP_LOGS.info("Message is-->" + e.getMessage());
        }
        return status;
    }


    public String get_log_path() throws Exception {
        String path = MasterDriver.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        String temp_log_file_path = "";
        //APP_LOGS.info("path:"+decodedPath);
        String log_path[] = decodedPath.split("target");
        //APP_LOGS.info("log_path=>"+log_path[0]);

        Enumeration enum1 = Logger.getRootLogger().getAllAppenders();
        while (enum1.hasMoreElements()) {
            Appender app = (Appender) enum1.nextElement();
            if (app instanceof FileAppender) {

                //System.out.println("File: " + ((FileAppender)app).getFile());
                temp_log_file_path = ((FileAppender) app).getFile();
            }
        }

        String log_file_path = log_path[0] + temp_log_file_path;
        APP_LOGS.info("log_file_path=>" + log_file_path);

        return log_file_path;
    }

    public void Close_windows() {
        try {
            Set<String> windows = omd.driver.getWindowHandles();
            Iterator<String> iter = windows.iterator();
            String[] winNames = new String[windows.size()];
            int i = 0;
            while (iter.hasNext()) {
                winNames[i] = iter.next();
                i++;
            }

            if (winNames.length > 1) {
                for (i = winNames.length; i > 1; i--) {
                    omd.driver.switchTo().window(winNames[i - 1]);
                    omd.driver.close();
                }
            }
            omd.driver.switchTo().window(winNames[0]);
            //omd.driver.close();
            omd.driver.quit();

        } catch (Exception e) {
            APP_LOGS.info("No Windows to close");
        }
    }

    /**
     * to upload files from system
     *
     * @param file_path
     * @throws Exception
     */
    public void file_upload(String file_path) throws Exception {
        StringSelection ss = new StringSelection(file_path);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

        //native key strokes for CTRL, V and ENTER keys
        Robot robot = new Robot();

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

    }

    /**
     * fnScrollPageDown
     * Scrolls the Page
     */
    public void fnScrollPage(By xpath) throws Exception {
        try {
            APP_LOGS.info("Scrolling");
            APP_LOGS.info("xpath==>" + xpath);
            JavascriptExecutor jse = (JavascriptExecutor) omd.driver;
            WebElement ele = omd.driver.findElement(xpath);
            jse.executeScript("arguments[0].scrollIntoView(true);", ele);
            Thread.sleep(5000);
            APP_LOGS.info("Scrolling ==>done");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            APP_LOGS.info("fnScrollPage=>Exception");
        } finally {
            APP_LOGS.info("fnScrollPage=>Exception handled Finallay block");
        }
    }


    public String Chrome_alert_getText() {
        String alert_text = "";
        try {
            Alert alert = omd.driver.switchTo().alert();
            alert_text = alert.getText();
            APP_LOGS.info("Alert text is-->" + alert_text);
        } catch (Exception e) {
            APP_LOGS.info("in commonscript ->alert is not present");
        }
        return alert_text;
    }

    public boolean isAlertPresent_chrome() throws Exception {
        boolean foundAlert = false;
        wait = new WebDriverWait(omd.driver, 10 /*timeout in seconds*/);
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            foundAlert = true;
            APP_LOGS.info("alert is there");
            return foundAlert;
        } catch (Exception eTO) {
            APP_LOGS.info("alert is not present");
            foundAlert = false;
            return foundAlert;
        }
    }

    public String FnCopyAlert(Robot rb) throws Exception {

        //Robot robot = new Robot();

        //robot.keyRelease(KeyEvent.VK_COPY);
        APP_LOGS.info("************");
        rb.keyPress(KeyEvent.VK_D);

        String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        APP_LOGS.info("Alert mesage data -->" + data);
        return data.trim();
    }

    public void fnCalender_Select_JSPScreen(String Month, String Year, String Date) throws Exception {
        FnSelectDropdown(By.xpath(".//*[@class='calander']//select[@id='monthList']"), Month);
        Thread.sleep(2000);
        FnSelectDropdown(By.xpath(".//*[@class='calander']//select[@id='yearList']"), Year);
        if (FnCheckObjectExists(By.xpath("html/body//a/font[.='" + Date + "']"))) {
            FnElementClick(By.xpath("html/body//a/font[.='" + Date + "']"));
        }
    }

    public void fnScrollDiv(By xpath) throws Exception {
        APP_LOGS.info("fnScrollDiv = >xpath = >" + xpath);
        Actions clickAction = new Actions(omd.driver);
        WebElement scrollablePane = omd.driver.findElement(xpath);
        clickAction.moveToElement(scrollablePane).click().build().perform();

        Actions scrollAction = new Actions(omd.driver);
        scrollAction.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform();
        Thread.currentThread().sleep(5000);
    }



    ////**************************************ZAP Functions****************************////////////////

    //static String alertDataFilePath = System.getProperty("user.home") + File.separator + "Report.html";

    public void createSession(ClientApi api, String SESSION_NAME) throws ClientApiException {
        // Create session
        APP_LOGS.info("Creating session... " + SESSION_NAME);
        api.core.newSession((String) SESSION_NAME, "true");
        APP_LOGS.info("Created session... " + SESSION_NAME);
    }


    /**
     * '###############################################################
     * 'Function Name        : Fnclearcachebrowser
     * 'Function Description : clear browser cache
     * 'Input Parameters     : NA
     * 'Output Parameters    :    NA
     * <p>
     * '################################################################
     */
    public void Fnclearcachebrowser(String cacheURL) throws Exception {

        APP_LOGS.info("Fnclearcachebrowser");
        String Clearcache_URL = cacheURL;
        try {
            //Navigate to the URL
            APP_LOGS.info("omd.driver--->" + omd.driver);
            APP_LOGS.info("cacheURL->" + cacheURL);
            omd.driver.get(cacheURL);
            omd.driver.findElement(By.xpath("//settings-ui")).sendKeys(Keys.ENTER);
            omd.driver.manage().deleteAllCookies();
            APP_LOGS.info("clearcache is done successfully");
            omd.test.log(LogStatus.PASS, "clearcache is done successfully");
        } catch (Exception e) {
            APP_LOGS.info("Exception in Fnclearcachebrowser");
            throw new Exception();
        }
    }

    public Boolean Fn_DefnitionExists(String elename, By by_element) throws InterruptedException {

        omd.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        //Function variables
        Boolean exists = false;

        //try{

        Thread.sleep(1000);
        int elesize = omd.driver.findElements(by_element).size();
        if (!(elesize == 0)) {
            exists = true;
            omd.test.log(LogStatus.INFO, elename + " present in the UI");
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } else {
            omd.test.log(LogStatus.SKIP, elename + " does not exist");
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->skip");
            throw new SkipException("Not running as the Models doesnot Exist");
        }


		/* }catch(Exception e){

			APP_LOGS.info(new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
			omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName()+"Exception Occured-> Fail");
			Assert.fail(Throwables.getStackTraceAsString(e));

		}*/

        return exists;
    }

    public void Fn_ElementNotExists(String elename, By by_element) {

        try {

            int elesize = omd.driver.findElements(by_element).size();
            if (elesize == 0) {
                omd.test.log(LogStatus.PASS, elename + " Not present in the UI as expected");
                //Logger to log function name and status
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            } else {
                omd.test.log(LogStatus.WARNING, elename + " present in the UI");
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->fail");
                Assert.fail("Element Exists in UI");
            }


        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
    }

    public Boolean Fn_NotExists(String elename, By by_element) {


        //Function variables
        Boolean exists = false;

        try {

            boolean elesize = omd.driver.findElement(by_element).isDisplayed();
            if (!elesize) {
                exists = true;
                omd.test.log(LogStatus.PASS, elename + " Not present in the UI as expected");
            } else {
                omd.test.log(LogStatus.WARNING, elename + " present in the UI");
            }


            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");


        }

        return exists;

    }


    public void Fn_VerifyCondition(Boolean Condition, String SucessMsg, String errorMsg) {

        try {
            if (Condition) {

                //System.out.println("Pop up msg matches");

                omd.test.log(LogStatus.PASS, "Condition ->" + Condition.toString() + "," + SucessMsg + "-> Pass");
                APP_LOGS.info("Condition ->" + Condition.toString() + "," + SucessMsg + "-> Pass");
            } else {
                System.out.println("Pop up msg mis-match");
                omd.test.log(LogStatus.FAIL, "Condition ->" + Condition.toString() + "," + errorMsg + "-> Fail");
                APP_LOGS.info("Condition ->" + Condition.toString() + "," + errorMsg + "-> Fail");
                //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->act msg is->" + actual_msg + "->exp msg->"+ exptedMsg +"->mis-matched");

                Assert.fail("Condition doesn't Match");

            }

        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "-> failed");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            //Assert.fail();
            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }

    }


    public boolean Fn_verifyMsg(By element, String exptedMsg, String errorMsg) {

        //Function variables
        String actual_msg = null;
        boolean status = false;
        try {
            actual_msg = Fn_getText(element);

            System.out.println("Actual msg->" + actual_msg);
            System.out.println("Expected msg->" + exptedMsg);

            if (actual_msg.trim().contains(exptedMsg.trim())) {

                System.out.println("Pop up msg matches");

                omd.test.log(LogStatus.PASS, "Message ->" + actual_msg + "-> Pass");
                APP_LOGS.info("Message ->" + actual_msg + "-> Pass");
                status = true;
            } else {
                System.out.println("Pop up msg mis-match");
                omd.test.log(LogStatus.FAIL, "Message ->" + actual_msg + "-> Fail");
                omd.test.log(LogStatus.FAIL, "Expected Message ->" + exptedMsg);
                APP_LOGS.info("Message ->" + actual_msg + "-> Fail");
                APP_LOGS.info("Expected Message ->" + exptedMsg);
                status = false;
                //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->act msg is->" + actual_msg + "->exp msg->"+ exptedMsg +"->mis-matched");
                //Assert.fail("Pop up Message didnt match");

            }

        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "-> failed");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            //Assert.fail();
            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }
        return status;
    }

    public void Fn_CompareValue(By element, String value) {

        try {

            System.out.println("Value->" + value);

            if (omd.driver.findElement(element).getText().equalsIgnoreCase(value)) {

                System.out.println("Value present");
                omd.test.log(LogStatus.PASS, "Message ->" + value + " present-> Pass");
                APP_LOGS.info("Message ->" + "Message ->" + value + " present-> Pass");
            }


        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "-> failed");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            //Assert.fail();
            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }
    }

    public Boolean Fn_Disabled(String elename, WebElement element) {


        //Function variables
        Boolean exists = false;

        try {

            boolean disable = element.isEnabled();

            if (!(disable)) {
                exists = true;
                omd.test.log(LogStatus.PASS, elename + " is disbaled in the UI as expected");

            } else {

                omd.test.log(LogStatus.WARNING, elename + " is enabled in the UI");
                Assert.fail("Element is Enabled");
            }

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + element + "->pass");


        } catch (Exception e) {


            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + element + "->exception");

            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));

        }

        return exists;

    }

    public Boolean Fn_Disabled(String elename, By by_element) {

        //Function variables
        Boolean exists = false;

        try {

            boolean disable = omd.driver.findElement(by_element).isEnabled();

            if (!(disable)) {
                exists = true;
                omd.test.log(LogStatus.PASS, elename + " is disabled in the UI as expected");

            } else {
                omd.test.log(LogStatus.FAIL, elename + " is enabled in the UI");
                Assert.fail("Element " + elename + " should not be enabled.");
            }

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {


            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");

            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));

        }

        return exists;

    }

    /**
     * @param elename
     * @param by_element
     * @param attr       (readonly or aria-readonly)
     * @return
     */
    public Boolean Fn_jsDisabled(String elename, By by_element, String attr) {
        //Function variables
        Boolean exists = false;

        try {
            boolean disable = Fn_getAttribute(by_element, attr).contains("true");

            if (disable) {
                exists = true;
                omd.test.log(LogStatus.PASS, elename + " is disabled in the UI as expected");

            } else {
                omd.test.log(LogStatus.WARNING, elename + " is enabled in the UI");
                Assert.fail("Element " + elename + " is Enabled");
            }

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
        return exists;
    }

    public Boolean Fn_Enabled(String elename, By by_element) {


        //Function variables
        Boolean exists = false;

        try {

            boolean disable = omd.driver.findElement(by_element).isEnabled();

            if ((disable)) {
                exists = true;
                omd.test.log(LogStatus.PASS, elename + " is enabled in the UI as expected");

            } else {

                omd.test.log(LogStatus.WARNING, elename + " is disabled in the UI");
                Assert.fail("Element is Enabled in UI");
            }

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {


            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");

            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));

        }

        return exists;

    }

    public By Fn_getcustomxpath(String LEName) throws IOException {
        String element = "//div";
        By by_element = null;
        String[] EleArr;

        try {
            EleArr = LEName.split(" ");
            for (int i = 0; i < EleArr.length; i++) {
                APP_LOGS.info(EleArr[i]);
                element += "[contains(text(),'" + EleArr[i].trim() + "')]";
                APP_LOGS.info(element);
            }

            by_element = By.xpath(element);
            APP_LOGS.info("Xpath: " + by_element);
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
        return by_element;
    }

    public void Fn_click(By by_element) throws Exception {
        try {
            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 30);
            //Explicit wait until the element is located
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            // wait the element  to become stale
            wait.until(ExpectedConditions.visibilityOf(element));
            wait.until(ExpectedConditions.elementToBeClickable(by_element));
            // click on "Add Item" once the page is reloaded
            APP_LOGS.info("Element to click : " + by_element);
            //omd.driver.findElement(by_element).click();
            element.click();
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } catch (Exception e) {
            APP_LOGS.info(e.getMessage());
            if (e.getMessage().contains("stale element reference:")) {
                APP_LOGS.info("Inside Stale Element Exception");
                boolean result = false;
                int attempts = 0;
                while (attempts < 2) {
                    try {
                        Thread.sleep(400);
                        MasterDriver.driver.findElement(by_element).click();
                        result = true;
                        break;
                    } catch (Exception ex) {
                        APP_LOGS.info("From Stal Exception Hansle : " + ex.getMessage());
                    }
                    attempts++;
                }

            } else if (e.getMessage().contains("element click intercepted:")) {
                APP_LOGS.info(handleInterceptElementException(by_element));
            } else {
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
                ////cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
                omd.test.log(LogStatus.FAIL, new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

                e.printStackTrace();

                //Assert.fail() to report in TestNG report
                Assert.fail(Throwables.getStackTraceAsString(e));
            }
        }

    }

    public boolean handleInterceptElementException(By by) {
        APP_LOGS.info("Inside Stale Element Exception");
        boolean result = false;
        int attempts = 0;
        while (attempts < 2) {
            try {
                WebElement element = MasterDriver.driver.findElement(by);
                Actions actions = new Actions(MasterDriver.driver);
                actions.moveToElement(element).click().build().perform();
                result = true;
                break;
            } catch (ElementClickInterceptedException e) {
                APP_LOGS.info("Element Click Exception Occurs");
            }
            attempts++;
        }
        return result;
    }

    public boolean handleStaleElementException(By by) {
        APP_LOGS.info("Inside Stale Element Exception");
        boolean result = false;
        int attempts = 0;
        while (attempts < 2) {
            try {
                Thread.sleep(400);
                MasterDriver.driver.findElement(by).click();
                result = true;
                break;
            } catch (Exception e) {
                APP_LOGS.info(e.getMessage());
            }
            attempts++;
        }
        return result;
    }

    public void Fn_click_1(By by_element) throws Exception {

        try {

            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 30);

            //Explicit wait until the element is located
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            //wait.until(ExpectedConditions.elementToBeClickable(element));
            Thread.sleep(1000);
            APP_LOGS.info("Element Present");
           /* JavascriptExecutor jse = (JavascriptExecutor) omd.driver;
            jse.executeScript("arguments[0].click()", element);*/
            element.click();
            //omd.driver.findElement(by_element).click();

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } catch (Exception e) {
            String eTitle = e.getClass().getName();
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");

            ////cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            omd.test.log(LogStatus.ERROR, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "-> " + eTitle);

            APP_LOGS.info("Exception Title : " + eTitle);
            e.printStackTrace();
            if (eTitle.contains("ElementNotInteractableException")) {
                Thread.sleep(1000);
                MasterDriver.driver.findElement(by_element).click();
            } else {
                //Assert.fail() to report in TestNG report
                Assert.fail(Throwables.getStackTraceAsString(e));
            }
        }

    }

	/*'###############################################################
	'Function Name        	: Fn_setText
	'Function Description	: To enter text in the Text field
	'Input Parameters    	: By of the element, Text to enter
	'Output Parameters   	: NA
	'Author				 	: akshatha.lokesha@oracle.com
	'Created date 		 	: 2nd May 2017
	'Modified date			:
	'################################################################*/

    public void Fn_setText(By by_element, String text) throws SecurityException, Exception {

        try {

            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            //############ Function implementation ###################

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            //Clear the text
            omd.driver.findElement(by_element).clear();
            //enter the text
            omd.driver.findElement(by_element).sendKeys(text);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            ////cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Set Text Failed");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //Fn//cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Get Text Failed");_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }
    }

    public void Fn_setText1(By by_element, String text) throws SecurityException, Exception {

        try {

            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            //############ Function implementation ###################

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            //Clear the text
            //omd.driver.findElement(by_element).clear();
            //enter the text
            if (!reUsables.CommonScripts.on().stringIsNullOrEmpty(text)) {
                omd.driver.findElement(by_element).sendKeys(text);
                //Logger to log function name and status
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
                omd.test.log(LogStatus.PASS, new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            }
        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            ////cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Set Text Failed");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }
    }


	/*'###############################################################
	'Function Name        : Fn_getText
	'Function Description : To get the inner text of an element
	'Input Parameters     : By of the element
	'Output Parameters    :	text
	'Author				  : akshatha.lokesha@oracle.com
	'Created date 		 	: 2nd May 2017
	'Modified date			:
	'################################################################*/

    public String Fn_getText(By by_element) throws SecurityException, Exception {

        //Function variables
        String txt = null;

        try {

            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            System.out.println("Cam here");

            txt = omd.driver.findElement(by_element).getText();
            System.out.println("txt is->" + txt);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            ////cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Get Text Failed");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));

        }

        return txt;

    }

    public String Fn_getText_DropDown(By by_element) throws SecurityException, Exception {

        //Function variables
        String txt = null;

        try {

            //////////////////////////////////////////////////////////
            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            //############ Function implementation ###################

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));


            //txt= omd.driver.findElement(by_element).getText();

            Select gl = new Select(omd.driver.findElement(by_element));

            txt = gl.getFirstSelectedOption().getText();

            System.out.println("txt is->" + txt);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            //cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Get Text Drop DownFailed");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail();
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }

        return txt;

    }
	/*'###############################################################
	'Function Name        : Fn_getAttribute
	'Function Description : To get the attribute of an element
	'Input Parameters     : By of the element , name of the attribute
	'Output Parameters    :	Attribute value
	'Author				  : akshatha.lokesha@oracle.com
	'Created date 		  : 2nd May 2017
	'Modified date		  :
	'################################################################*/

    public String Fn_getAttribute(By by_element, String attribute) throws SecurityException, Exception {

        //Function variables
        String txt = null;

        try {

            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            //############ Function implementation ###################

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));


            txt = omd.driver.findElement(by_element).getAttribute(attribute);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "for attribute->" + attribute + "->pass");
            System.out.println("txt is->" + txt);

        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            //cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");
            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }

        return txt;

    }

    public String Fn_ElementDoubleClick(By by_element) throws SecurityException, Exception {

        //Function variables
        String txt = null;

        try {

            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            //############ Function implementation ###################

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            Actions action = new Actions(omd.driver);
            action.moveToElement(omd.driver.findElement(by_element)).doubleClick().build().perform();

            Thread.sleep(90);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");
            //cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail();
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }

        return txt;

    }

	/*'###############################################################
	'Function Name        : Fn_select_dropdown
	'Function Description : To select a value from drop down
	'Input Parameters     : By of the element(Select), Text to select from drop down
	'Output Parameters    :	NA
	'Author				  : akshatha.lokesha@oracle.com
	'Created date 		 	: 2nd May 2017
	'Modified date			:
	'################################################################*/

    public void Fn_select_dropdown(By by_element, String text) throws SecurityException, Exception {

        try {

            WebDriverWait wait = new WebDriverWait(omd.driver, 15);

            //############	End of Common lines for each function	##########

            //############ Function implementation ###################


            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            Select select = new Select(omd.driver.findElement(by_element));
            select.selectByVisibleText(text);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            //cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Select DropDown Failed");

            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");
            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            //Assert.fail();
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }


    }

    public void Fn_select_dropdownValue(By by_element, String value) throws SecurityException, Exception {

        try {

            WebDriverWait wait = new WebDriverWait(omd.driver, 15);

            //############	End of Common lines for each function	##########

            //############ Function implementation ###################


            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            Select select = new Select(omd.driver.findElement(by_element));
            select.selectByValue(value);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            //cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Select DropDown Failed");

            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");
            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            //Assert.fail();
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }


    }

    public void Fn_select_dropdown(By by_element, int value) throws SecurityException, Exception {

        try {

            WebDriverWait wait = new WebDriverWait(omd.driver, 15);

            //############	End of Common lines for each function	##########

            //############ Function implementation ###################


            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            Select select = new Select(omd.driver.findElement(by_element));
            select.selectByIndex(value);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");

            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");
            e.printStackTrace();
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }
    }

    /***###############################################################
     'Function Name        : Fn_goToWindow
     'Function Description : To switch to window
     'Input Parameters     : window instance number
     'Output Parameters    :	NA
     'Author				  : divya.srinivasan@oracle.com
     '################################################################
     * @throws Exception
     * @throws SecurityException ***/

    public void Fn_goToWindow(int window_instance) throws SecurityException, Exception {

        try {

            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            /**############ Function implementation ###################**/

            Thread.sleep(3000);
            String[] windows = new String[5];

            //to get the current window instances
            Set<String> all_windows = omd.driver.getWindowHandles();

            int no_of_windws = all_windows.size();

            int i = 0;

            System.out.println("No Of Windows:" + no_of_windws);

            //If window instance to switch is valid
            if (window_instance <= no_of_windws) {

                Iterator<String> it = all_windows.iterator();

                //Iterate to point to the required window instance
                while (it.hasNext() && i < no_of_windws) {

                    windows[i] = it.next();
                    i++;
                }

                System.out.println("i=>" + i);

                //Switch the driver to required instance
                omd.driver.switchTo().window(windows[window_instance - 1]);

                //Logger to log function name and status
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + window_instance + "->pass");
                omd.test.log(LogStatus.PASS, new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + window_instance + "->pass");

            } else {

                //Logger to log function name and status
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->fail. Window instance " + window_instance + " is not present");
                //cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Go To Window Failed");

                omd.test.log(LogStatus.FAIL, new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + window_instance + "->fail");

                Assert.fail();
            }

        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");

            //cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "Go To Window Failed");

            e.printStackTrace();

            Assert.fail("Go to Window Function Failed");
        }
    }


    /***###############################################################
     'Function Name        : Fn_goToFrame
     'Function Description : To switch to different frame
     'Input Parameters     :
     frame_id
     'Output Parameters    :	NA
     'Author				  : akshatha.lokesha@oracle.com
     '################################################################***/

    public void Fn_goToFrame(By by_element_iframe) {

        try {
            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by_element_iframe));

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + by_element_iframe + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + by_element_iframe + "->pass");


        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "-> failed");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            //Assert.fail();
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Excepion ->Frame Not found");
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }
    }

	/*'###############################################################
	'Function Name        : Fn_go_OutOfFrame_allFrames
	'Function Description : To come out of all the frames
	'Input Parameters     : NA
	'Output Parameters    :	NA
	'Author				  : akshatha.lokesha@oracle.com
	'Created date 		  : 2nd May 2017
	'Modified date		  :
	'################################################################*/

    public void Fn_go_OutOfFrame_allFrames() {

        try {

            omd.driver.switchTo().defaultContent();
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->pass");


        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "-> failed");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            //Assert.fail();
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Excepion ->could not switch out of Frame");
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }
    }


    public void Fn_go_ParentFrame() {

        try {


            omd.driver.switchTo().parentFrame();

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->pass");

            JavascriptExecutor jsExecutor = (JavascriptExecutor) omd.driver;
            String currentFrame = (String) jsExecutor.executeScript("return self.name");
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "IN TH FRAME -" + currentFrame);
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "IN TH FRAME -" + currentFrame);

        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "-> failed");
            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Excepion ->could not switch out of Frame");
        }
    }

    /**
     * '##############################################################################
     * 'Function Name        : FnSelectApplication
     * 'Function Description : Selecting the application
     * 'Input Parameters     : String
     * '                     :
     * 'Output Parameters    :
     * 'Author               : sakshi.agrawal@oracle.com
     * '################################################################################
     *
     * @throws Exception
     */

    public CommonScripts FnSelectApplication(String appName) {
        try {
            APP_LOGS.info(appName);
            String appLogo = "//span[@title='Oracle Logo']";
            String appHeader = "//span[contains(text(),'Financial Services Analytical Applications')]";
            String menuItemLabel = "//span[contains(text(),'applications')]";
            String name = "//div[contains(text(),'" + appName + "')]";
            String appSubLabel = "//div[contains(text(),'Application for Insurance Liability Calculations')]";
            if (omd.driver.findElement(By.xpath(name)).getText().equalsIgnoreCase(appName)) {
                if (omd.driver.findElement(By.xpath(appSubLabel)).isDisplayed()) {
                    APP_LOGS.info("********Welcome to IFRS17 HomePage**********");
                }
            }
            if (omd.driver.findElement(By.xpath(appLogo)).isDisplayed()) {
                APP_LOGS.info("*******Oracle Logo displayed on the Applications HomePage");
                if (omd.driver.findElement(By.xpath(appHeader)).isDisplayed()) {
                    APP_LOGS.info("*******Application Header " + appHeader + " displayed***********");
                }
            }
            if (omd.driver.findElement(By.xpath(menuItemLabel)).isDisplayed()) {
                APP_LOGS.info("********Menu Item Label " + menuItemLabel + " displayed***********");
            }
            omd.driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            Thread.sleep(1000);
            Fn_click(By.xpath(name));
            Thread.sleep(2000);
            APP_LOGS.info("Application "+appName+" has been clicked");
        } catch (Exception e) {
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "-> failed");
            e.printStackTrace();
            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Excepion ->could not switch out of Frame");
        }
        return obj;
    }

    public Boolean Fn_Exists(String elename, By by_element) {

        //Function variables
        Boolean exists = false;

        try {
            System.out.println("Element : " + elename + " and By element : " + by_element);
            boolean isDisplayed = omd.driver.findElement(by_element).isDisplayed();

            System.out.println("Element isDisplayed:" + isDisplayed);
            if (!(isDisplayed)) {
                omd.test.log(LogStatus.WARNING, elename + " not present in the UI");

            } else {

                exists = true;
                omd.test.log(LogStatus.PASS, elename + " present in the UI as expected");
            }


            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");


        } catch (Exception e) {

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");

            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));

        }

        return exists;

    }


    public static void createFolder(String folderPath, String filefolder) {
        File DestFile = new File(folderPath + "//" + filefolder);
        if (!DestFile.exists()) {
            DestFile.mkdir();
        }
    }




	/*'###############################################################
	'Function Name        	: Fn_CalSelect
	'Function Description 	: To select day in calender - frame
	'Input Parameters    	: By of the element
	'Output Parameters    	: true/false
	'################################################################*/

    public void Fn_CalSelect(String valDate, By calbutton) throws SecurityException, Exception {
        //d3.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        //WebDriverWait wait=new WebDriverWait(d3, 30);
        try {
            if (!valDate.isEmpty()) {
                //String currWin = d3.getWindowHandle();
                Fn_click(calbutton);
                //calbutton.click();
                //wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("calWin"));
                Fn_goToFrame(By.id("calWin"));
                String[] Dateparts = valDate.split("-");
                String valYear = Dateparts[2];
                APP_LOGS.info("Date : " + valDate);
                for (String datePart : Dateparts) {
                    APP_LOGS.info(datePart);
                }
				/*WebElement Year =d3.findElement(By.id("yearList"));
					CommSelect(Year, valYear, "text");*/
                Fn_select_dropdown(By.id("yearList"), valYear);
                int month = Integer.parseInt(Dateparts[1]);
                //month = month -1;
                //String valMonth =  String.valueOf(month);
                String valMonth = Month.of(month).name();
                APP_LOGS.info("Month value before: " + valMonth);
                String lowerCase = valMonth.toLowerCase();
                String valMonth1 = lowerCase.substring(0, 1).toUpperCase() + lowerCase.substring(1);
                APP_LOGS.info("Month final value: " + valMonth1);
				/*WebElement Month =d3.findElement(By.id("monthList"));
					CommSelect(Month, valMonth, "value");*/
                Fn_select_dropdown(By.id("monthList"), valMonth1);
                String valDay = Dateparts[0];
				/*WebElement selday = d3.findElement(By.linkText(valDay));
					selday.click();
					d3.switchTo().parentFrame();*/
                Fn_click(By.linkText(valDay));
                Fn_go_ParentFrame();
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->Date Selected->pass");
                omd.test.log(LogStatus.PASS, new Object() {
                }.getClass().getEnclosingMethod().getName() + "->Date Selected->pass");
            }
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + calbutton + "->exception");

            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
    }

    /*'###############################################################
	'Function Name        	: Fn_CalSelect1
	'Function Description 	: To select day in calender - popup window
	'Input Parameters    	: By of the element
	'Output Parameters    	: true/false
	'################################################################*/
    public void Fn_CalSelect1(String valDate, By calbutton) throws SecurityException, Exception {
        //WebDriverWait wait=new WebDriverWait(d3, 30);
        try {
            if (!valDate.isEmpty()) {
                String currWin = omd.driver.getWindowHandle();
                //explicitWaitClick(calbutton, d3);
                //calbutton.click();
                Fn_click(calbutton);
                Thread.sleep(2000);
                for (String winHandle : omd.driver.getWindowHandles()) {
                    omd.driver.switchTo().window(winHandle);
                }
                Thread.sleep(1000);
                String[] Dateparts = valDate.split("-");
                String valYear = Dateparts[2];
                //Thread.sleep(2100);
				/*WebElement Year =d3.findElement(By.id("yearList"));
					CommSelect(Year, valYear, "text");*/
                Fn_select_dropdown(By.id("yearList"), valYear);

                int month = Integer.parseInt(Dateparts[1]);
                month = month - 1;
                String valMonth = String.valueOf(month);
				/*WebElement Month =d3.findElement(By.id("monthList"));
					CommSelect(Month, valMonth, "value");*/
                Fn_select_dropdown(By.id("monthList"), valMonth);

                String valDay = Dateparts[0];
				/*WebElement selday = wait.until(ExpectedConditions.elementToBeClickable(d3.findElement(By.linkText(valDay))));
					selday.click();*/
                Fn_click(By.linkText(valDay));
                Thread.sleep(500);
                omd.driver.switchTo().window(currWin);
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->Date Selected->pass");
                omd.test.log(LogStatus.PASS, new Object() {
                }.getClass().getEnclosingMethod().getName() + "->Date Selected->pass");
            }
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + calbutton + "->exception");

            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
    }

    /*'###############################################################
	'Function Name        	: Fn_CalSelect1
	'Function Description 	: To select day in calender - popup window
	'Input Parameters    	: By of the element
	'Output Parameters    	: true/false
	'################################################################*/
    public void Fn_CalSelect2(String valDate, By calbutton) throws SecurityException, Exception {
        //WebDriverWait wait=new WebDriverWait(d3, 30);
        try {
            if (!valDate.isEmpty()) {
                String currWin = omd.driver.getWindowHandle();
                //explicitWaitClick(calbutton, d3);
                //calbutton.click();
                Fn_click(calbutton);
                Thread.sleep(2000);
                for (String winHandle : omd.driver.getWindowHandles()) {
                    omd.driver.switchTo().window(winHandle);
                }
                Thread.sleep(1000);
                String[] Dateparts = valDate.split("-");
                String valYear = Dateparts[2];
                APP_LOGS.info("Date : " + valDate);
                Fn_select_dropdown(By.id("yearList"), valYear);

                int month = Integer.parseInt(Dateparts[1]);
                month = month - 1;
                String valMonth = String.valueOf(month);
				/*WebElement Month =d3.findElement(By.id("monthList"));
					CommSelect(Month, valMonth, "value");*/
                Fn_select_dropdownValue(By.id("monthList"), valMonth);

                String valDay = Dateparts[0];
				/*WebElement selday = wait.until(ExpectedConditions.elementToBeClickable(d3.findElement(By.linkText(valDay))));
					selday.click();*/
                if(valDay.charAt(0)=='0'){
                    valDay=valDay.substring(1);
                }
                Fn_click(By.linkText(valDay));
                Thread.sleep(500);
                omd.driver.switchTo().window(currWin);
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->Date Selected->pass");
                omd.test.log(LogStatus.PASS, new Object() {
                }.getClass().getEnclosingMethod().getName() + "->Date Selected->pass");
            }
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + calbutton + "->exception");

            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
    }

    /*'###############################################################
	'Function Name        	: Fn_TakeSnapShot
	'Function Description 	: To take screen shot of the current window
	'Input Parameters    	: By of the element
	'Output Parameters    	: true/false
	'################################################################*/


    /*'###############################################################
	'Function Name        	: tableRowCount
	'Function Description 	: To get the number of rows in the table
	'Input Parameters    	: By of the element
	'Output Parameters    	: true/false
	'################################################################*/
    public int tableRowCount(String DBName, String DBUser, String DBPass, String Query) {
        int rowCnt = 0;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //String DBConnName = "jdbc:oracle:thin:@"+DBName;
            Connection con = DriverManager.getConnection(DBName, DBUser, DBPass);
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "Connected to database");
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rc = stmt.executeQuery(Query);
            rc.last();
            rowCnt = rc.getRow();
            rc.beforeFirst();
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Query \n" + Query);
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->RowCount " + rowCnt);
            //omd.test.log(LogStatus.PASS, Method.class.getEnclosingMethod().getName()+"->RowCount "+rowCnt);
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");
            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
        return rowCnt;
    }


    /*'###############################################################
	'Function Name        	: reformatDate
	'Function Description 	: reformate date to yyyymmdd
	'Input Parameters    	: By of the element
	'Output Parameters    	: number of rows deleted
	'################################################################*/
    public String reformatDate(String date) {
        String formatedDt = null;
        try {
            String[] spltDt = date.split("-");
            spltDt[0] = "0" + spltDt[0];
            spltDt[1] = "0" + spltDt[1];
            formatedDt = spltDt[0] + "-" + spltDt[1] + "-" + spltDt[2];
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Reformated Date" + formatedDt);
            omd.test.log(LogStatus.PASS, Method.class.getEnclosingMethod().getName() + "->Reformated Date" + formatedDt);
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");
            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
        return formatedDt;
    }



    public int totalRowsCount(String DBName, String DBUser, String DBPass, String Query) {
        int rowCount = 0;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //String DBConnName = "jdbc:oracle:thin:@"+DBName;
            Connection con = DriverManager.getConnection(DBName, DBUser, DBPass);
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "Connected to database");
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rc = stmt.executeQuery(Query);
            rc.next();
            rowCount = rc.getInt(1);
            APP_LOGS.info("Total rows: " + rowCount);
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Query \n" + Query);
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->RowCount " + rowCount);
            //omd.test.log(LogStatus.PASS, Method.class.getEnclosingMethod().getName()+"->RowCount "+rowCnt);
        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->exception");
            omd.test.log(LogStatus.FAIL, Method.class.getEnclosingMethod().getName() + "Exception Occured-> Fail");
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
        return rowCount;
    }


	/*'###############################################################
	'Function Name        : FnSummaryRuleCheck
	'Function Description : To verify if rule name exists in summary page
	'Input Parameters     : NameXpath -> Textfield to enter Name,
	                                        Name      -> Name of rule to be searched
	'Output Parameters    :
	'################################################################*/

    public String FnSummaryRuleCheck(String valuetocmpre) throws Exception {
        APP_LOGS.info("----> FnSummaryRuleCheck Started ");
        String temp = "FALSE";
        String tbl_cur_val = "";
        // int j =2;// column to fetch value to compare


        // FnResetSummary();
        // FnSortSummary();
        // Get table values
        WebElement BooksTable = omd.driver.findElement(By.id("searchResultsTable"));


        // if(web.exists(var.SummaryTableXpath)){
        APP_LOGS.info("input valuetocmpre is " + valuetocmpre);
        int rowCount = omd.driver.findElements(By.xpath("//table[@id='searchResultsTable']/tbody/tr")).size();
        APP_LOGS.info("no of rows " + rowCount);

        // Search for value in table
        if (rowCount == 0) {
            APP_LOGS.info("No Results in Table:0 i.e Table is empty-No value to check");
            temp = "FALSE";
        } else {
            for (int i = 1; i <= rowCount; i++) {
                for (int j = 2; j <= 3; j++) {

                    if (rowCount == 1)
                        tbl_cur_val = omd.driver.findElement(By.xpath("//table[@id='searchResultsTable']/tbody/tr/td[" + j + "]")).getText().trim();
                    else
                        tbl_cur_val = omd.driver.findElement(By.xpath("//table[@id='searchResultsTable']/tbody/tr[" + i + "]/td[" + j + "]")).getText().trim();

                    APP_LOGS.info(" ---> Value in row " + i + "& " + j + " col is :" + tbl_cur_val + "");
                    if (tbl_cur_val.toLowerCase().equalsIgnoreCase(valuetocmpre)) {
                        temp = "TRUE";
                        APP_LOGS.info("Exact match found:" + temp);
                        break;
                    } else {
                        temp = "FALSE";
                        APP_LOGS.info("Exact match NOT found:" + temp);
                    }
                }
                if (temp.equalsIgnoreCase("TRUE"))
                    break;
            }

        }

        return temp;

    }

    /*
     *
     * FnName:FnSelectDateFromCalendar
     * Input:day,month and year
     * Created By:Chippy.Jacob@oracle.com
     *
     */

    public void FnSelectDateFromCalendar(String day, String month, String year) {
        String currWin = MasterDriver.driver.getWindowHandle();
        try {
            Thread.sleep(2000);
            for (String winHandle : MasterDriver.driver.getWindowHandles()) {
                MasterDriver.driver.switchTo().window(winHandle);
            }
            System.out.println("switched to date window");
            String mn = null;

            //System.out.println("Default date in tetx box "+asDate);
            switch (month) {
                case "01":
                    mn = "January";
                    break;
                case "02":
                    mn = "February";
                    break;
                case "03":
                    mn = "March";
                    break;
                case "04":
                    mn = "April";
                    break;
                case "05":
                    mn = "May";
                    break;
                case "06":
                    mn = "June";
                    break;
                case "07":
                    mn = "July";
                    break;
                case "08":
                    mn = "August";
                    break;
                case "09":
                    mn = "September";
                    break;
                case "10":
                    mn = "October";
                    break;
                case "11":
                    mn = "November";
                    break;
                case "12":
                    mn = "December";
                    break;
            }
            Thread.sleep(3000);
            //FnSelectDropdown(IFRS17_Common_Objlib.TS_DateMonth_select(), mn);
            Thread.sleep(3000);
            //FnSelectDropdown(IFRS17_Common_Objlib.TS_DateYear_select(), year);

            MasterDriver.driver.findElement(By.xpath("//a[@href='Javascript:window.opener.returnDate3(" + day + ")']")).click();
            System.out.println("day is selected");
            MasterDriver.driver.switchTo().window(currWin);
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Error in selection of day");
            e.printStackTrace();
        }
    }


    public String Fn_getCellValue(HSSFSheet sh, int row_num, String col_name) throws SecurityException, Exception {

        //FUnction variables
        String cell_value = null;

        try {

            HSSFRow header_row = sh.getRow(0);
            HSSFRow current_row = sh.getRow(row_num);
            HSSFCell header_cell, result_cell;

            Short first_col = header_row.getFirstCellNum();
            Short last_col = header_row.getLastCellNum();

            DataFormatter data_formatter = new DataFormatter();

            Map<String, Integer> map = new HashMap<String, Integer>();

            for (Short col = first_col; col < last_col; col++) {

                header_cell = header_row.getCell(col);

                map.put(header_cell.getStringCellValue(), header_cell.getColumnIndex());
            }


            //Get the required cell of a row number and column name
            result_cell = current_row.getCell(map.get(col_name));

            //Get the String value of Cell irrespective of Cell Type i.e, NUMERIC, DATE..
            cell_value = data_formatter.formatCellValue(result_cell);


        } catch (Exception e) {

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail();

        }

        //Function return
        return cell_value;
    }

	/*'##############################################################################
    ' 				         Function Name: FnSearchInSummary
    '			      Function Description: To search summary records
    '        		      Input Parameters: SUMMARY_NAME_XPATH,PROCESS_NAME,SUMMARY_FOLDER_XPATH,SUMMARY_FOLDER_NAME,SUMMARY_SEARCH_BUTTON_XPATH
    '				     Output Parameters: Returns the Result Status (PASS/FAIL)
    '                    Object Repository: NA
    '                     Input Sheet name: NA
    '                         Date Created: 12-DEC-2014
    '                           Written by: abhay.dubey@oracle.com
    '  					     Date Modified: 12-DEC-2014
    '                          Modified by: abhay.dubey@oracle.com
    '################################################################################*/

    /**
     *
     */

    public String FnSearchInSummaryProcess(By SUMMARY_NAME_XPATH
            , String PROCESS_NAME
            , By SUMMARY_FOLDER_XPATH
            , String SUMMARY_FOLDER_NAME, By SUMMARY_CODE_XPATH, String SUMMARY_CODE
            , By SUMMARY_SEARCH_BUTTON_XPATH

    ) throws Exception {
        Object fnStatus = "";
        boolean isRuleContains = false;
        System.out.println("Search the item in Search Section of processSummary Screen");
        {

            if (!PROCESS_NAME.isEmpty()) {
                omd.driver.findElement(SUMMARY_NAME_XPATH).clear();
                omd.driver.findElement(SUMMARY_NAME_XPATH).sendKeys("");
                FnSetText(SUMMARY_NAME_XPATH, PROCESS_NAME);
            }

            if (!SUMMARY_FOLDER_NAME.isEmpty())
                FnSelectDropdown(SUMMARY_FOLDER_XPATH, SUMMARY_FOLDER_NAME);

            FnElementClick(SUMMARY_SEARCH_BUTTON_XPATH);
            Thread.sleep(500);
            //isRuleContains=true; //Remove

        }
        //To verify if results are displayed
        fnScrollDiv(By.id("searchResultsGrid"));
        WebElement BooksTable = omd.driver.findElement(By.xpath("//div[@id='searchResultsGrid']//table[@id='searchResultsTable']"));
        int rowCount = omd.driver.findElements(By.xpath("//div[@id='searchResultsGrid']//table[@id='searchResultsTable']/tbody/tr")).size();

        System.out.println("rowcount:::" + rowCount);
        if (rowCount <= 0) {
            isRuleContains = false;
            System.out.println("rowcount:::1234");
            APP_LOGS.info("The process does not exist");

        } else {
            APP_LOGS.info("The newly added process exist");
            System.out.println("rowcount is " + rowCount);
            isRuleContains = FnSummaryProcessRuleContains(PROCESS_NAME);
        }

        if (isRuleContains) {
            System.out.println("Search results contains search value -");
            fnStatus = "PASS";
        } else {
            fnStatus = "FAIL";
            System.out.println("FAIL rowcount:::12345");
            APP_LOGS.error("Search results doesnot contains search value -" + PROCESS_NAME);
        }
        System.out.println("Function FnSearchInSummary " + fnStatus);
        return (fnStatus.toString());
    }


	/*'###############################################################
    'Function Name        : FnSummaryProcessRuleContains
    'Function Description : To verify if rule name exists in summary page for process
    'Input Parameters     : NameXpath -> Textfield to enter Name,
                                            Name      -> Name of rule to be searched
    'Output Parameters    :
    Note:Similar to Fnsummary Rule,but xpath was changed in Static deterministic page.Hence modified
    '################################################################*/

    public boolean FnSummaryProcessRuleContains(String valuetocmpre) throws Exception {

        boolean temp = false;
        int rowCount;
        String tbl_cur_val;
        Thread.sleep(4000);

        APP_LOGS.info("<--FnSummaryRuleContains::Verify if Table has Rule after search-->");
        // Get table values
        fnScrollDiv(By.id("searchResultsGrid"));
        WebElement BooksTable = omd.driver.findElement(By.xpath("//div[@id='searchResultsGrid']//table[@id='searchResultsTable']"));
        rowCount = omd.driver.findElements(By.xpath("//div[@id='searchResultsGrid']//table[@id='searchResultsTable']/tbody/tr")).size();
        valuetocmpre = valuetocmpre.trim();


        valuetocmpre = (valuetocmpre.toLowerCase()).trim();
        APP_LOGS.info("Input valuetocompare is:" + valuetocmpre);

        APP_LOGS.info("No of rows in Table:" + rowCount);

        // Search for value in table
        if (rowCount == 0) {
            APP_LOGS.info("No Results in Table:0");
            temp = false;
        } else {
            for (int i = 1; i <= rowCount; i++) {
                for (int j = 1; j <= 2; j++) {

                    if (rowCount == 1)
                        tbl_cur_val = omd.driver.findElement(By.xpath("//div[@id='searchResultsGrid']//table[@id='searchResultsTable']/tbody/tr/td[" + j + "]")).getText().trim();
                    else
                        tbl_cur_val = omd.driver.findElement(By.xpath("//div[@id='searchResultsGrid']//table[@id='searchResultsTable']/tbody/tr[" + i + "]/td[" + j + "]")).getText().trim();

                    APP_LOGS.info("CHECKBOX_ROWNBR -> Value in row-" + i + " & col-" + j + " is:" + tbl_cur_val);


                    APP_LOGS.info(" ---> Value to compare is :" + tbl_cur_val + ":");
                    if (!tbl_cur_val.isEmpty()) {
                        tbl_cur_val = (tbl_cur_val.toLowerCase()).trim();
                        if (!(tbl_cur_val.contains(valuetocmpre))) {
                            temp = false;
                            APP_LOGS.info(tbl_cur_val + "-> DOESNOT contain " + valuetocmpre);


                        } else {
                            temp = true;
                            APP_LOGS.info(tbl_cur_val + "-> Does contain " + valuetocmpre);
                            //break;
                        }
                    }
                }
            }
        }

		/*  if(var.ModuleSet.equalsIgnoreCase(var.ConsTWO))
              {
                    if (rowCount == 1) {
                               temp = false;
                               APP_LOGS.info("Table is empty-No value to compare");
                        }

              }
		 *//*else if(var.ModuleSet.equalsIgnoreCase(var.ConsTHREE)){
                    FnPm803Condition();
              }*/
        //APP_LOGS.info(" ---> End of FnSummaryRuleContains retun temp is: "+temp);
        return temp;
    }



    public String getCurrentWindow() {
        return omd.driver.getTitle();
    }

    public boolean isElementExist(By element) {
        boolean check = false;
        if (omd.driver.findElements(element).size() > 0) {
            WebElement wElement = MasterDriver.driver.findElement(element);
            if (wElement.isDisplayed() && wElement.isEnabled()) {
                check = true;
            }
        }
        return check;
    }

    public static boolean isElementClickable(By by) {
        try {
            WebDriverWait wait = new WebDriverWait(MasterDriver.driver, 5);
            wait.until(ExpectedConditions.elementToBeClickable(by));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*public void setFormula(String text, int boxValue) throws Exception {
        try {
            if (!(text.equalsIgnoreCase("NA") || text.isEmpty())) {
                Fn_click(IFRS17_CalcPreference_Objlib.VFAinputVariableBtn(boxValue));
                Thread.sleep(1500);
                Fn_setText1(IFRS17_CalcPreference_Objlib.expressionTextbox(), text);
                Fn_click(IFRS17_CalcPreference_Objlib.expressionBoxOkBtn());
                Thread.sleep(1500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            APP_LOGS.info("Failed in contractualServiceOutputParams bcz-->" + e.getMessage());
        }
    }*/

    public void Fn_waitForElementDisplayed(By by_element) throws SecurityException, Exception {

        try {

            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 20);

            //############ Function implementation ###################

            //Explicit wait until the element is located
            wait.until(ExpectedConditions.presenceOfElementLocated(by_element));

            //Logger to log function name and status
            APP_LOGS.info("Element has been Located");

        } catch (Exception e) {

            //############ Common lines for each function-> Catch to report the exception with function name	##########
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
            //cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            //Fn_TestCaseStatusReport(result_file_path, map_rep_details,"FAIL", new Object(){}.getClass().getEnclosingMethod().getName() + "->" +by_element + " failed");
            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));
            //############ End of Common lines for each function-> Catch to report the exception with function name	##########
        }

    }


    public List select(Connection con, String Query) throws Exception {

        List<HashMap<String, String>> dbValues = new ArrayList<>();
        try {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "Connected to database");
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->Query \n" + Query);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(Query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount = 0;
            while (rs.next()) {
                HashMap row1 = new HashMap(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    String nullVal = rs.getString(i);
                    if (nullVal == null) {
                        nullVal = "null";
                        row1.put(rsmd.getColumnName(i), nullVal);
                    } else {
                        row1.put(rsmd.getColumnName(i), rs.getString(i));
                    }
                    rowCount = rowCount + 1;
                }//end of while
                dbValues.add(row1);
            }//while
        }//try
        catch (Exception e) {
            e.printStackTrace();

        }
        return dbValues;

    }

    public int searchValuesinTable(By tablePath, String searchValue) {
        int rows = 1;
        WebElement baseTable = MasterDriver.driver.findElement(tablePath);
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        try {
            for (int row = 1; row < tableRows.size(); row++) {
                List<WebElement> tableColumns = tableRows.get(row).findElements(By.tagName("td"));
                for (int column = 1; column < tableColumns.size(); column++) {
                    if (tableColumns.get(column).getText().replaceAll("\\s+", "").equals(searchValue)) {
                        System.out.println("in the Row :" + row + " " + tableColumns.get(column).getText());
                        rows = row;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public void waitForElement(By element) {
        WebDriverWait wait = new WebDriverWait(omd.driver, 30);
        wait.until(ExpectedConditions.presenceOfElementLocated(element));
    }

	  /*'###############################################################
    'Function Name        : createLogFile
    'Function Description : create error logs
    'Input Parameters     :String DBName,String DBUser,String DBPass,String Query,String Parameter,String locationPath
    'Output Parameters    :

    '################################################################*/

    public void createLogFile(String Data, String Path) {
        try {


            BufferedWriter bw = null;
            FileWriter fw = null;


            File file = new File(Path);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            bw.write(Data);
            bw.close();

            System.out.println("Error log updated");


        } catch (IOException e) {
            e.printStackTrace();

        }


    }


    //Converting DB results to CSV file

    /*'###############################################################
    'Function Name        : convertDBtoCSV
    'Function Description : Convert the DB to CSV
    'Input Parameters     :String DBName,String DBUser,String DBPass,String Query,String Parameter,String locationPath
    'Output Parameters    :

    '################################################################*/


    /*'###############################################################
    'Function Name        : FnCSVFileExist
    'Function Description : Check whether file exists in folder
    'Input Parameters     :String objectDefID,String DBName,String DBUser,String DBPass,String Query
    'Output Parameters    :
    @Chippy.Jacob@oracle.com
    '################################################################*/
    public boolean FnCSVFileExist(String Filename) throws IOException {
        boolean val = false;
        File f = new File(Filename);
        byte[] buffer = new byte[1024];
        //CsvReader csv = new CsvReader(Filename);
        if (f.exists() && !f.isDirectory()) {
            val = true;
            System.out.println("The file exists");
            f.delete();
            System.out.println("File deleted successfully");
        } else {
            val = false;
            System.out.println("The file does not exists" + Filename);
        }
        return val;
    }


    public void clickThroughJavaExecutor(By by) {

        try {

            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 50);

            //Explicit wait until the element is located
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(by));
            // wait the element  to become stale
            wait.until(ExpectedConditions.elementToBeClickable(by));
            // click on "Add Item" once the page is reloaded
            APP_LOGS.info("Element to click : " + by);
//            WebElement element = MasterDriver.driver.findElement(by);
            JavascriptExecutor jse = (JavascriptExecutor) omd.driver;
            jse.executeScript("arguments[0].click()", el);

            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by + "->pass");

        } catch (Exception e) {if (e.getMessage().contains("stale element reference:")) {
            APP_LOGS.info("Inside Stale Element Exception");
            boolean result = false;
            int attempts = 0;
            while (attempts < 2) {
                try {
                    Thread.sleep(400);
                    MasterDriver.driver.findElement(by).click();
                    result = true;
                    break;
                } catch (Exception ex) {
                    APP_LOGS.info("From Stal Exception Hansle : " + ex.getMessage());
                }
                attempts++;
            }

        } else if (e.getMessage().contains("element click intercepted:")) {
            APP_LOGS.info(handleInterceptElementException(by));
        } else {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by + "->exception");
            ////cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
            omd.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by + "->Object Not found");

            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));
        }


        }


    }



    public void moveElementAction(By by_element) {
        try {
            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 30);
            Actions action = new Actions(MasterDriver.driver);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            APP_LOGS.info("Element Present");
            Thread.sleep(1000);
            action.moveToElement(element).click(element).perform();

            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } catch (Exception e) {
            String eTitle = e.getClass().getName();
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");

            ////cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

            omd.test.log(LogStatus.ERROR, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "-> " + eTitle);

            APP_LOGS.info("Exception Title : " + eTitle);
            e.printStackTrace();

            //Assert.fail() to report in TestNG report
            Assert.fail(Throwables.getStackTraceAsString(e));

        }
    }

    public static void waitForelementToLoad(By by) {
        WebDriverWait wait = new WebDriverWait(MasterDriver.driver, 30);
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.presenceOfElementLocated(by));

    }


    public HashMap<Integer, String> searchTable(By tableElem, String search) throws Exception {
        WebElement baseTable = MasterDriver.driver.findElement(tableElem);
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        HashMap<Integer, String> table = new HashMap<Integer, String>();
        APP_LOGS.info(tableRows.size());
        if (tableRows.size() > 0) {
            for (int i = 0; i < tableRows.size(); i++) {
                List<WebElement> columns = tableRows.get(i).findElements(By.xpath(".//td"));
                for (WebElement column : columns) {
                    if (column.getText().replaceAll("\\s", "").equals(search)) {
                        table.put(i + 1, tableRows.get(i).getText());
                        break;
                    }
                }
                if (table.size() > 0) {
                    break;
                }
            }
        }
        Thread.sleep(500);
        return table;
    }



    public int getLatsRow(String filePath) throws Exception {
        File file = new File(filePath);
        int n_lines = 10;
        int counter = 0;
        if (file.exists()) {
            ReversedLinesFileReader object = new ReversedLinesFileReader(file);
            while (object.readLine() != null) {
                counter++;
            }
        }
        return counter;
    }


    public void getMouseOverValue(By by) {
        try {
            waitForElement(by);
            WebElement element = MasterDriver.driver.findElement(by);
            Actions toolAct = new Actions(MasterDriver.driver);
            toolAct.moveToElement(element).build().perform();
            System.out.println("Came here");
            WebElement toolTipElement = MasterDriver.driver.findElement(By.cssSelector(".ui-tooltip"));
            String toolTipText = toolTipElement.getText();
            System.out.println(toolTipText);
        } catch (Exception e) {
            APP_LOGS.info(e.getMessage());
        }
    }

    public void enterTextDropDown(String[] strings, By by, By byTextField) {
        try {
            APP_LOGS.info(reUsables.CommonScripts.on().stringIsNullOrEmpty(strings[0]));
            if (reUsables.CommonScripts.on().stringIsNullOrEmpty(strings[0]) == false) {
                String btnVal = Fn_getAttribute(by, "aria-checked");
                APP_LOGS.info("BtnVal : " + btnVal);
                if(reUsables.CommonScripts.on().stringIsNullOrEmpty(btnVal)==true){
                    Fn_click(by);
                }
                else if (!btnVal.equalsIgnoreCase("true")) {
                    Fn_click(by);
                }
                for (String string : strings) {
                    APP_LOGS.info("For "+string);
                    MasterDriver.driver.findElement(byTextField).sendKeys(Keys.ARROW_DOWN);
                    String polTermvalue = "//div[contains(text(),'" + string + "')]";
                    APP_LOGS.info(polTermvalue);
                    if (isElementExist(By.xpath(polTermvalue))) {
                        Fn_click(By.xpath(polTermvalue));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void enterTextDropDownForSpan(String[] strings, By byTextField) {
        try {
            APP_LOGS.info(reUsables.CommonScripts.on().stringIsNullOrEmpty(strings[0]));
            if (reUsables.CommonScripts.on().stringIsNullOrEmpty(strings[0]) == false) {
                for (String string : strings) {
                    APP_LOGS.info("For "+string);
                    MasterDriver.driver.findElement(byTextField).sendKeys(Keys.ARROW_DOWN);
                    String polTermvalue = "//span[contains(text(),'" + string + "')]";
                    if(isElementExist(By.xpath("//span[contains(text(),'" + string + "')]"))){
                        Fn_click(By.xpath("//span[contains(text(),'" + string + "')]"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    public void clickThroughJavaExecutorByscrollView(By by) {
        try {
            waitForelementToLoad(by);
            JavascriptExecutor executor = (JavascriptExecutor) MasterDriver.driver;

            executor.executeScript("arguments[0].scrollIntoView(true);",
                    MasterDriver.driver.findElement(by));
            Fn_click(by);

        } catch (Exception e) {
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by + "->exception");
            MasterDriver.test.log(LogStatus.FAIL, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by + "->Object Not found");
            e.printStackTrace();
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
    }

    public List deepCloneListOfHashMap(List<HashMap<String, String>> source) {
        List<HashMap<String, String>> result = new ArrayList<>();
        Iterator iterator = source.iterator();
        while (iterator.hasNext()) {
            HashMap<String, String> mapp = (HashMap<String, String>) iterator.next();
            HashMap<String, String> tempMap = new HashMap<>();
            for (Map.Entry<String, String> entry : mapp.entrySet()) {
                tempMap.put(entry.getKey(), entry.getValue());
            }
            result.add(tempMap);
        }
        return result;
    }

    public HashMap<String,String> string2Map(String string){
        HashMap<String,String> map=new HashMap<>();
        String[] pairs = string.split(",");
        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            String[] keyValue = pair.split(":");
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    public void Fn_clickForNoException(By by_element) throws Exception {
        try {
            //Initiate explicit wait
            WebDriverWait wait = new WebDriverWait(omd.driver, 30);
            //Explicit wait until the element is located
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by_element));
            // wait the element  to become stale
            wait.until(ExpectedConditions.visibilityOf(element));
            wait.until(ExpectedConditions.elementToBeClickable(by_element));
            // click on "Add Item" once the page is reloaded
            APP_LOGS.info("Element to click : " + by_element);
            //omd.driver.findElement(by_element).click();
            element.click();
            //Logger to log function name and status
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");
            omd.test.log(LogStatus.PASS, new Object() {
            }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->pass");

        } catch (Exception e) {
            APP_LOGS.info(e.getMessage());
            if (e.getMessage().contains("stale element reference:")) {
                APP_LOGS.info("Inside Stale Element Exception");
                int attempts = 0;
                while (attempts < 2) {
                    try {
                        Thread.sleep(400);
                        MasterDriver.driver.findElement(by_element).click();

                        break;
                    } catch (Exception ex) {
                        APP_LOGS.info("From Stal Exception Hansle : " + ex.getMessage());
                    }
                    attempts++;
                }

            } else if (e.getMessage().contains("element click intercepted:")) {
                APP_LOGS.info(handleInterceptElementException(by_element));
            } else {
                APP_LOGS.info(new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->exception");
                ////cu.FnTestCaseStatusReport("FAIL",new Object (){}.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");
                omd.test.log(LogStatus.FAIL, new Object() {
                }.getClass().getEnclosingMethod().getName() + "->" + by_element + "->Object Not found");

                e.printStackTrace();

                //Assert.fail() to report in TestNG report

            }
        }

    }
}	
