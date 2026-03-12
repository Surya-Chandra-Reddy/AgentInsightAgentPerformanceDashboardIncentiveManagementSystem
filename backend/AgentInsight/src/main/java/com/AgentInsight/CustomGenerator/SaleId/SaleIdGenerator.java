package com.AgentInsight.CustomGenerator.SaleId;


import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;

public class SaleIdGenerator implements BeforeExecutionGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        // Use the actual class name from the owner object
        String entityName = owner.getClass().getName();

        // Query for the max saleid
        String query = "select max(s.saleid) from " + entityName + " s";

        String maxId = session.createSelectionQuery(query, String.class).getSingleResult();

        long nextNumber = 1;
        if (maxId != null) {
            // Extract numeric part from "S-001"
            String numericPart = maxId.substring(2);
            nextNumber = Long.parseLong(numericPart) + 1;
        }

        return "S-" + String.format("%03d", nextNumber);
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}
