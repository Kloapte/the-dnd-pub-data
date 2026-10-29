package com.thedndpub.data.dto.sense.capabilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.sense.enums.SenseCapabilityKind;
import com.thedndpub.data.dto.sense.enums.SenseColorPerception;
import com.thedndpub.data.dto.sense.enums.SenseCondition;
import com.thedndpub.data.dto.sense.enums.SenseLightingEffect;
import com.thedndpub.data.dto.sense.enums.SensePerception;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SightCapability(
        SenseCapabilityKind kind,
        List<SensePerception> perceives,
        List<SenseCondition> conditions,
        SenseLightingEffect darknessAs,
        SenseColorPerception colorPerception,
        SenseLightingEffect dimLightAs,
        SenseIllusionInteraction illusionInteraction) implements SenseCapability {
    public SightCapability {
        if (kind != SenseCapabilityKind.SIGHT) throw new IllegalArgumentException("Sight kind must be SIGHT");
        Objects.requireNonNull(perceives, "Sight perceives is required");
        if (perceives.isEmpty() || new HashSet<>(perceives).size() != perceives.size()) {
            throw new IllegalArgumentException("Sight perceives must be nonempty and unique");
        }
        perceives = List.copyOf(perceives);
        conditions = conditions == null ? List.of() : List.copyOf(conditions);
    }
}
