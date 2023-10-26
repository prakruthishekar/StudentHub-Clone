package com.cloudcomputing.assignment1.service;


import java.io.IOException;
import java.io.InputStream;

public interface UserService {
    void createUserFromCSV(InputStream csvFilePath) throws IOException;
    boolean isUserAuthenticated(String header);
}