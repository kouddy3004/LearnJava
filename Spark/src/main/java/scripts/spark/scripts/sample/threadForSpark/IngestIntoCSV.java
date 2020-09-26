package scripts.spark.scripts.sample.threadForSpark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import spark.scripts.sample.SparkAndThread;

public class IngestIntoCSV extends Thread{
    Dataset<Row> copyDS;
    public IngestIntoCSV(Dataset<Row> copyDS){
        this.copyDS=copyDS;
    }
    public synchronized void run() {
        SparkAndThread.finalDs=SparkAndThread.finalDs.unionAll(copyDS);

        System.out.println("In Thread : "+Thread.currentThread().getName()+" "+java.time.LocalTime.now());
    }

}
