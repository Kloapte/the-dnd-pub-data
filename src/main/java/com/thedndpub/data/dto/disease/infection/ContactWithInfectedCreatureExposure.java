package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ContactWithInfectedCreatureExposure(
        String kind,
        String contact,
        String sourceState,
        DiseaseSavingThrow savingThrow,
        String onFailure,
        List<String> eligibleCreatureTypes,
        DiseaseImmunity onSuccessImmunity,
        String site) implements DiseaseExposure {
    public ContactWithInfectedCreatureExposure {
        eligibleCreatureTypes = eligibleCreatureTypes == null ? List.of() : List.copyOf(eligibleCreatureTypes);
    }
}
