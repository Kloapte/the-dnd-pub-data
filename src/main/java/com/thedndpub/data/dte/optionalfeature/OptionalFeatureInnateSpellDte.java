package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OptionalFeatureInnateSpellDeserializer.class)
public record OptionalFeatureInnateSpellDte(
        @JsonProperty("_") List<String> spells,
        Map<String, List<String>> daily,
        Map<String, List<String>> resource,
        List<OptionalFeatureSpellPrerequisiteDte> ritual) {
}
