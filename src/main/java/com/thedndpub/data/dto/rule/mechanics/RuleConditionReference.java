package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dto.rule.enums.RuleReferenceType;
import com.thedndpub.data.dto.rule.enums.RuleReferenceOperation;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleConditionReference(@JsonProperty("targetType") RuleReferenceType targetType, String id,
        String relationship, RuleReferenceOperation operation) {
    public RuleConditionReference {
        if (targetType != RuleReferenceType.CONDITION) {
            throw new IllegalArgumentException("Condition references require targetType condition");
        }
        new com.thedndpub.data.dto.rule.RuleReference(targetType, id, relationship, operation);
    }
}
