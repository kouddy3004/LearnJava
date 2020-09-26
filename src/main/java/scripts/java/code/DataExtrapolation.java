package scripts.java.code;

import com.sun.org.apache.xalan.internal.xsltc.trax.XSLTCSource;
import org.apache.commons.io.FileUtils;
import scripts.java.reusable.CommonScripts;
import scripts.java.reusable.CsvFileHandler;
import scripts.java.reusable.DbService;
import scripts.java.reusable.ThreadForCSvWrite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataExtrapolation {
    public static int startLoop=2;
    public static Connection conn = null;
    public static String databank=System.getProperty("user.dir")+"\\src\\main\\java\\scripts\\java\\result\\";
    private CommonScripts cs=new CommonScripts();
    public void ingestDbtoCSV(String tableName,String whereClause,String replacePolicy) throws Exception {
        String resultPath="";
        resultPath=databank+tableName+"\\";
        int endLoop=2;
        int getPartiotion=1;
        int partitionstart=1;
        int partitionEnd=0;
        if(endLoop%getPartiotion==0) {
            File file= new File(resultPath);
            if(file.exists()) {
                FileUtils.cleanDirectory(new File(resultPath));
            }
            System.out.println(resultPath);
            System.out.println("Working for " + endLoop + " Reps");
            String query = "select * from " + tableName + " " + whereClause;//where v_policy_code ='DISVFA_TRANS1' and fic_mis_date='31-DEC-20'";
            DbService db = new DbService();
            List<HashMap<String, String>> dbValues = db.select(conn, query,"dd-MMM-yy");
            System.out.println("No of Values fetched from DB is "+dbValues.size()+" at " + java.time.LocalTime.now());
            List<HashMap<String, String>> repliCaValues = new ArrayList<>();
            //ThreadForCSvWrite[] threads=new ThreadForCSvWrite[endLoop/getPartiotion];
            if (dbValues.size() > 0) {
                CsvFileHandler csv = new CsvFileHandler();
                for (int i = 1; i <= endLoop / getPartiotion; i++) {
                    if (partitionEnd < endLoop) {
                        System.out.println("Partition By " + i);
                        int setPartition=getPartiotion;
                        if((endLoop/setPartition)>setPartition){
                            setPartition=endLoop/setPartition;
                        }
                        partitionEnd = i * (setPartition);
                        if(partitionEnd>=startLoop) {
                            ThreadForCSvWrite thread = new ThreadForCSvWrite(dbValues, tableName, partitionstart, partitionEnd, replacePolicy);
                            thread.run();
                        }
                        partitionstart = partitionEnd + 1;
                    } else {
                        break;
                    }
                }
                int dbsize=dbValues.size();
                int csvSize=(cs.getLatsRow(resultPath+"\\"+tableName+".csv")-1);
                if(startLoop!=1){dbsize=(dbsize*((endLoop-startLoop)+1));}
                else{dbsize=dbsize*(partitionEnd);}
                System.out.println("DB Size "+dbsize);
                System.out.println("CSV Size "+csvSize);
                if(csvSize==(dbsize)) {
                    System.out.println(dbsize + " Values are Loaded at " + java.time.LocalTime.now());
                }
                else{
                    System.out.println(" Values are not loaded As we Expected " + java.time.LocalTime.now());
                }
            }
            else{
                System.out.println("Unable to Fetch Sample Data for the query:\n"+query);
            }
        } else {
                System.out.println("Unable to Partition. Your End Loop(" + endLoop + ") and partition(" + getPartiotion + ") should be  divisible. So not able to generate CSV");

        }

    }



    public static void main(String[] args) throws Exception {
        System.out.println("Program started at " + java.time.LocalTime.now());
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //conn = DriverManager.getConnection("jdbc:oracle:thin:@whf00pjx.in.oracle.com:1521:IFRS19PDB", "volatm81", "ifrs17user");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@whf00btx.in.oracle.com:1521:IFRS18PDB", "ouxatm81", "ifrs17user");
            DataExtrapolation data = new DataExtrapolation();
            //data.ingestDbtoCSV("STG_LIFE_INS_POLICY_TXNS","where v_account_number ='DISVFA_TRANS1' and fic_mis_date='31-DEC-20'","EXT_DISVFA_TRANS_LIFE");
            data.ingestDbtoCSV("STG_ANNUITY_TXNS","where v_account_number ='DISVFAA_anty_3' and fic_mis_date='31-DEC-20'","EXT_DISVFA_TRANS_ANN");
            //data.ingestDbtoCSV("STG_RETIREMENT_ACCOUNTS_TXNS","where v_account_number ='DISVFAA_retire_acc_3' and fic_mis_date='31-DEC-20'","EXT_DISVFA_TRANS_RET");
            //data.ingestDbtoCSV("STG_POLICY_COVERAGES","where v_account_number ='DISVFA_TRANS1' and fic_mis_date='31-DEC-20'","EXT_DISVFA_TRANS_LIFE");
            //data.ingestDbtoCSV("STG_INS_POLICY_CASH_FLOW","where v_policy_code ='DISVFA_TRANS1' and fic_mis_date='31-DEC-20'","EXT_DISVFA_TRANS_LIFE");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //conn.close();
            System.out.println("Connection CLosed Successfully");
        }
    }
}
