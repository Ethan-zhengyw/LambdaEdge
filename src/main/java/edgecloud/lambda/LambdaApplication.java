package edgecloud.lambda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication()
public class LambdaApplication {

    public LambdaApplication() {

    }

    public static void main(String args[]) {
        init();
        SpringApplication.run(LambdaApplication.class, args);
    }

    // TODO
    // 1. Query all available edge node
    private static void init() {
        updateNodes();
    }

    private static void updateNodes() {
    }

}
