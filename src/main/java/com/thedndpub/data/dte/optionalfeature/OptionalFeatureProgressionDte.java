package com.thedndpub.data.dte.optionalfeature;

import java.util.List;
import java.util.Map;

public record OptionalFeatureProgressionDte(
        String name,
        List<String> featureType,
        List<String> category,
        Map<String, Integer> progression) {
}
