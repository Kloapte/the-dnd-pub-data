package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemProtectionDto(
        List<String> resistance,
        List<String> immunity,
        List<String> vulnerability,
        List<String> conditionImmunity,
        List<String> poisonTypes) {
    public ItemProtectionDto {
        resistance = List.copyOf(resistance);
        immunity = List.copyOf(immunity);
        vulnerability = List.copyOf(vulnerability);
        conditionImmunity = List.copyOf(conditionImmunity);
        poisonTypes = List.copyOf(poisonTypes);
    }
}
