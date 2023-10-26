package com.cloudcomputing.assignment1.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAssignmentResponseDto {
    private List<AssignmentResponseDto> ass;
}
