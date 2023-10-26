package com.cloudcomputing.assignment1.util;

import java.util.Base64;

public class Util {

    public static String getUserName(String headerValue){
        byte[] decodedBytes = Base64.getDecoder().decode(headerValue.split(" ")[1]);
        // Convert the decoded bytes to a string (assuming it's text)
        String decodedString = new String(decodedBytes);
        // Split the decoded string into username and password
        String[] parts = decodedString.split(":");
        String username = parts[0];
        return username;
    }
}
