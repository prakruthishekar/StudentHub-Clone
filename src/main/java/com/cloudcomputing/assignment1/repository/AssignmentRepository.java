package com.cloudcomputing.assignment1.repository;

import com.cloudcomputing.assignment1.entity.Assignment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findAll();
    List<Assignment> findAllByCreatedBy(String createdBy);

    Optional<Assignment> findById(UUID id);

    boolean existsById(UUID id);

    @Transactional
    void removeById(UUID id);
}


