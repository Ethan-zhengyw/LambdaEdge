package edgecloud.lambda.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetConfig {

    public Properties readConfig(String configFile) {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configFile);

        Properties p = new Properties();

        try {
            p.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return p;
    }

}
