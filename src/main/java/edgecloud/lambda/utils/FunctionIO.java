package edgecloud.lambda.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FunctionIO {

    private static Logger log = LoggerFactory.getLogger(FunctionIO.class);

    public static boolean writeFunction(String funcBody, String funcPath) throws IOException {

        File file = new File(funcPath);

        if (!file.getParentFile().exists()) {
            log.info("Directory doesn't exist, creating...: ", file.getParent());
            if (!file.getParentFile().mkdir()) {
                log.info("Create directory failed.");
                return false;
            }
            log.info("Finished.");
        }

        if (!file.exists()) {
            log.info("File not exist, creating...: ", file.getPath());
            if (!file.createNewFile()) {
                log.info("Create new file failed.");
                return false;
            }
            log.info("Finished.");
        }

        // 清空已存在的内容
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();

        try{
            writer = new PrintWriter(funcPath, "UTF-8");
            writer.println(funcBody);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Write function finished.");

        return true;
    }

    public static String readFunction(String funcPath) throws FileNotFoundException {
        File file = new File(funcPath);

        if (!file.exists()) {
            log.info("Function file not exist: ", funcPath);
        }

        String funcBody;
        try {
            funcBody = new String(Files.readAllBytes(Paths.get(funcPath)));
        } catch (IOException e) {
            log.info("Read funcBody failed.");
            funcBody = "";
        }

        return funcBody;
    }

}
