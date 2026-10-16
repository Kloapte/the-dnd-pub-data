package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OptionalFeatureClassReferenceDte(
        String name,
        String source,
        Boolean visibleStats) {
}
