package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionReferenceType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionReference(
        ConditionReferenceType targetType,
        String id,
        String relationship,
        String operation) {
}
