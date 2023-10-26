package com.cloudcomputing.assignment1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthChecker {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public boolean isDatabaseHealthy() {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return true;
    }
}