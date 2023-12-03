package com.cloudcomputing.assignment1.payload;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class SubmissionDto {
    private UUID id;
    private UUID assignment_id;
    private String submission_url;
    private Timestamp submission_date;
    private Timestamp submission_updated;
}
