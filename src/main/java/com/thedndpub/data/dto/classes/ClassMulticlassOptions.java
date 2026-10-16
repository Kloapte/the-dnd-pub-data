package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassMulticlassOptions(
        ClassAbilityRequirement requirements,
        ClassProficiencyOptions proficienciesGained) {
}
