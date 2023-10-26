package com.cloudcomputing.assignment1.IntegrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(properties = {"spring.datasource.url=jdbc:mysql://localhost:3306/myblog"})
public class HealthCheckControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCheckDatabaseHealth() throws Exception {
        // Test without a request body
        mockMvc.perform(MockMvcRequestBuilders.get("/healthz")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Test with a request (should return 400 Bad Request)
        mockMvc.perform(MockMvcRequestBuilders.get("/healthz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Invalid Request Body")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

