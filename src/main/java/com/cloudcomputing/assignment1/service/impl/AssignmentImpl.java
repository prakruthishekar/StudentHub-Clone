package com.cloudcomputing.assignment1.service.impl;

import com.cloudcomputing.assignment1.entity.Assignment;
import com.cloudcomputing.assignment1.payload.AssignmentDto;
import com.cloudcomputing.assignment1.payload.AssignmentResponseDto;
import com.cloudcomputing.assignment1.payload.GetAssignmentResponseDto;
import com.cloudcomputing.assignment1.repository.AssignmentRepository;
import com.cloudcomputing.assignment1.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class AssignmentImpl implements AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Override
    public AssignmentResponseDto createAssignment(AssignmentDto assignmentDto) {
        Assignment assignment = mapToEntity(assignmentDto);
        Assignment newAssignmnet = assignmentRepository.save(assignment);
        System.out.println("Saved: "+newAssignmnet);
        // convert entity to DTO
        AssignmentResponseDto assignmentResponse = mapToDTO(newAssignmnet);
        return assignmentResponse;
    }

    @Override
    public GetAssignmentResponseDto getAllAssignments(String userName) {
        GetAssignmentResponseDto getAssignmentResponseDto = new GetAssignmentResponseDto();
        List<Assignment> assignmentList = assignmentRepository.findAllByCreatedBy(userName);

        GetAssignmentResponseDto gato = new GetAssignmentResponseDto();
        List<AssignmentResponseDto> assDTO = new ArrayList<>();
        for(Assignment assignment : assignmentList){
            assDTO.add(mapToDTO(assignment));
        }
        gato.setAss(assDTO);
        return gato;
    }

    @Override
    public List<AssignmentResponseDto> getAssignmentById(UUID id) {
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        ArrayList assignmentById = new ArrayList();
        if (assignment != null) {
            assignmentById.add(mapToDTO(assignment));
        }
        else {
            // Handle the case where no assignment was found with the given id
            // You can return an empty list or throw an exception, depending on your requirements
            return Collections.emptyList(); // Return an empty list as an example
        }
        return assignmentById;
    }

    @Override
    public boolean deleteAssignmentById(UUID id) {
        try {
            Assignment assignment = assignmentRepository.findById(id).orElseThrow(NoSuchElementException::new);
            // Delete the assignment or perform other actions if necessary
            if (assignmentRepository.existsById(id)) {
                assignmentRepository.removeById(id);
            } else {
                throw new NoSuchElementException("Assignment not found");
            }
            return true;
        } catch (NoSuchElementException e) {
            // Assignment not found, return false or handle the error accordingly
            return false;
        }
    }

    @Override
    public boolean updateAssignmentbyId(UUID id, AssignmentDto assignmentDto) {

        try {
            Assignment assignment = assignmentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Assignment not found with id: " + id));
            // Update assignment properties
            assignment.setName(assignmentDto.getName());
            assignment.setPoints(assignmentDto.getPoints());
            assignment.setNum_of_attemps(assignmentDto.getNum_of_attemps());
            assignment.setDeadline(assignmentDto.getDeadline());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            assignment.setAssignment_updated(timestamp);

            // Save the updated assignment
            assignmentRepository.save(assignment);

            return true; // Return true if the update is successful
        } catch (IllegalArgumentException ex) {
            // Handle the exception and return false
            return false;
        }
    }

    // convert Entity into DTO
    private AssignmentResponseDto mapToDTO(Assignment assignment){
        AssignmentResponseDto assignmentResponseDto = new AssignmentResponseDto();
        assignmentResponseDto.setId(assignment.getId());
        assignmentResponseDto.setName(assignment.getName());
        assignmentResponseDto.setPoints(assignment.getPoints());
        assignmentResponseDto.setDeadline(assignment.getDeadline());
        assignmentResponseDto.setNum_of_attemps(assignment.getNum_of_attemps());
        assignmentResponseDto.setAssignment_created(assignment.getAssignment_created());
        assignmentResponseDto.setAssignment_updated(assignment.getAssignment_updated());
        return assignmentResponseDto;
    }

    // convert DTO to entity
    private Assignment mapToEntity(AssignmentDto assignmentDto){
        Assignment assignment = new Assignment();
        assignment.setName(assignmentDto.getName());
        assignment.setPoints(assignmentDto.getPoints());
        assignment.setNum_of_attemps(assignmentDto.getNum_of_attemps());
        assignment.setDeadline(assignmentDto.getDeadline());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        assignment.setAssignment_created(timestamp);
        assignment.setAssignment_updated(timestamp);
        assignment.setCreatedBy(assignmentDto.getCreatedBy());
        return assignment;
    }

    public boolean isAuthorized(UUID id, String useName){

        Optional<Assignment> assignment = assignmentRepository.findById(id);
        System.out.println(assignment);
        if(assignment.isPresent()){
            System.out.println("Assignment Is Present");
            System.out.println(useName);
            System.out.println(assignment.get().getCreatedBy());
            if(assignment.get().getCreatedBy().equals(useName)){
                System.out.println("Returned true");
                return  true;
            }
        }
        return false;
    }
}
