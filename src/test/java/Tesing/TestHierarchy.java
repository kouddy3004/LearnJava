package Tesing;

import org.testng.annotations.*;

public class TestHierarchy {

    @BeforeClass
    public void beforeClass() {
        System.out.println("Before Class");
    }

    @AfterClass
    public void afterClass() {
        System.out.println("After Class");
    }

    @BeforeTest
    public void beforeTest() {
        System.out.println("Before Test");
    }

    @AfterTest
    public void afterTest() {
        System.out.println("After Test");
    }
    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Before SUite");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("After Suite");
    }



    @BeforeGroups
    public void beforeGroups() {
        System.out.println("Before Groups");
    }

    @AfterGroups
    public void afterGroups() {
        System.out.println("After Groups");
    }

    @BeforeMethod
    public void beforeMethod() {
        System.out.println("Before Metod");
    }

    @AfterMethod
    public void afterMethod() {
        System.out.println("After Method");
    }

    @org.testng.annotations.Test
    public void test() {
        System.out.println("TEST");
    }
}
