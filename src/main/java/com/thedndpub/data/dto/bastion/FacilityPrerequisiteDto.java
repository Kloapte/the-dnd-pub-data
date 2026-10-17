package com.thedndpub.data.dto.bastion;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FacilityPrerequisiteDto(
        List<String> membership,
        FacilitySpellcastingFocus spellcastingFocus,
        List<FacilityExpertise> expertise,
        List<FacilityProficiency> proficiency) {
    public FacilityPrerequisiteDto {
        membership = copy(membership);
        expertise = copy(expertise);
        proficiency = copy(proficiency);
    }

    public static FacilityPrerequisiteDto from(FacilityPrerequisite source) {
        return new FacilityPrerequisiteDto(source.membership(), source.spellcastingFocus(),
                source.expertise(), source.proficiency());
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
