package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.infection.DiseaseImmunity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseOutcomeCounter(
        String branchId,
        String countedOutcome,
        Integer threshold,
        String sequence,
        String reset,
        DiseaseProgressionOutcome onThreshold,
        DiseaseImmunity immunityAfterRecovery) {
}
