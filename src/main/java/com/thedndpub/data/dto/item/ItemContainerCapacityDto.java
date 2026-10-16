package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemContainerCapacityDto(
        List<Double> weight,
        Boolean weightless,
        List<Double> volume,
        List<Map<String, Integer>> item) {
    public ItemContainerCapacityDto {
        weight = weight == null ? List.of() : List.copyOf(weight);
        volume = volume == null ? List.of() : List.copyOf(volume);
        item = item == null ? List.of() : item.stream().map(Map::copyOf).toList();
    }
}
