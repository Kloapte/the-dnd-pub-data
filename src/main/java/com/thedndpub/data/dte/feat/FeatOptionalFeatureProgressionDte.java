package com.thedndpub.data.dte.feat;

import java.util.List;
import java.util.Map;

public record FeatOptionalFeatureProgressionDte(
        String name,
        List<String> featureType,
        Map<String, Integer> progression) {
}
