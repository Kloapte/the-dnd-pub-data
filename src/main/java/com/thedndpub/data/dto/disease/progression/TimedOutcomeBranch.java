package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TimedOutcomeBranch(
        String kind,
        String branchId,
        DiseaseProgressionTrigger trigger,
        DiseaseInterval interval,
        List<DiseaseProgressionOutcome> outcomes) implements DiseaseProgression {
    public TimedOutcomeBranch {
        outcomes = outcomes == null ? List.of() : List.copyOf(outcomes);
    }
}
