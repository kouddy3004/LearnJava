package scripts.spark.scripts.sample;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.Map;

public class WordCount {

    public static void main(String[] args) throws Exception {

        Logger.getLogger("org").setLevel(Level.ERROR);
        SparkConf conf = new SparkConf().setAppName("wordCounts").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("src/main/java/spark/dataBank/word_count.text");
        JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());

        Map<String, Long> wordCounts = words.countByValue();

        for (Map.Entry<String, Long> entry : wordCounts.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
