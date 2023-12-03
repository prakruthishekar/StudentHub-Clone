package com.cloudcomputing.assignment1.service;

import com.cloudcomputing.assignment1.payload.AssignmentDto;
import com.cloudcomputing.assignment1.payload.AssignmentResponseDto;
import com.cloudcomputing.assignment1.payload.GetAssignmentResponseDto;
import com.cloudcomputing.assignment1.payload.SubmissionDto;
import com.cloudcomputing.assignment1.payload.SubmissionRequestDto;

import java.util.List;
import java.util.UUID;

public interface AssignmentService {

    AssignmentResponseDto createAssignment(AssignmentDto assignmentDto);

    GetAssignmentResponseDto getAllAssignments(String userName);

    List<AssignmentResponseDto> getAssignmentById(UUID id);

    boolean deleteAssignmentById(UUID id);

    boolean updateAssignmentbyId(UUID id, AssignmentDto assignmentDto);

    boolean isAuthorized(UUID id, String userName);
    
    boolean isAssignmentPresent(UUID id);

    boolean submissionCondition(UUID id);

    SubmissionDto submitAssignment(UUID id, SubmissionRequestDto submissionRequestDto);




}