package cucumber.stepDefinition;


import calculatorApplication.operations.CalculatorApp;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class StepsForCalculatorAPP {
    String arithOps="";

    @Given("Getting inputs from User for Calculator APP")
    public void getInputs(){
        System.out.println(SetupEnvironment.setupEnv);
        System.out.println("Get Inputs");
    }

    @When("Do {string} Arithmetic Calculation for all the given inputs")
    public void performArithmeticCalculation(String arithmeticops){
        System.out.println("Perform Arithmetic Calculation " +arithmeticops);
        arithOps=arithmeticops;
    }

    @Then("Resultant value should be displayed")
    public void printResult(){
        System.out.println("Result is "+CalculatorApp.on().doArithmeticOps(arithOps));
    }


}
