package com.codeahoy.alluvium.server;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author umer
 */
@SpringBootApplication
public class Application {

    /**
     * Starts the application.
     *
     * @param args Command line args, for example, --debug to enable boot strapping logging
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AlluviumConfiguration.class, Application.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.run(args);
    }
}
