package com.thedndpub.data.dto.bastion;

import java.util.List;

public record FacilityPrerequisite(
        List<String> membership,
        FacilitySpellcastingFocus spellcastingFocus,
        FacilityOtherSummary otherSummary,
        List<FacilityExpertise> expertise,
        List<FacilityProficiency> proficiency) {
    public FacilityPrerequisite {
        membership = copy(membership);
        expertise = copy(expertise);
        proficiency = copy(proficiency);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
