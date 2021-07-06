package reUsables;

import HTTPClient.Codecs;
import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.NVPair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;


import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JiraHandler {

    //  String jiraDetails = System.getProperty("user.dir") + "\\src\\main\\java\\reUsables\\jiraDetails";
    HashMap<String, String> jiraAuth = new HashMap<>();
    HTTPConnection http = null;

    NVPair[] headers = {new NVPair("Content-Type", "application/json")};
    final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    HashMap<String, String> jiraMap = CommonScripts.on().readExcelByKey(MasterDriver.properties.getProperty("DatabankPath") + "\\dataBank.xlsx"
            , "JiraDetails", "GroupName", MasterDriver.testgroup);

    private String projectId = jiraMap.get("Project-id"),
            versionId = jiraMap.get("VersionID"),
            cycleId = jiraMap.get("CycleID"),
            folderID = jiraMap.get("FolderID");
    private String baseUrl = "https://jira.oraclecorp.com/";
    private String userName = "ofss_in@oracle.com";
    private String passWord = "Autologin123";
    private String loginUrl = "";
    private String issueAssignee = "koushik.subramanian@oracle.com";
    private String projectKey = "OFSIIA";

    public boolean setJiraRestClient() {
        boolean status = false;
        try {
            URL url = new URL(baseUrl);
            http = new HTTPConnection(url);
            String headerCreds = new sun.misc.BASE64Encoder().encode((userName + ":" + passWord).getBytes());
            List<NVPair> headers = new ArrayList<>();
            NVPair nvPair = new NVPair("Authorization", "Basic " + headerCreds);
            headers.add(nvPair);
            http.setDefaultHeaders(new NVPair[]{headers.get(0)});
            http.removeModule(HTTPClient.CookieModule.class);
            status = jiraIsUp(http);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public boolean jiraIsUp(HTTPConnection conn) {
        boolean status = false;
        try {
            HTTPResponse response = conn.Get("jira/status");
            if (response.getStatusCode() == 200) {
                status = true;
            }
            ;
        } catch (Exception e) {
            APP_LOGS.info("Jira Connectivity issue  " + e.getMessage());
        }
        return status;
    }

    public void getIssueDetails(String issueKey) {
        try {
            HTTPResponse response = http.Get("jira/rest/auth/1/session");
            String responseData = new String(response.getData());
            System.out.println("Response : " + responseData);
            System.out.println(" Node : " + response.getHeader("X-ANODEID"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public HTTPResponse logOutOfJira() {
        HTTPResponse response = null;
        try {
            response = http.Delete("/jira/rest/auth/1/session");
            int statusCode = response.getStatusCode();
            if (statusCode != 204) {
                String responseData = new String(response.getData());
                System.out.println("Problem logging out of Jira. Status code "
                        + statusCode + " was returned with response data: "
                        + responseData);
            } else {
                System.out.println("Jira logout successful..!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public String createIssueInJira() {
        String issueKey = "";
        try {
            // Create an issue of type Test in the project
            String jsonStr = "{\"fields\":{\"issuetype\":{\"name\":\"Test" + "\"},\"summary\":\""
                    + MasterDriver.testData.get("TestcaseName")
                    + "\",\"assignee\":{\"name\":\"" + issueAssignee + "\"},\"project\":{\"key\":\"" + projectKey
                    + "\"}}}";
            /*String jsonStr = "{\"fields\":{\"issuetype\":{\"name\":\"Test" + "\"},\"summary\":\""
                    + MasterDriver.testData.get("TestcaseName")+"\","+
                    "\"assignee\":{\"name\":\"" + issueAssignee + "\"},\"project\":{\"key\":\"" + projectKey
                    + "\",\"Sprint\": \"IAA_Mar_2021\",\"Scrum Team\": \"Automation\"} " +
                    "}}";*/
            System.out.println(jsonStr);
            HTTPResponse response = http.Post("/jira/rest/api/latest/issue", jsonStr.getBytes(), headers);
            int statusCode = response.getStatusCode();
            String responseData = new String(response.getData());
            if (statusCode == 201) {
                // issue was created successfully
                APP_LOGS.info("JIRAIssueCreation:Success. Response data: " + statusCode + "::" + responseData);
                System.out.println("JIRAIssueCreation:Success. Response data: " + statusCode + "::" + responseData);
            } else {
                // issue creation failed
                APP_LOGS.info("JIRAIssueCreation:Failed. Response data: " + statusCode
                        + " was returned with response data: " + responseData);
                System.out.println("JIRAIssueCreation:Failed. Response data: " + statusCode
                        + " was returned with response data: " + responseData);
            }

            JSONObject jsonRes = new JSONObject(responseData);
            System.out.println("jsonRes:" + jsonRes);

            issueKey = JsonFileHandler.on().parseJson(jsonRes, "key").toString();
            System.out.println("issueKey:" + issueKey);

        } catch (Exception e) {
            e.printStackTrace();
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->JIRA Issue Creation failed");
        }
        return issueKey;
    }

    public boolean addTestsToCycle(String issueKey) {
        boolean status = false;
        try {

            projectId = jiraMap.get("Project-id");
            versionId = jiraMap.get("VersionID");
            cycleId = jiraMap.get("CycleID");
            String jsonStr = "{  \"issues\": [\"" + issueKey + "\"],  \"versionId\": \"" + versionId + "\",  \"cycleId\": \""
                    + cycleId + "\",  \"projectId\": \"" + projectId + "\",  \"method\": \"1\"}";
            System.out.println("jsonStr:" + jsonStr);
            HTTPResponse response = http.Post("/jira/rest/zapi/latest/execution/addTestsToCycle/", jsonStr.getBytes(), headers);
            int statusCode = response.getStatusCode();
            if (statusCode == 200) {
                String responseData = new String(response.getData());
                System.out.println("Test Added to Cycle successfully");
                System.out.println("Response from Get call: " + statusCode + "::" + responseData);
                status = true;
            } else {
                String responseData = new String(response.getData());
                System.err.println("Test Added to Cycle failed. Status code " + statusCode
                        + " was returned with response data: " + responseData);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->JIRA  Add Tests to Cycle failed");

        }
        return status;
    }

    public boolean moveCycleToFolder() {
        boolean status=false;
        try {
            if (!CommonScripts.on().stringIsNullOrEmpty(folderID)) {
                String jsonStr = "{\"projectId\": \"" + projectId + "\",  \"versionId\": \"" + versionId + "\""
                        + ",\"schedulesList\": []}";
                HTTPResponse response = http.Put("/jira/rest/zapi/latest/cycle/" + cycleId + "/move/executions/folder/" + folderID + "",
                        jsonStr, headers);
                int statusCode = response.getStatusCode();
                String responseData = new String(response.getData());
                System.out.println("Response data: " + statusCode + "::" + responseData);
                if (statusCode == 200) {
                    APP_LOGS.info("JIRAMoveExecCycleToFolder. Response data: " + statusCode + "::" + responseData);
                    System.out.println("JIRAMoveExecCycleToFolder. Response data: " + statusCode + "::" + responseData);
                    status=true;
                } else {
                    System.err.println("JIRAMoveExecCycleToFolder. Response data: " + statusCode + "::" + responseData);

                }

            }

        } catch (Exception e) {

            e.printStackTrace();
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->JIRA Move Cycle to Folder failed");

        }
        return status;
    }

    public void deleteExecutions(String id) {
        Object execid = null;
        try {
            HTTPResponse response = http.Delete("/jira/rest/zapi/latest/execution/" + id + "");
            int statusCode = response.getStatusCode();
            APP_LOGS.info(statusCode);
            String responseData = new String(response.getData());
            System.out.println("responseData:" + responseData);
        } catch (Exception e) {

        }
    }

    public void putIssueKeyinDataBank(String issueKey) {
        try {

            HashMap<String, String> jiraMap = JsonFileHandler.on()
                    .readValueFromJsoninMap(MasterDriver.jiraDetailPath);
            APP_LOGS.info("Kousik " + jiraMap);
            if (!jiraMap.containsKey("error")) {
                jiraMap.put(MasterDriver.testCaseId, issueKey);
                JsonFileHandler.on().writeIntoJson(jiraMap, MasterDriver.jiraDetailPath);
            } else {
                jiraMap.clear();
                jiraMap.put(MasterDriver.testCaseId, issueKey);
                JsonFileHandler.on().writeIntoJson(jiraMap, MasterDriver.jiraDetailPath);
            }
            APP_LOGS.info("Kousik " + jiraMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateExecStatusInJIRA(String sIssueKey, int status) {
        Object execid = null;
        try {
            HTTPResponse response = http.Get("/jira/rest/zapi/latest/execution/?action=expand&cycleId=" + cycleId + "&folderId=" + folderID);
            int statusCode = response.getStatusCode();
            String responseData = new String(response.getData());
            if (statusCode == 200) {
                JSONObject jsonRes = new JSONObject(responseData);
                JSONArray array = jsonRes.getJSONArray("executions");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    Object id = object.get("id");
                    System.out.println("ID:" + id);
                    Object issueKey = object.get("issueKey");
                    System.out.println("issueKey:" + issueKey);
                    if (issueKey.equals(sIssueKey)) {
                        execid = id;
                    }
                }

                String jsonStr = "{  \"status\": \"" + status + "\"}";

                System.out.println("/jira/rest/zapi/latest/execution/" + execid + "/execute");
                System.out.println("Json Str : " + jsonStr);
                System.out.println("headers : " + headers);
                response = http.Put("/jira/rest/zapi/latest/execution/" + execid + "/execute", jsonStr.getBytes(), headers);
                statusCode = response.getStatusCode();
                responseData = new String(response.getData());
                if (statusCode != 200) {
                    //APP_LOGS.info("JIRAExecStatusUpdate:Failed. Response data: " + statusCode + "::" + responseData);
                    System.err.println("JIRAExecStatusUpdate:Failed. Response data: " + statusCode + "::" + responseData);
                }
            } else {
                //APP_LOGS.info("JIRAExecStatusUpdate:Failed. Response data: " + statusCode + "::" + responseData);
                System.err.println("JIRAExecStatusUpdate:Failed. Response data: " + statusCode + "::" + responseData);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->JIRA Execution Status update failed");
        }
    }

    public void pushAttachmentToIssue(String issueKey, String filePath) {
        try {
            System.out.println(filePath);
            String url = "/jira/rest/api/latest/issue/" + issueKey + "/attachments";
            NVPair[] file = {new NVPair("file", filePath)}; // Path to the file
            NVPair[] hdrs = new NVPair[2];
            hdrs[1] = new NVPair("X-Atlassian-Token", "no-check"); // Required by Jira

            byte[] data = Codecs.mpFormDataEncode(null, file, hdrs);
            HTTPResponse response = http.Post(url, data, hdrs);

            String responseJson = new String(response.getData());
            System.out.println(String.format("Response: \n %s %s \n Body: \n %s", response.getStatusCode(),
                    response.getReasonLine(), responseJson));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
            APP_LOGS.info(new Object() {
            }.getClass().getEnclosingMethod().getName() + "->JIRA Add Attachment to Issue failed");

        }
    }

    public void moveIssueToFolder(String issueKey){
        try {
            String jsonStr = "{  \"issues\": [\"" + issueKey + "\"],  \"versionId\": \"" + versionId + "\",  \"cycleId\": \""
                    + cycleId + "\",  \"projectId\": \"" + projectId + "\",  \"method\": \"1\"}";
            System.out.println("jsonStr:" + jsonStr);
            HTTPResponse response = http.Post("/jira/rest/zapi/latest/execution/addTestsToCycle/", jsonStr.getBytes(), headers);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

