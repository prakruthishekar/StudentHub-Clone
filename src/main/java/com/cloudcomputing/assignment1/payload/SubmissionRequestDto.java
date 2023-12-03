package com.cloudcomputing.assignment1.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmissionRequestDto {
    @NotBlank(message = "Name is required")
    private String submission_url;
}
