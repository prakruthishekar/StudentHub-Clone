package com.cloudcomputing.assignment1.repository;

import com.cloudcomputing.assignment1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findAll();
    
}
