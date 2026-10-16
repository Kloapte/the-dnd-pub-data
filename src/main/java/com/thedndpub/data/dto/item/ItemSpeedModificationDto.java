package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemSpeedModificationDto(
        Map<String, String> equal,
        Map<String, ItemNumberTextDto> multiply,
        Map<String, ItemNumberTextDto> staticValues,
        Map<String, ItemNumberTextDto> bonus) {
    public ItemSpeedModificationDto {
        equal = Map.copyOf(equal);
        multiply = Map.copyOf(multiply);
        staticValues = Map.copyOf(staticValues);
        bonus = Map.copyOf(bonus);
    }
}
