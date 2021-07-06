package Tesing;

import java.math.BigDecimal;
import java.util.Formatter;

public class BasicJava {

    public void stringToBigNUmber(String bigString){
        BigDecimal bigDec=new BigDecimal(bigString);
        System.out.println(bigDec);
    }
    public static void main(String[] args) {
        BasicJava basicJava=new BasicJava();
        basicJava.stringToBigNUmber("162361243399462108.359");
    }
}
