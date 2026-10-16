package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemAttunementTagDto(
        String className,
        String background,
        Boolean spellcasting,
        String skillProficiency,
        List<String> alignment,
        String creatureType,
        String race,
        Boolean psionics,
        String languageProficiency,
        String size,
        Integer intelligenceRequirement) {
    public ItemAttunementTagDto {
        alignment = List.copyOf(alignment);
    }
}
