package com.thedndpub.data.dto.feat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** The owning field distinguishes ordinary sense ranges from bonus sense values. */
public record FeatSenseValues(Map<String, Integer> ranges) {
    public FeatSenseValues {
        ranges = Collections.unmodifiableMap(new LinkedHashMap<>(ranges));
    }
}
