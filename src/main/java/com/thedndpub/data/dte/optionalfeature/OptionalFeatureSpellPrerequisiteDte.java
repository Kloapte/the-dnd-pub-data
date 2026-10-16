package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OptionalFeatureSpellPrerequisiteDeserializer.class)
public record OptionalFeatureSpellPrerequisiteDte(
        String spell,
        String entry,
        String entrySummary,
        String choose,
        Integer count) {
}
