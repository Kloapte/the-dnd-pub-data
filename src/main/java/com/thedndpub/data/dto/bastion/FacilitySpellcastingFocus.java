package com.thedndpub.data.dto.bastion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FacilitySpellcastingFocusRequirement.class, name = "requirement"),
        @JsonSubTypes.Type(value = FacilitySpellcastingFocusOptions.class, name = "options")
})
public sealed interface FacilitySpellcastingFocus permits FacilitySpellcastingFocusRequirement,
        FacilitySpellcastingFocusOptions {
}
