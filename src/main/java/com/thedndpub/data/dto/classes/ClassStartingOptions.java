package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassStartingOptions(
        Boolean additionalFromBackground,
        ClassProficiencyOptions proficiencies,
        List<ClassEquipmentGroup> equipment) {
    public ClassStartingOptions {
        equipment = equipment == null ? List.of() : List.copyOf(equipment);
    }
}
