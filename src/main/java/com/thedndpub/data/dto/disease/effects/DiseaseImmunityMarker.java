package com.thedndpub.data.dto.disease.effects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.thedndpub.data.dto.disease.enums.DiseaseImmunityMarkerKind;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DamageTypeImmunityMarker.class, name = "DAMAGE_TYPE"),
        @JsonSubTypes.Type(value = ConditionImmunityMarker.class, name = "CONDITION")
})
public sealed interface DiseaseImmunityMarker permits DamageTypeImmunityMarker, ConditionImmunityMarker {
    DiseaseImmunityMarkerKind kind();
}
