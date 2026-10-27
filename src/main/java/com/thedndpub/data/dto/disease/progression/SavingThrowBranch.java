package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SavingThrowBranch(
        String kind,
        String branchId,
        DiseaseProgressionTrigger trigger,
        DiseaseSavingThrow savingThrow,
        List<DiseaseProgressionOutcome> onSuccess,
        List<DiseaseProgressionOutcome> onFailure,
        DiseaseConditionThreshold endsWhen,
        DiseaseOutcomeCounter outcomeCounter) implements DiseaseProgression {
    public SavingThrowBranch {
        onSuccess = onSuccess == null ? List.of() : List.copyOf(onSuccess);
        onFailure = onFailure == null ? List.of() : List.copyOf(onFailure);
    }
}
