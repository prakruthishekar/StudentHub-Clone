package com.cloudcomputing.assignment1.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.cloudcomputing.assignment1.contoller.AssignmentController;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SnsService {

    @Value("${snsTopicArn}")
    private String snsTopicArn;

    @Value("${awsRegion}")
    private String awsRegion;

    private static final Logger logger = LoggerFactory.getLogger(SnsService.class);

    private final AmazonSNS snsClient;
    // snsService.publishToTopic(username, submissionDto.getSubmission_url(), submissionDto.getAssignment_id(), user.getFirstName(), user.getLastName());
   @Autowired
    public SnsService(@Value("${awsRegion}") String awsRegion, 
                      @Value("${snsTopicArn}") String snsTopicArn) {
        this.snsClient = AmazonSNSClientBuilder.standard()
                                               .withRegion(Regions.fromName(awsRegion))
                                               .build();
        this.snsTopicArn = snsTopicArn;
    }

    public void publishToTopic(String user, String url, UUID assignment_id, String first_name, String last_name ) {
        // String message = createMessage(user, url);
        String message = createMessage(user, url, assignment_id, first_name, last_name);
        try {
            snsClient.publish(snsTopicArn, message);
        } catch (AmazonServiceException e) {
            // Handle AWS service exceptions
            logger.error("Error publishing to SNS topic: {}", e.getMessage());
        } catch (SdkClientException e) {
            // Handle AWS client exceptions
            logger.error("Error in AWS SDK: {}", e.getMessage());
        }
        System.out.println("UserName: " + user);
    }

    private String createMessage(String user, String url, UUID assignment_id, String first_name, String last_name) {
        // Convert the data to a JSON format
        return String.format("{\"user_email\": \"%s\", \"submission_url\": \"%s\", \"assignment_id\": \"%s\", \"first_name\": \"%s\", \"last_name\": \"%s\"}", 
                             user, url, assignment_id.toString(), first_name, last_name);
    }
}
