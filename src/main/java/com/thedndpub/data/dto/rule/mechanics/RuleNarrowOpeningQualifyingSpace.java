package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceSize;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleNarrowOpeningQualifyingSpace(
        RuleQualifyingSpaceKind kind,
        RuleQualifyingSpaceSize size) implements RuleQualifyingSpace {
    public RuleNarrowOpeningQualifyingSpace {
        if (kind != RuleQualifyingSpaceKind.NARROW_OPENING) {
            throw new IllegalArgumentException("Expected qualifying-space kind NARROW_OPENING");
        }
        Objects.requireNonNull(size, "Narrow-opening qualifying space requires size");
        if (size != RuleQualifyingSpaceSize.ONE_SIZE_SMALLER_THAN_CREATURE) {
            throw new IllegalArgumentException("Narrow opening size must be one-size-smaller-than-creature");
        }
    }
}
