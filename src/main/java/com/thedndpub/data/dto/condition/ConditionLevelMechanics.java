package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionLevelApplication;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionLevelMechanics(
        Integer maximumLevel,
        Boolean cumulative,
        ConditionLevelApplication application,
        Integer endsAtLevel,
        List<ConditionRecovery> recovery) {

    public ConditionLevelMechanics {
        recovery = recovery == null ? List.of() : List.copyOf(recovery);
    }
}
