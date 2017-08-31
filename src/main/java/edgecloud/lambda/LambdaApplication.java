package edgecloud.lambda;

import edgecloud.deviceserver.server.Device;
import edgecloud.deviceserver.server.DeviceServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan
public class LambdaApplication {



    public static void main(String args[]) {
        init();

//        SpringApplication.run(LambdaApplication.class, args);
        ApplicationContext ctx = SpringApplication.run(LambdaApplication.class, args);
        Thread deviceServer = new Thread(ctx.getBean(DeviceServerRun.class));
        deviceServer.start();
    }

    private static void init() {
    }


}
