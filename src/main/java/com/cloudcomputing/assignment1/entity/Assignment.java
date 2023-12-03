package com.cloudcomputing.assignment1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "assignmnet")
public class Assignment {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String name;
    private int points;
    private int num_of_attemps;
    private Timestamp deadline;
    private  Timestamp assignment_created;
    private Timestamp assignment_updated;
    private String createdBy;
    private UUID submissionId;
    private String submission_url;
    private Timestamp submission_date;
    private Timestamp submission_updated;

//     No-arg constructor
    public Assignment() {
        this.id = UUID.randomUUID();  // Assign a unique UUID when a new object is created
    }
    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", num_of_attemps=" + num_of_attemps +
                ", deadline=" + deadline +
                ", assignment_created=" + assignment_created +
                ", assignment_updated=" + assignment_updated +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
