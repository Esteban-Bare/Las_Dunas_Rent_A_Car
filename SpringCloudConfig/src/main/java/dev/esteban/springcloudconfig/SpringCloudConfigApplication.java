package dev.esteban.springcloudconfig;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class SpringCloudConfigApplication {
    static {
        try {
            String userDir = System.getProperty("user.dir") + "/env";
            Dotenv dotenv = Dotenv.configure()
                    .directory(userDir)
                    .load();
            dotenv.entries().forEach((entry) -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            System.out.println("Loaded .env file from: " + userDir);
        } catch (Exception e) {
            System.err.println("Error loading .env file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigApplication.class, args);
    }


}
