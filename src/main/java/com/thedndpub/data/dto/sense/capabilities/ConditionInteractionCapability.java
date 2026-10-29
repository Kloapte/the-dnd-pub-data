package com.thedndpub.data.dto.sense.capabilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.sense.SenseReference;
import com.thedndpub.data.dto.sense.enums.SenseCapabilityKind;
import com.thedndpub.data.dto.sense.enums.SenseInteractionEffect;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionInteractionCapability(
        SenseCapabilityKind kind,
        SenseReference interaction,
        SenseInteractionEffect effect) implements SenseCapability {
    public ConditionInteractionCapability {
        if (kind != SenseCapabilityKind.CONDITION_INTERACTION) {
            throw new IllegalArgumentException("Condition interaction kind is invalid");
        }
        Objects.requireNonNull(interaction, "Condition interaction reference is required");
        Objects.requireNonNull(effect, "Condition interaction effect is required");
    }
}
