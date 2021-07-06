package calculatorApplication.operations;

public class CalculatorApp {
    private static  CalculatorApp calculatorApp=new CalculatorApp();
    private CalculatorApp(){}

    public static CalculatorApp on(){
        return calculatorApp;
    }

    public int doArithmeticOps(String arithmeticOp){
        int c=0;
        switch (arithmeticOp.toLowerCase()){
            case "addition":
                c=addition();
                break;
            case "subtraction":
                c=subtraction();
                break;
            case "multiplication":
                c=multiplication();
                break;
            case "division":
                c=division();
                break;
            case "mods":
                c=mod();
                break;
        }
        return c;
    }

    private int addition(){
        int a=10;
        return a+a;
    }

    private int subtraction(){
        int a=10;
        return a-a;
    }

    private int multiplication(){
        int a=10;
        return a*a;
    }

    private int division(){
        int a=10;
        return a/a;
    }

    private int mod(){
        int a=10;
        return a%a;
    }
}
