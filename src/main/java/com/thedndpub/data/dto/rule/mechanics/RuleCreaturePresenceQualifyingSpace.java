package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceRestriction;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleCreaturePresenceQualifyingSpace(
        RuleQualifyingSpaceKind kind,
        RuleQualifyingSpaceRestriction restriction) implements RuleQualifyingSpace {
    public RuleCreaturePresenceQualifyingSpace {
        requireKind(kind, RuleQualifyingSpaceKind.CREATURE_PRESENCE);
        Objects.requireNonNull(restriction, "Creature-presence qualifying space requires restriction");
    }

    private static void requireKind(RuleQualifyingSpaceKind actual, RuleQualifyingSpaceKind expected) {
        if (actual != expected) throw new IllegalArgumentException("Expected qualifying-space kind " + expected);
    }
}
