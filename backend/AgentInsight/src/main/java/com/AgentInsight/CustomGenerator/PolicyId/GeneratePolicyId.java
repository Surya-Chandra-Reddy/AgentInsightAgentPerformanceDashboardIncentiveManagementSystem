package com.AgentInsight.CustomGenerator.PolicyId;

import org.hibernate.annotations.IdGeneratorType;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(GeneratedPolicyId.class)
public @interface GeneratePolicyId {
}
