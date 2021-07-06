package reUsables;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class    FileHandler {

    final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    static FileHandler obj = new FileHandler();

    private FileHandler() {
    }

    public static FileHandler on() {
        return obj;
    }

    public boolean createFreshFileorFolder(String path,boolean overWrite) {
        Boolean check = false;
        try {
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String paths[] = path.split(pattern);
            String folPath="";
            for (String folder : paths) {
                if(folPath.isEmpty()){
                    folPath=folder;
                }
                else {
                    folPath = folPath + "\\" + folder;
                }
                File file = new File(folPath);
                if (file.exists()==false && folder.contains(".")==false) {
                    System.out.println(folder);
                    file.mkdir();
                    check = true;
                }
                else if(folder.contains(".")){
                    if(overWrite){
                        file.delete();
                        file.createNewFile();
                        APP_LOGS.info("Created New File");
                    }
                    else {
                        if (!file.exists()) {
                            file.createNewFile();
                            APP_LOGS.info("Created New File");
                        } else {
                            APP_LOGS.info("File Exists");
                        }
                    }
                    check=true;
                }
                else{
                    check=false;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    public FileHandler cleanifFolder(String path){
        try {
            File file = new File(path);
            if (file.exists()) {
                FileUtils.cleanDirectory(new File(path));
                System.out.println(path+" contents has been removed");
            }
        }
        catch (Exception e){e.printStackTrace();}
        return obj;
    }

    public boolean deleteIfExist(String path){
        boolean status=false;
        File file=new File(path);
        if(file.exists()){
            file.delete();
            System.out.println("File deleted succesffully");
            status=true;
        }
        return status;
    }
}
