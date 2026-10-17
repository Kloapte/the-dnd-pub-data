package com.thedndpub.data.dto.feat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record FeatOptionalFeatureProgression(
        String name,
        List<String> featureTypes,
        Map<String, Integer> progression) {
    public FeatOptionalFeatureProgression {
        featureTypes = featureTypes == null ? null : List.copyOf(featureTypes);
        progression = progression == null ? null : Collections.unmodifiableMap(new LinkedHashMap<>(progression));
    }
}
