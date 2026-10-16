package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeatureSpellGrantDto(
        String ability,
        List<OptionalFeatureSpellChoiceDto> known,
        List<OptionalFeatureSpellChoiceDto> prepared,
        List<String> innateSpells,
        Map<String, List<String>> daily,
        Map<String, List<String>> resource,
        List<OptionalFeatureSpellRequirementDto> ritual,
        String resourceName) {
    public OptionalFeatureSpellGrantDto {
        known = List.copyOf(known);
        prepared = List.copyOf(prepared);
        innateSpells = List.copyOf(innateSpells);
        daily = immutableNestedMap(daily);
        resource = immutableNestedMap(resource);
        ritual = List.copyOf(ritual);
    }

    private static Map<String, List<String>> immutableNestedMap(Map<String, List<String>> source) {
        Map<String, List<String>> copy = new TreeMap<>();
        source.forEach((key, values) -> copy.put(key, List.copyOf(values)));
        return Collections.unmodifiableMap(copy);
    }
}
