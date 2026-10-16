package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OptionalFeatureLevelPrerequisiteDte(
        Integer level,
        @JsonProperty("class")
        OptionalFeatureClassReferenceDte classReference,
        OptionalFeatureClassReferenceDte subclass) {
}
