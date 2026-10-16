package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemSpellGrantDto(List<String> direct, Map<String, List<String>> byUses) {
    public ItemSpellGrantDto {
        direct = List.copyOf(direct);
        byUses = byUses.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
    }
}
