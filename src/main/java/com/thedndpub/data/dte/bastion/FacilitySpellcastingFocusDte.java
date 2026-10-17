package com.thedndpub.data.dte.bastion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FacilitySpellcastingFocusDeserializer.class)
public sealed interface FacilitySpellcastingFocusDte permits FacilitySpellcastingFocusFlagDte,
        FacilitySpellcastingFocusTypesDte {
}
