package com.cloudcomputing.assignment1.contoller;

import com.cloudcomputing.assignment1.entity.User;
import com.cloudcomputing.assignment1.payload.AssignmentDto;
import com.cloudcomputing.assignment1.payload.AssignmentResponseDto;
import com.cloudcomputing.assignment1.payload.SubmissionDto;
import com.cloudcomputing.assignment1.payload.SubmissionRequestDto;
import com.cloudcomputing.assignment1.repository.UserRepository;
import com.cloudcomputing.assignment1.service.AssignmentService;
import com.cloudcomputing.assignment1.service.MetricServices;
import com.cloudcomputing.assignment1.service.SnsService;
import com.cloudcomputing.assignment1.service.UserService;
import com.cloudcomputing.assignment1.util.Util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
// import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/v3/demo/assignments")
@Validated
public class AssignmentController {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private MetricServices metricServices;

    @Autowired
    private SnsService snsService;

    @PostMapping
    public ResponseEntity<AssignmentResponseDto> createAssignment(
            @Valid @RequestBody AssignmentDto assignmentDto,
            @RequestHeader("Authorization") String headerValue,
            HttpServletRequest request){
        
        metricServices.incrementCounter("createAssignment.post.request");
        // Check for query parameters
        if (!request.getParameterMap().isEmpty()) {
            logger.error("POST:/v1/assignments : BAD REQUEST", (Throwable)null);
            metricServices.incrementCounter("createAssignment.post.bad_request");
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if(userService.isUserAuthenticated(headerValue)){
            String username = Util.getUserName(headerValue);
            assignmentDto.setCreatedBy(username);
            logger.info("POST:/v1/assignments, Assignmnet Created");
            metricServices.incrementCounter("createAssignment.post.success"); 
            System.out.println(assignmentDto);
        }else {
            logger.error("POST:/v1/assignments,UNAUTHORIZED", (Throwable)null);
            metricServices.incrementCounter("createAssignment.post.unauthorized");
            return  new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(assignmentService.createAssignment(assignmentDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AssignmentResponseDto>> getAllAssignments(
            @RequestHeader("Authorization") String headerValue,
            @RequestBody(required = false) String requestBody,
            HttpServletRequest request) {
        
        metricServices.incrementCounter("getAllAssignments.get.request");
        
        if (requestBody != null) {
            // Return a 400 Bad Request response if there is a request body
            logger.error("GET:/v1/assignments, BAD REQUEST", (Throwable)null);
            metricServices.incrementCounter("getAllAssignments.get.bad_request");
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache()).build();
        }
        // Check for query parameters
        if (!request.getParameterMap().isEmpty()) {
            logger.error("GET:/v1/assignments, BAD REQUEST", (Throwable)null);
            metricServices.incrementCounter("getAllAssignments.get.query_bad_request");
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if (userService.isUserAuthenticated(headerValue)) {
            String username = Util.getUserName(headerValue);
            List<AssignmentResponseDto> assignments = assignmentService.getAllAssignments(username).getAss();
            logger.info("GET:/v1/assignments, Assignmnet fetched");
            metricServices.incrementCounter("getAllAssignments.get.success");
            return ResponseEntity.ok(assignments);
        } else {
            // If the user is not authenticated, return a 401 Unauthorized status code
            logger.error("GET:/v1/assignments, UNAUTHORIZED", (Throwable)null);
            metricServices.incrementCounter("getAllAssignments.get.unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<AssignmentResponseDto>> getAssignmentById(
            @PathVariable(name = "id") UUID id,
            @RequestHeader("Authorization") String headerValue,
            @RequestBody(required = false) String requestBody,
            HttpServletRequest request) {
        
        metricServices.incrementCounter("getAssignmentById.get.request");
        
        if (requestBody != null) {
            // Return a 400 Bad Request response if there is a request body
            logger.error("GET:/v1/assignments/{id}, BAD REQUEST", (Throwable)null);
            metricServices.incrementCounter("getAssignmentById.get.query_bad_request");
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache()).build();
        }

        // Check for query parameters
        if (!request.getParameterMap().isEmpty()) {
            logger.error("GET:/v1/assignments/{id}, BAD REQUEST", (Throwable)null);
            metricServices.incrementCounter("getAssignmentById.get.query_bad_request");
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        String username = Util.getUserName(headerValue);

        if (userService.isUserAuthenticated(headerValue)) {
            // Check if the assignment with the given id exists
            List<AssignmentResponseDto> assignment = assignmentService.getAssignmentById(id);

            if (!assignment.isEmpty()) {

                if (assignmentService.isAuthorized(id, username)) {

                    // Assignment exists, return its details
                    metricServices.incrementCounter("getAssignmentById.get.success");
                    logger.info("GET:/v1/assignments/{id}, Assignmnet details fetched");
                    return ResponseEntity.ok(assignment);
                
                } else {
                    metricServices.incrementCounter("getAssignmentById.get.forbidden");
                    logger.error("GET:/v1/assignments/{id}, FORBIDDEN", (Throwable)null);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .cacheControl(CacheControl.noCache())
                            .build();
                }

            } 
            else {
                    // Assignment not found, return a 404 Not Found response
                    metricServices.incrementCounter("getAssignmentById.get.not_found");
                    logger.error("GET:/v1/assignments/{id}, NOT FOUND", (Throwable)null);
                    return ResponseEntity.notFound().build();
                }

        } else {
            metricServices.incrementCounter("getAssignmentById.get.unauthorized");
            logger.error("GET:/v1/assignments/{id}, UNAUTHORIZED", (Throwable)null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAssignmentById(
            @PathVariable(name = "id") UUID id,
            @Valid @RequestBody AssignmentDto assignmentDto,
            @RequestHeader("Authorization") String headerValue) {

        String username = Util.getUserName(headerValue);

        metricServices.incrementCounter("updateAssignmentById.put.request");

        if (!userService.isUserAuthenticated(headerValue)) {
            logger.error("PUT:/v1/assignments/{id}, UNAUTHORIZED", (Throwable)null);
            metricServices.incrementCounter("updateAssignmentById.put.unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if (!assignmentService.isAuthorized(id, username)) {
             logger.error("PUT:/v1/assignments/{id}, FORBIDDEN", (Throwable)null);
             metricServices.incrementCounter("updateAssignmentById.put.forbidden");
             return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .cacheControl(CacheControl.noCache())
                        .build();
        }

        assignmentDto.setCreatedBy(username);

        if (assignmentService.updateAssignmentbyId(id, assignmentDto)) {
            logger.info("PUT:/v1/assignments/{id}, Assignmen updated");
            metricServices.incrementCounter("updateAssignmentById.put.success");
            return ResponseEntity.noContent().cacheControl(CacheControl.noCache()).build();
        }
        logger.error("PUT:/v1/assignments/{id}, NOT FOUND", (Throwable)null);
        metricServices.incrementCounter("updateAssignmentById.put.not_found");
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAssignment(
        @PathVariable(name = "id") UUID id,
        @RequestHeader("Authorization") String headerValue,
        @RequestBody(required = false) String requestBody ) {

        metricServices.incrementCounter("deleteAssignment.delete.request");

        if (requestBody != null) {
            // Return a 400 Bad Request response if there is a request body
            metricServices.incrementCounter("deleteAssignment.delete.bad_request");
            logger.error("DELETE:/v1/assignments/{id}, BAD_REQUEST", (Throwable)null);
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache()).build();
        }

        String username = Util.getUserName(headerValue);

        if (!userService.isUserAuthenticated(headerValue)) {
            logger.error("DELETE:/v1/assignments/{id}, UNAUTHORIZED", (Throwable)null);
            metricServices.incrementCounter("deleteAssignment.delete.unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if (assignmentService.isAssignmentPresent(id)){

            if (!assignmentService.isAuthorized(id, username)) {
             logger.error("DELETE:/v1/assignments/{id}, FORBIDDEN", (Throwable)null);
             metricServices.incrementCounter("deleteAssignment.delete.forbidden");
             return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .cacheControl(CacheControl.noCache())
                        .build();
             }
            
        }
        else {
            logger.error("DELETE:/v1/assignments/{id}, NOT_FOUND", (Throwable)null);
            metricServices.incrementCounter("deleteAssignment.delete.not_found");
            return ResponseEntity.notFound().build();
        }

        assignmentService.deleteAssignmentById(id);
        logger.info("DELETE:/v1/assignments/{id}, Assignmen deleted");
        metricServices.incrementCounter("deleteAssignment.delete.success");
        return ResponseEntity.noContent()
                            .cacheControl(CacheControl.noCache())
                            .build();
        
    }

    @PostMapping("/{id}/submission")
    public ResponseEntity<SubmissionDto> submitAssignment(
            @PathVariable(name = "id") UUID id,
            @Valid @RequestBody SubmissionRequestDto submissionRequestDto,
            @RequestHeader("Authorization") String headerValue,
            HttpServletRequest request){
        
        
        
        metricServices.incrementCounter("submitAssignment.post.request");

        // Check for query parameters
        if (!request.getParameterMap().isEmpty()) {
            logger.error("POST:/v1/assignments/{id}/submission : BAD REQUEST", (Throwable)null);
            metricServices.incrementCounter("submitAssignment.post.bad_request");
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }


        String username = Util.getUserName(headerValue);
        User user = userRepository.findByEmail(username);

        if (!userService.isUserAuthenticated(headerValue)) {
            logger.error("POST:/v1/assignments/{id}/submission, UNAUTHORIZED", (Throwable)null);
            metricServices.incrementCounter("submitAssignment.post.unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if (assignmentService.isAssignmentPresent(id)){

            if (!assignmentService.isAuthorized(id, username)) {
             logger.error("POST:/v1/assignments/{id}/submission, FORBIDDEN", (Throwable)null);
             metricServices.incrementCounter("submitAssignment.post.forbidden");
             return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .cacheControl(CacheControl.noCache())
                        .build();
             }
            
        }
        else {
            logger.error("POST:/v1/assignments/{id}/submission, NOT_FOUND", (Throwable)null);
            metricServices.incrementCounter("submitAssignment.post.not_found");
            return ResponseEntity.notFound().build();
        }

        if (assignmentService.submissionCondition(id)){
            System.out.println("INSSIDE SUBMISSION");
            SubmissionDto submissionDto = assignmentService.submitAssignment(id,submissionRequestDto );
            logger.info("POST:/v1/assignments/{id}/submission, Assignmen Submitted");
            snsService.publishToTopic(username, submissionDto.getSubmission_url(), submissionDto.getAssignment_id(), user.getFirstName(), user.getLastName());
            metricServices.incrementCounter("submitAssignment.post.success");
            return ResponseEntity.ok(submissionDto);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .cacheControl(CacheControl.noCache())
                        .build();
        }
        
    }

}
