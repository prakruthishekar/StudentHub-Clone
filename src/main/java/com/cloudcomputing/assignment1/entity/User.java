package com.cloudcomputing.assignment1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Column(name = "account_created", nullable = false)
    @CreationTimestamp // Automatically set the creation timestamp
    private LocalDateTime accountCreated;

    @Column(name = "account_updated", nullable = false)
    @UpdateTimestamp // Automatically update the modification timestamp
    private LocalDateTime accountUpdated;
    // Getters and setters
    public User(String firstName,  String lastName, String email, String password) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
    }
}

