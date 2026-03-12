package com.AgentInsight.CustomGenerator.IncentiveId;


import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;

public class IncentiveIdGenerator implements BeforeExecutionGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        String entityName = owner.getClass().getName();

        // Query for the max incentiveid
        String query = "select max(i.incentiveid) from " + entityName + " i";

        String maxId = session.createSelectionQuery(query, String.class).getSingleResult();

        long nextNumber = 1;
        if (maxId != null) {
            // Extract numeric part from "I-001"
            String numericPart = maxId.substring(2);
            nextNumber = Long.parseLong(numericPart) + 1;
        }

        return "I-" + String.format("%03d", nextNumber);
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}
