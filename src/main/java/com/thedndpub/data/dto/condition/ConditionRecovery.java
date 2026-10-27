package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionRecoveryMethod;
import com.thedndpub.data.dto.condition.enums.ConditionRecoveryRequirement;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionRecovery(
        ConditionRecoveryMethod method,
        Integer levelsRemoved,
        List<ConditionRecoveryRequirement> requirements) {
    public ConditionRecovery {
        requirements = requirements == null ? List.of() : List.copyOf(requirements);
    }
}
