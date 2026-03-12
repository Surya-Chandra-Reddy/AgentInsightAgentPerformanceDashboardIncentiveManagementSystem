package com.AgentInsight.CustomGenerator.PolicyId;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class GeneratedPolicyId implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        // Query the database to get the max policy ID
        String maxId = (String) session.createQuery(
                "SELECT MAX(p.policyId) FROM Policy p"
        ).uniqueResult();

        int nextNumber = 1;
        if (maxId != null && maxId.startsWith("POL-")) {
            try {
                int currentNumber = Integer.parseInt(maxId.substring(4));
                nextNumber = currentNumber + 1;
            } catch (NumberFormatException e) {
                nextNumber = 1;
            }
        }

        return String.format("POL-%03d", nextNumber);
    }
}
