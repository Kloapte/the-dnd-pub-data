package com.thedndpub.data.dto.magicvariant;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MagicVariantRequirementDto(
        String type,
        String source,
        List<String> names,
        List<String> property,
        String weaponCategory,
        String damageType,
        String spellcastingFocusType,
        Boolean weapon,
        Boolean armor,
        Boolean arrow,
        Boolean axe,
        Boolean bolt,
        Boolean bow,
        Boolean crossbow,
        Boolean net,
        Boolean polearm,
        Boolean spear,
        Boolean sword,
        Boolean bulletFirearm,
        Boolean cellEnergy) {
    public MagicVariantRequirementDto {
        names = List.copyOf(names);
        property = List.copyOf(property);
    }
}
