package com.thedndpub.data.dto.bastion;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FacilitySpellcastingFocusOptions(List<String> types) implements FacilitySpellcastingFocus {
    public FacilitySpellcastingFocusOptions {
        types = types == null ? List.of() : List.copyOf(types);
    }
}
