package com.cloudcomputing.assignment1.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AssignmentDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Points is required")
    @Min(value = 0, message = "Number of Attempts cannot be negative")
    @PositiveOrZero(message = "Points must be a positive or zero value")
    @Max(value = 100, message = "Number of points cannot be more than 100")
    private Integer points;

    @NotNull(message = "Number of attempts is required")
    @Min(value = 0, message = "Number of Attempts cannot be negative")
    @PositiveOrZero(message = "Number of attempts must be a positive or zero value")
    @Max(value = 100, message = "Number of attempts cannot be more than 100")
    private Integer num_of_attemps;

    @NotNull(message = "Deadline is required")
    private Timestamp deadline;

    private String createdBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
