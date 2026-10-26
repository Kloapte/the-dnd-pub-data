package com.thedndpub.data.dto.corerules.sense;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.CanonicalReference;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConditionInteraction(
        String kind,
        CanonicalReference interaction,
        String effect) implements SenseCapability {
    public ConditionInteraction {
        if (!"CONDITION_INTERACTION".equals(kind) || interaction == null || effect == null) {
            throw new IllegalArgumentException("Invalid sense condition interaction");
        }
    }
}
