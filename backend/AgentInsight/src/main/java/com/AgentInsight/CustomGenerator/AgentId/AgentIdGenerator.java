package com.AgentInsight.CustomGenerator.AgentId;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;

public class AgentIdGenerator implements BeforeExecutionGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        // Use the actual class name from the owner object
        String entityName = owner.getClass().getName();

        // FIXED: Use 'agentid' instead of 'id' because your entity uses 'agentid'
        String query = "select max(a.agentid) from " + entityName + " a";

        String maxId = session.createSelectionQuery(query, String.class).getSingleResult();

        long nextNumber = 1;
        if (maxId != null) {
            // Extracts the numeric part from "A-001"
            String numericPart = maxId.substring(2);
            nextNumber = Long.parseLong(numericPart) + 1;
        }

        return "A-" + String.format("%03d", nextNumber);
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}