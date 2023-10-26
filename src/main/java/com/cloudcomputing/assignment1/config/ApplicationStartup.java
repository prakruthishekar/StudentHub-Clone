package com.cloudcomputing.assignment1.config;

import com.cloudcomputing.assignment1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


@Configuration
@Component
public class ApplicationStartup {

    @Autowired
    private UserService userService;
    
//    @Value("${user.csv.path}")
//    private String csvpath;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) throws IOException {
        System.out.println("Order 1");
        // userService.createUserFromCSV("src/main/resources/users.csv");

        // Get the file URL from the classpath
        // URL resourceURL = getClass().getClassLoader().getResource("users.csv");
        // if(resourceURL != null) {
        //     String path = resourceURL.getPath();
        //     userService.createUserFromCSV(path);
        // } else {
        //     System.err.println("Unable to find users.csv on the classpath");
        // }

        // InputStream inputStream = getClass().getClassLoader().getResourceAsStream("users.csv");
        // String filePath = getClass().getClassLoader().getResource("users.csv").getPath();
        // System.out.println("File path" + filePath);
        // userService.createUserFromCSV(filePath);
        // System.out.println(inputStream);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("users.csv");
        // String filePath = getClass().getClassLoader().getResource("users.csv").getPath();
        if (inputStream != null) {
            userService.createUserFromCSV(inputStream);
        } else {
            // userService.createUserFromCSV("src/main/resources/users.csv");
            System.err.println("Unable to find users.csv on the classpath");
        }


    }
}
