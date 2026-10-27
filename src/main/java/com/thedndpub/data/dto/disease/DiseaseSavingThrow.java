package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseSavingThrow(
        String ability,
        Double dc,
        DiseaseDcReduction dcReduction,
        String frequency,
        String dcTrackId,
        Boolean advantageForNaturalArmor,
        String dcVariation) {
}
