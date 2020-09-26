package spark.scripts.sample;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.Properties;

import static org.apache.spark.sql.functions.*;


public class LearnSpark {

    SparkSession sparkSession;
    String databank="src/main/java/spark/dataBank/";


    public void ingestIntoCSV(String csvName){
        try {
            int endLoop=1000;
            sparkSession = new SparkSession.Builder().appName("DB To CSV").master("local[*]").getOrCreate();
            System.out.println("Session started at "+java.time.LocalTime.now());
            String query = "select * from STG_INS_POLICY_CASH_FLOW where v_policy_code ='DISVFA_TRANS1' and fic_mis_date='31-DEC-20'";
            Dataset<Row> df = sparkSession.sqlContext().read()
                    .format("jdbc")
                    .option("url", "jdbc:oracle:thin:@whf00pjx.in.oracle.com:1521:IFRS19PDB")
                    .option("query",query)
                    .option("user", "volatm81")
                    .option("password", "ifrs17user")
                    .option("driver", "oracle.jdbc.driver.OracleDriver")
                    .load();
            System.out.println("Values fetched from DB at "+java.time.LocalTime.now());
            Dataset<Row> finalDs = sparkSession.createDataFrame(new ArrayList<>(),df.schema());
            for(int i = 1; i<=endLoop; i++) {
                Dataset<Row> copyDs = df.withColumn("v_policy_code".toUpperCase(), concat(lit("EXT_DISVFA_TRANS_LIFE" + i)));
                finalDs=finalDs.unionAll(copyDs);
                if (i%100==0){
                    System.out.println("IN Loop for "+i+" : "+java.time.LocalTime.now());
                }
            }
            System.out.println("Ingesting Dataframe  Completed at "+java.time.LocalTime.now());
            finalDs.repartition(1)
                    .write().format("com.databricks.spark.csv")
                    .option("header", "true")
                    .mode(SaveMode.Overwrite).csv(databank+"data.csv");
            System.out.println("Completed "+java.time.LocalTime.now());
        }
        catch(Exception e){e.printStackTrace();}
        finally {sparkSession.stop();}

    }

    public void readDatabase(){
        try {

            sparkSession = new SparkSession.Builder().appName("Read DB").master("local[*]").getOrCreate();
            System.out.println("Session started at "+java.time.LocalTime.now());
            System.out.println(sparkSession.sparkContext().uiWebUrl());
            String query = "select * from STG_INS_POLICY_CASH_FLOW where v_policy_code ='DISVFA_TRANS1' and fic_mis_date='31-DEC-20'";
            Dataset<Row> df = sparkSession.sqlContext().read()
                                        .format("jdbc")
                                        .option("url", "jdbc:oracle:thin:@whf00pjx.in.oracle.com:1521:IFRS19PDB")
                                        .option("query",query)
                                        .option("user", "volatm81")
                                        .option("password", "ifrs17user")
                                        .option("driver", "oracle.jdbc.driver.OracleDriver")
                                        .load();
            System.out.println("Values fetched from DB at "+java.time.LocalTime.now());
            Dataset<Row> finalDs = sparkSession.createDataFrame(new ArrayList<>(),df.schema());
            for(int i = 1; i<=100; i++) {
                Dataset<Row>  copyDs = df.withColumn("v_policy_code",concat(lit("EXT_DISVFA_TRANS_LIFE"+i)));
                finalDs=finalDs.unionByName(copyDs);
            }
            System.out.println("Fetched DB Values put into DatFrame at "+java.time.LocalTime.now());
            finalDs.repartition(6).write().format("jdbc")
                    .option("url", "jdbc:oracle:thin:@whf00pjx.in.oracle.com:1521:IFRS19PDB")
                    .option("dbtable","STG_LIFE_INS_POLICY_TXNS_spark")
                    .option("user", "volatm81")
                    .option("password", "ifrs17user")
                    .option("driver", "oracle.jdbc.driver.OracleDriver")
                    .mode(SaveMode.Append).save();
            System.out.println("Ingesting Dataframe to Database Completed at "+java.time.LocalTime.now());
        }
        catch(Exception e){e.printStackTrace();}
        finally {sparkSession.stop();
            System.out.println("Spark Session stopped successfully");}
    }
    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.ERROR);
        LearnSpark learnSpark=new LearnSpark();
        learnSpark.ingestIntoCSV("STG_RETIREMENT_ACCOUNTS_TXNS.csv");
       // learnSpark.readDatabase();
    }
}
