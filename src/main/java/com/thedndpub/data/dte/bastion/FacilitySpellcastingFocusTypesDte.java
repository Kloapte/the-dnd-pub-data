package com.thedndpub.data.dte.bastion;

import java.util.List;

public record FacilitySpellcastingFocusTypesDte(List<String> types) implements FacilitySpellcastingFocusDte {
    public FacilitySpellcastingFocusTypesDte {
        types = types == null ? List.of() : List.copyOf(types);
    }
}
