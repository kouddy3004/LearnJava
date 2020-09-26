package spark.scripts;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataHandline {
    List<HashMap<String,String>> listOfDict=new ArrayList<HashMap<String,String>>();
    String databank="src/main/java/spark/dataBank/";

    public void readDF(){
        Logger.getLogger("org").setLevel(Level.ERROR);
        HashMap<String,String>  dict=new HashMap<>();
        dict.put("name","Koushik");
        dict.put("intrest","Programming");
        listOfDict.add(dict);
        SparkConf sc=new SparkConf().setAppName("DataHandling").setMaster("local[*]");
        JavaSparkContext js=new JavaSparkContext(sc);
        JavaRDD<String> lines=js.textFile(databank+"/word_count.text");
        for(String line : lines.collect()){
            System.out.println(line);
        }
        js.close();
    }

    public void readCsv(String csvName){
        SparkSession session = SparkSession.builder().appName("ReadCSV").master("local[1]").getOrCreate();
        DataFrameReader dataFrameReader = session.read();
        Dataset<Row> responses = dataFrameReader.option("header","true").csv(databank+"/"+csvName);
        try{
            /*Dataset<Row> V_TXN_REF_NO=responses.withColumn("V_TXN_REF_NO",when(col("V_TXN_REF_NO")));
            Dataset<Row> V_ACCOUNT_NUMBER=responses.select(col("V_ACCOUNT_NUMBER"));
*/
            responses.select(col("V_TXN_REF_NO")).show();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            session.stop();
        }
    }

    public static void main(String a[]){
        Logger.getLogger("org").setLevel(Level.ERROR);
        DataHandline dataHandline=new DataHandline();
        dataHandline.readCsv("STG_RETIREMENT_ACCOUNTS_TXNS.csv");
    }


}
