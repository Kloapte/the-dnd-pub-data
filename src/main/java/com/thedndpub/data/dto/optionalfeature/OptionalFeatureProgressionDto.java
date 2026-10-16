package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeatureProgressionDto(
        String name,
        List<String> featureTypes,
        List<String> categories,
        Map<String, Integer> progression) {
    public OptionalFeatureProgressionDto {
        featureTypes = List.copyOf(featureTypes);
        categories = List.copyOf(categories);
        progression = Collections.unmodifiableMap(new TreeMap<>(progression));
    }
}
