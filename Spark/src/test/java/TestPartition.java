import org.apache.commons.io.FileUtils;
import scripts.java.reusable.CsvFileHandler;

import java.io.File;

public class TestPartition {
        public void testPartition(){
            int startLoop=40;
            int endLoop=100;
            int getPartiotion=10;
            int partitionstart=1;
            int partitionEnd=0;
            if(endLoop%getPartiotion==0) {
                for(int i=1;i<=endLoop/getPartiotion;i++) {
                    System.out.println(partitionEnd);
                    if (partitionEnd < endLoop) {
                        System.out.println("Partition By " + i);
                        int setPartition=getPartiotion;
                        if((endLoop/setPartition)>setPartition){
                            setPartition=endLoop/setPartition;
                        }
                        partitionEnd = i * (setPartition);
                        if(partitionEnd>=partitionstart) {
                            if(partitionEnd>=startLoop) {
                                System.out.println("Starts at " + partitionstart + " and Ends at " + partitionEnd);
                            }
                            else{
                                System.out.println("Not Started for " + partitionstart + " and Ends at " + partitionEnd);
                            }
                            partitionstart = partitionEnd + 1;
                        }

                    }
                    else{
                        break;
                    }
                }
                if(partitionstart!=1){System.out.println("Partition Ended for "+(endLoop-startLoop));}
                else{System.out.println("Partition Ended for "+(partitionEnd));}
            }

            else {
                System.out.println("Unable to Partition. Your End Loop and partition should get  divisible. So not able to generate CSV");
            }
        }

        public void checkFilePath() throws Exception{
            CsvFileHandler csv=new CsvFileHandler();
            //String path=System.getProperty("user.dir")+"\\src\\main\\java\\scripts\\java\\result\\STG_INS_POLICY_CASH_FLOW\\STG_INS_POLICY_CASH_FLOW.csv";
            String path=System.getProperty("user.dir")+"\\src\\main\\java\\scripts\\java\\result\\STG_INS_POLICY_CASH_FLOW";
            FileUtils.cleanDirectory(new File(path));

        }

        public void checkSubstring(){
            String source = "DISVFAA_anty_3".replaceAll("[0-9]","");
            System.out.println(source);

        }
    public static void main(String[] args) throws Exception{
        TestPartition obj=new TestPartition();
        obj.testPartition();
    }
}

