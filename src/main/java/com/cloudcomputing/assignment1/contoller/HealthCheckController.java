package com.cloudcomputing.assignment1.contoller;

import com.cloudcomputing.assignment1.payload.HealthCheckResponse;
import com.cloudcomputing.assignment1.service.DatabaseHealthChecker;
import com.cloudcomputing.assignment1.service.MetricServices;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class HealthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @Autowired
    private DatabaseHealthChecker databaseHealthChecker;

    @Autowired
    private MetricServices metricServices;

    @GetMapping("/healthz")
    public ResponseEntity<Object> checkDatabaseHealth(@RequestBody(required = false) String requestBody, HttpServletRequest request) {

        metricServices.incrementCounter("healthz.get.request");
        if (requestBody != null) {
            // Return a 400 Bad Request response if there is a request body
            logger.error("GET:/healthz : BAD_REQUEST", (Throwable)null);
            metricServices.incrementCounter("healthz.get.bad_request");
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache()).build();
        }
        // Check for query parameters
        if (!request.getParameterMap().isEmpty()) {
            metricServices.incrementCounter("healthz.get.bad_request");
            logger.error("GET:/healthz : BAD_REQUEST", (Throwable)null);
            return ResponseEntity.badRequest()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }
        HealthCheckResponse response = new HealthCheckResponse();
        try{
            databaseHealthChecker.isDatabaseHealthy();
            logger.info("GET:/healthz : SUCCESS");
            response.setStatus("Database is healthy");
            System.out.println("Database is healthy");
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache()).build();
        } catch (Exception e) {
            response.setStatus("Database is not healthy");
            logger.error("GET:/healthz : SERVICE_UNAVAILABLE", (Throwable)null);
            metricServices.incrementCounter("healthz.get.service_unavailable");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .cacheControl(CacheControl.noCache()).build();
        }
    }
}