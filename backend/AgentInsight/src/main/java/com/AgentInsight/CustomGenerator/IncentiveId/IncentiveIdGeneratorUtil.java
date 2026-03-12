package com.AgentInsight.CustomGenerator.IncentiveId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class IncentiveIdGeneratorUtil {

    @PersistenceContext
    private EntityManager entityManager;

    public String generateId() {
        String maxId = entityManager.createQuery(
                "select max(i.incentiveid) from Incentive i", String.class
        ).getSingleResult();

        long nextNumber = 1;
        if (maxId != null) {
            String numericPart = maxId.substring(2);
            nextNumber = Long.parseLong(numericPart) + 1;
        }
        return "I-" + String.format("%03d", nextNumber);
    }
}

