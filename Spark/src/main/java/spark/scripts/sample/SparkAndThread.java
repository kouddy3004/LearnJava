package spark.scripts.sample;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import scripts.spark.scripts.sample.threadForSpark.IngestIntoCSV;


import java.util.ArrayList;

import static org.apache.spark.sql.functions.*;

public class SparkAndThread {
    SparkSession sparkSession;
    public static Dataset<Row> finalDs;
    public static String databank="src/main/java/spark/dataBank/";
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
            finalDs = sparkSession.createDataFrame(new ArrayList<>(),df.schema());
            IngestIntoCSV[] csvs=new IngestIntoCSV[(endLoop/10)];
            for(int i = 1; i<=endLoop; i++) {
                Dataset<Row> copyDs = df.withColumn("v_policy_code".toUpperCase(),
                        concat(lit("RET" + i)));//EXT_DISVFA_TRANS_LIFE
                if (i%10==0){
                    System.out.println("In on Loop of "+i);
                    int objV=(i/10)-1;
                    System.out.println("Ready For "+Integer.toString(objV));
                    csvs[objV]=new IngestIntoCSV(copyDs);
                    csvs[objV].setName("Thread For "+i);
                    csvs[objV].start();
                }
            }
            for(IngestIntoCSV csv:csvs){
                csv.join();
            }
            System.out.println("Ingesting Dataframe  Completed at "+java.time.LocalTime.now());
            System.out.println(finalDs.count());
            SparkAndThread.finalDs
                    .write().format("com.databricks.spark.csv")
                    .option("header", "true")
                    .mode(SaveMode.Overwrite).csv(SparkAndThread.databank+"data.csv");
            System.out.println("Completed "+java.time.LocalTime.now());
        }
        catch(Exception e){e.printStackTrace();}
        finally {sparkSession.stop();
            System.out.println("Spark Session Stopped");}

    }

    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.ERROR);
        SparkAndThread obj=new SparkAndThread();
        obj.ingestIntoCSV("STG_RETIREMENT_ACCOUNTS_TXNS.csv");
    }
}
