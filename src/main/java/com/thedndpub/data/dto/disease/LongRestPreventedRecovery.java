package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LongRestPreventedRecovery(
        String kind,
        DiseaseReference condition,
        Integer minimumLevel,
        List<String> preventedRecoveries) implements DiseaseConditionGatedRestriction {
    public LongRestPreventedRecovery {
        preventedRecoveries = preventedRecoveries == null ? List.of() : List.copyOf(preventedRecoveries);
    }
}
