package com.thedndpub.data.dto.disease.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.enums.DiseaseImmunityMarkerKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageTypeImmunityMarker(DiseaseImmunityMarkerKind kind, String damageType)
        implements DiseaseImmunityMarker {
}
