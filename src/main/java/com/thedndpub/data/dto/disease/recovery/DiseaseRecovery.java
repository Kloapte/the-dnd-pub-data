package com.thedndpub.data.dto.disease.recovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;
import com.thedndpub.data.dto.disease.progression.DiseaseOutcomeCounter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseRecovery(
        String id,
        String method,
        DiseaseReference reference,
        String dcTrackId,
        DiseaseSavingThrow savingThrow,
        Double endsWhenDcAtOrBelow,
        Boolean immunityAfterRecovery,
        DiseaseOutcomeCounter outcomeCounter,
        Boolean endsDisease,
        List<DiseaseConditionLevelRemoval> removesConditionLevels,
        String ability,
        Double dc,
        String trigger,
        String performedBy,
        String outcome,
        String caregiverLongRestOutcome,
        String ends,
        String symptomId,
        DiseaseTreatment treatment) {
    public DiseaseRecovery {
        removesConditionLevels = removesConditionLevels == null ? List.of() : List.copyOf(removesConditionLevels);
    }
}
