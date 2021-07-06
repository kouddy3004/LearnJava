package restAssured;

import com.csvreader.CsvReader;
import restAssured.httpResponseScripts.ValidateHttpResponses;

import java.io.FileReader;

public class WebServicesValidation {
    String path=System.getProperty("user.dir");
    public static String environmentName,baseUrl, getUrl, postUrl, deleteUrl, putUrl, queryParameter = "";
    ValidateHttpResponses validateHttpResponses;
    public WebServicesValidation(String environmentName){
        this.environmentName=environmentName;
        readEnv();
        validateHttpResponses=new ValidateHttpResponses();
    }


    public void readEnv() {
        try (FileReader read=new FileReader(path + "\\src\\main\\java\\restAssured\\dataBank\\environment.csv")) {
            CsvReader csv = new CsvReader(read);
            csv.readHeaders();
            while(csv.readRecord()){
                String envName = csv.get("EnvName");
                if(envName.equalsIgnoreCase(environmentName)){
                    baseUrl=csv.get("Base Url");
                    getUrl=csv.get("Get");
                    postUrl=csv.get("Post");
                    deleteUrl=csv.get("Delete");
                    putUrl=csv.get("Put");
                    queryParameter=csv.get("QueryParameter");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String validatePost(){
        String status="Fail";
        validateHttpResponses.validatePost();
        return status;
    }
}
