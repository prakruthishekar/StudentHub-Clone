package com.cloudcomputing.assignment1.contoller;

import com.cloudcomputing.assignment1.payload.AssignmentDto;
import com.cloudcomputing.assignment1.payload.AssignmentResponseDto;
import com.cloudcomputing.assignment1.service.AssignmentService;
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
@RequestMapping("/v1/assignments")
@Validated
public class AssignmentController {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<AssignmentResponseDto> createAssignment(
            @Valid @RequestBody AssignmentDto assignmentDto,
            @RequestHeader("Authorization") String headerValue,
            HttpServletRequest request){

        // Check for query parameters
        if (!request.getParameterMap().isEmpty()) {
            logger.error("POST:/v1/assignments : BAD REQUEST", (Throwable)null);
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if(userService.isUserAuthenticated(headerValue)){
            String username = Util.getUserName(headerValue);
            assignmentDto.setCreatedBy(username);
            System.out.println(assignmentDto);
        }else {
            logger.error("POST:/v1/assignments,UNAUTHORIZED", (Throwable)null);
            return  new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(assignmentService.createAssignment(assignmentDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AssignmentResponseDto>> getAllAssignments(
            @RequestHeader("Authorization") String headerValue,
            @RequestBody(required = false) String requestBody,
            HttpServletRequest request) {

        if (requestBody != null) {
            // Return a 400 Bad Request response if there is a request body
            logger.error("GET:/v1/assignments, BAD REQUEST", (Throwable)null);
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache()).build();
        }
        // Check for query parameters
        if (!request.getParameterMap().isEmpty()) {
            logger.error("GET:/v1/assignments, BAD REQUEST", (Throwable)null);
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if (userService.isUserAuthenticated(headerValue)) {
            String username = Util.getUserName(headerValue);
            List<AssignmentResponseDto> assignments = assignmentService.getAllAssignments(username).getAss();
            logger.info("GET:/v1/assignments, Assignmnet fetched");
            return ResponseEntity.ok(assignments);
        } else {
            // If the user is not authenticated, return a 401 Unauthorized status code
            logger.error("GET:/v1/assignments, UNAUTHORIZED", (Throwable)null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<AssignmentResponseDto>> getAssignmentById(
            @PathVariable(name = "id") UUID id,
            @RequestHeader("Authorization") String headerValue,
            @RequestBody(required = false) String requestBody,
            HttpServletRequest request) {

        if (requestBody != null) {
            // Return a 400 Bad Request response if there is a request body
            logger.error("GET:/v1/assignments/{id}, BAD REQUEST", (Throwable)null);
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache()).build();
        }

        // Check for query parameters
        if (!request.getParameterMap().isEmpty()) {
            logger.error("GET:/v1/assignments/{id}, BAD REQUEST", (Throwable)null);
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        String username = Util.getUserName(headerValue);

        if (userService.isUserAuthenticated(headerValue)) {
            if (assignmentService.isAuthorized(id, username)) {
                // Check if the assignment with the given id exists
                List<AssignmentResponseDto> assignment = assignmentService.getAssignmentById(id);

                if (!assignment.isEmpty()) {
                    // Assignment exists, return its details
                    logger.info("GET:/v1/assignments/{id}, Assignmnet details fetched");
                    return ResponseEntity.ok(assignment);
                } else {
                    // Assignment not found, return a 404 Not Found response
                    logger.error("GET:/v1/assignments/{id}, NOT FOUND", (Throwable)null);
                    return ResponseEntity.notFound().build();
                }
            } else {
                logger.error("GET:/v1/assignments/{id}, FORBIDDEN", (Throwable)null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .cacheControl(CacheControl.noCache())
                        .build();
            }
        } else {
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

        if (!userService.isUserAuthenticated(headerValue)) {
            logger.error("PUT:/v1/assignments/{id}, UNAUTHORIZED", (Throwable)null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if (!assignmentService.isAuthorized(id, username)) {
             logger.error("PUT:/v1/assignments/{id}, FORBIDDEN", (Throwable)null);
             return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .cacheControl(CacheControl.noCache())
                        .build();
        }

        assignmentDto.setCreatedBy(username);

        if (assignmentService.updateAssignmentbyId(id, assignmentDto)) {
            logger.info("PUT:/v1/assignments/{id}, Assignmen updated");
            return ResponseEntity.noContent().cacheControl(CacheControl.noCache()).build();
        }
        logger.error("PUT:/v1/assignments/{id}, NOT FOUND", (Throwable)null);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAssignment(
        @PathVariable(name = "id") UUID id,
        @RequestHeader("Authorization") String headerValue,
        @RequestBody(required = false) String requestBody ) {

        if (requestBody != null) {
            // Return a 400 Bad Request response if there is a request body
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache()).build();
        }

        String username = Util.getUserName(headerValue);

        if (!userService.isUserAuthenticated(headerValue)) {
            logger.error("DELETE:/v1/assignments/{id}, UNAUTHORIZED", (Throwable)null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        if (assignmentService.isAssignmentPresent(id)){

            if (!assignmentService.isAuthorized(id, username)) {
             logger.error("DELETE:/v1/assignments/{id}, FORBIDDEN", (Throwable)null);
             return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .cacheControl(CacheControl.noCache())
                        .build();
             }
            
        }
        else {
            return ResponseEntity.notFound().build();
        }

        assignmentService.deleteAssignmentById(id);
        return ResponseEntity.noContent()
                            .cacheControl(CacheControl.noCache())
                            .build();
        
    }
}
