package Tesing;

import org.testng.annotations.Test;
import reUsables.JiraHandler;

public class TestJira {

    @Test
    public void deleteJira(){
        String iD="OFSIIA-2634";
        JiraHandler jiraHandler=new JiraHandler();
        if(jiraHandler.setJiraRestClient()){
            jiraHandler.deleteExecutions(iD);
            jiraHandler.logOutOfJira();
        }

    }
}
