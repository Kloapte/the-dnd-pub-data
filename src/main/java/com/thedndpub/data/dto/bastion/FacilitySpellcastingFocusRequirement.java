package com.thedndpub.data.dto.bastion;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FacilitySpellcastingFocusRequirement(boolean required) implements FacilitySpellcastingFocus {
}
