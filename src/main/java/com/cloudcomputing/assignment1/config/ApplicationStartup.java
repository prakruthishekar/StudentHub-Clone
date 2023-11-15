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
    
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) throws IOException {
        System.out.println("Order 1");
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
