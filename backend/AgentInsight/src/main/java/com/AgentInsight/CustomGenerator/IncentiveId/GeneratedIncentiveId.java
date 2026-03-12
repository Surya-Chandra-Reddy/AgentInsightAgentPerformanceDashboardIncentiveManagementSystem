package com.AgentInsight.CustomGenerator.IncentiveId;


import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.*;

@IdGeneratorType(com.AgentInsight.CustomGenerator.IncentiveId.IncentiveIdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GeneratedIncentiveId {
}
