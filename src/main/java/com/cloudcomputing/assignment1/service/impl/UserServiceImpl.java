package com.cloudcomputing.assignment1.service.impl;

import com.cloudcomputing.assignment1.entity.Assignment;
import com.cloudcomputing.assignment1.entity.User;
import com.cloudcomputing.assignment1.repository.AssignmentRepository;
import com.cloudcomputing.assignment1.repository.UserRepository;
import com.cloudcomputing.assignment1.service.AssignmentService;
import com.cloudcomputing.assignment1.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    public void createUserFromCSV(InputStream csvFilePath) throws IOException {
        List<User> usersFromCSV = parseCSV(csvFilePath);
        for (User user : usersFromCSV) {
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser == null) {
                userRepository.save(user); // Create a new user if it doesn't exist
            }
        }
    }

    public boolean isUserAuthenticated(String header) {
        try {
            // Decode the Base64 string
            byte[] decodedBytes = Base64.getDecoder().decode(header.split(" ")[1]);
            // Convert the decoded bytes to a string (assuming it's text)
            String decodedString = new String(decodedBytes);
            // Split the decoded string into username and password
            String[] parts = decodedString.split(":");
            String username = parts[0];
            String inputPassword = parts[1];

            System.out.println("InputPassword: " + inputPassword);
            User existingUser = userRepository.findByEmail(username);

            if (existingUser == null) {
                System.out.println("USER NOT FOUND");
                return false;
            }

            boolean passwordMatch = BCrypt.checkpw(inputPassword, existingUser.getPassword());
            if (passwordMatch){
                System.out.println("USER FOUND");
            }
            else{
                System.out.println("USER NOT FOUND");
            }
            return passwordMatch;
        } catch (Exception e) {
            // Handle any exceptions (e.g., decoding or splitting errors)
            e.printStackTrace();
            return false;
        }
    }


    // private List<User> parseCSV(InputStream csvFilePath) throws IOException {
    //     // Implementation of CSV parsing logic and return a list of User objects
    //     ArrayList<User> userList = new ArrayList<>();
    //     File csvFile = new File(csvFilePath);
    //     List<String> str = new ArrayList<>();
    //     try (Scanner scanner = new Scanner(csvFile)) {
    //         while (scanner.hasNextLine()) {
    //             String line = scanner.nextLine();
    //             String[] fields = line.split(" ");
    //             for (String field : fields) {
    //                 str.add(field);
    //             }
    //         }

    //         str.remove(0);
    //         for(String s : str){
    //             System.out.println("String: "+s);
    //             List<String> csvRecord = Arrays.stream(s.split(",")).toList();
    //             System.out.println("firstName: "+csvRecord.get(0));
    //             String firstName = csvRecord.get(0);
    //             System.out.println("lastName: "+csvRecord.get(1));
    //             String lastName = csvRecord.get(1);
    //             System.out.println("email: "+csvRecord.get(2));
    //             String email = csvRecord.get(2);
    //             System.out.println("password: "+csvRecord.get(3));
    //             String password =  BCrypt.hashpw(csvRecord.get(3), BCrypt.gensalt());
    //             System.out.println("DataBase Storing Password " + password);
    //             User user = new User(firstName, lastName, email, password);
    //             userList.add(user);
    //             System.out.println("USER: "+user);
    //         }
    //     }
    //     System.out.println("USERLIST: "+userList);
    //     return userList;
    // }

    private List<User> parseCSV(InputStream csvStream) throws IOException {
    ArrayList<User> userList = new ArrayList<>();
    
    // Use a BufferedReader with InputStreamReader for InputStream
    try (BufferedReader br = new BufferedReader(new InputStreamReader(csvStream))) {
        String line;

        // Skip header if your CSV has one
        line = br.readLine();
        System.out.println("Header (if any): " + line);

        while ((line = br.readLine()) != null) {
            System.out.println("Raw CSV Line: " + line);
            String[] fields = line.split(",");
            if (fields.length < 4) {
                // Log error or throw exception if the line doesn't have enough data
                System.err.println("Invalid CSV line (not enough fields): " + line);
                continue;
            }
            
            String firstName = fields[0].trim();
            String lastName = fields[1].trim();
            String email = fields[2].trim();
            String rawPassword = fields[3].trim();
            String password = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

            System.out.println("Parsed - firstName: " + firstName);
            System.out.println("Parsed - lastName: " + lastName);
            System.out.println("Parsed - email: " + email);
            System.out.println("Raw Password: " + rawPassword);
            System.out.println("Hashed Password: " + password);

            User user = new User(firstName, lastName, email, password);
            userList.add(user);
            System.out.println("User Object Created: " + user);
        }
    }

    System.out.println("All Users Parsed: " + userList);
    return userList;
}

}


