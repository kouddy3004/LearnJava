package reUsables;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class SftpHandler {
    final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    static SftpHandler obj = new SftpHandler();
    ;

    private SftpHandler() {
    }

    String projectPath = System.getProperty("user.dir");

    public static SftpHandler on() {
        return obj;
    }

    public Channel createChannel(String str_Username, String str_Password, String str_Host) {
        JSch obj_JSch = new JSch();
        Session obj_Session = null;
        Channel obj_Channel = null;

        String str_FileDirectory = "/scratch/" + str_Username + "/Automation_Properties/";
        String str_FileName = "Env.props";
        try {
            obj_Session = obj_JSch.getSession(str_Username, str_Host, 22);
            //obj_Session.setPort(int_Port);
            obj_Session.setPassword(str_Password);
            Properties obj_Properties = new Properties();
            obj_Properties.put("StrictHostKeyChecking", "no");
            obj_Session.setConfig(obj_Properties);
            obj_Session.connect();
            obj_Channel = obj_Session.openChannel("sftp");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj_Channel;
    }

    public String readFileinServer(ChannelSftp obj_SFTPChannel, String path, String fileName) {
        StringBuilder obj_StringBuilder = new StringBuilder();
        try {
            obj_SFTPChannel.cd(path);
            InputStream obj_InputStream = obj_SFTPChannel.get(fileName);
            char[] ch_Buffer = new char[0x10000];
            Reader obj_Reader = new InputStreamReader(obj_InputStream, "UTF-8");
            int int_Line = 0;
            do {
                int_Line = obj_Reader.read(ch_Buffer, 0, ch_Buffer.length);
                if (int_Line > 0) {
                    obj_StringBuilder.append(ch_Buffer, 0, int_Line);
                    APP_LOGS.info(int_Line);
                    APP_LOGS.info("************************");
                }
            }
            while (int_Line >= 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj_StringBuilder.toString();
    }

    public HashMap<String, String> setEnvironmentDetails() {
        String str_Host = System.getProperty("hostName");
        String str_Username = System.getProperty("Username");
        String str_Password = System.getProperty("Password");
        HashMap<String, String> env = new HashMap<>();
        try {
            Channel channel = createChannel(str_Username, str_Password, str_Host);
            channel.connect();
            APP_LOGS.info("Session Connected");
            String envString = readFileinServer((ChannelSftp) channel, "/scratch/" + str_Username + "/Automation_Properties/", "Env.props");
            env = CommonScripts.on().string2Map(envString.toString(), "\\r?\\n", "=");
            env.forEach((K, V) -> System.out.println(K + "= " + V));
            String envJson = MasterDriver.properties.getProperty("DatabankPath")+"\\env.json";
            JsonFileHandler.on().writeIntoJson(env, envJson);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        return env;
    }
    public HashMap<String, String> getEnvironmentDetails(Properties properties) {
        HashMap<String, String> env = new HashMap<>();
        try {
            String envJson = properties.getProperty("DatabankPath")+"\\env.json";
            env=JsonFileHandler.on().readValueFromJsoninMap(envJson);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        return env;
    }

}
