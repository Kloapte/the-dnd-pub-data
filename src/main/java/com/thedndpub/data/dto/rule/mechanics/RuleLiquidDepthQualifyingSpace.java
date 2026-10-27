package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleLiquidDepth;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceKind;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleLiquidDepthQualifyingSpace(
        RuleQualifyingSpaceKind kind,
        RuleLiquidDepth depth) implements RuleQualifyingSpace {
    public RuleLiquidDepthQualifyingSpace {
        if (kind != RuleQualifyingSpaceKind.LIQUID_DEPTH) {
            throw new IllegalArgumentException("Expected qualifying-space kind LIQUID_DEPTH");
        }
        Objects.requireNonNull(depth, "Liquid-depth qualifying space requires depth");
    }
}
