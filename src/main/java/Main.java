import restAssured.WebServicesValidation;
import reusable.JsonHandler;

public class Main {

    public static void main(String[] args) {
        WebServicesValidation ws=new WebServicesValidation("RahulShetty");
        ws.validatePost();

    }
}
