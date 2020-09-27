package restAssured.httpResponseScripts;

import io.restassured.RestAssured;
import restAssured.WebServicesValidation;
import reusable.JsonHandler;

import static io.restassured.RestAssured.given;

public class ValidateHttpResponses {
    String inputJson=System.getProperty("user.dir")+"\\src\\main\\java\\restAssured\\inputJson\\"
            + WebServicesValidation.environmentName+".json";
    public ValidateHttpResponses(){
        RestAssured.baseURI=WebServicesValidation.environmentName;
        }
    public void validatePost(){
        JsonHandler jsonHandler=new JsonHandler();
        String inputBody=jsonHandler.readJsonasString(inputJson,"POST");
        String response = given().log().parameters().queryParam(WebServicesValidation.queryParameter)
                                .header("Content-Type","application/json")
                                .body(inputBody)
                                .when().post(WebServicesValidation.postUrl)
                                .then().extract().response().toString();
        System.out.println(response);
    }
}
