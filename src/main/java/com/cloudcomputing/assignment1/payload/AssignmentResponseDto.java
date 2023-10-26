package com.cloudcomputing.assignment1.payload;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class AssignmentResponseDto {
    private UUID id;
    private String name;
    private int points;
    private int num_of_attemps;
    private Timestamp deadline;
    private Timestamp assignment_created;
    private Timestamp assignment_updated;
}
