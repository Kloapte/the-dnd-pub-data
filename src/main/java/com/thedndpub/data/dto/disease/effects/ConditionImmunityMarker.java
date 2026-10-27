package com.thedndpub.data.dto.disease.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;
import com.thedndpub.data.dto.disease.enums.DiseaseImmunityMarkerKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionImmunityMarker(DiseaseImmunityMarkerKind kind, DiseaseReference condition)
        implements DiseaseImmunityMarker {
}
