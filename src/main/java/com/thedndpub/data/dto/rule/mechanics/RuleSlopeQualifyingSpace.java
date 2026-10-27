package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceKind;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSlopeQualifyingSpace(
        RuleQualifyingSpaceKind kind,
        Integer minimumDegrees) implements RuleQualifyingSpace {
    public RuleSlopeQualifyingSpace {
        if (kind != RuleQualifyingSpaceKind.SLOPE) {
            throw new IllegalArgumentException("Expected qualifying-space kind SLOPE");
        }
        Objects.requireNonNull(minimumDegrees, "Slope qualifying space requires minimumDegrees");
        if (minimumDegrees != 20) throw new IllegalArgumentException("Slope minimumDegrees must equal 20");
    }
}
