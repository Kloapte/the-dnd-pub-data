package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonDeserialize(using = ClassFeatureUnlockDeserializer.class)
public record ClassFeatureUnlockDte(
        String classFeature,
        Boolean gainSubclassFeature,
        Boolean gainSubclassFeatureHasContent,
        String tableDisplayName,
        @JsonIgnore boolean stringForm) {
}
