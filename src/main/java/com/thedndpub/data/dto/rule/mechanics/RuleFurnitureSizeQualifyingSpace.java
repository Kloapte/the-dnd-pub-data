package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceSize;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFurnitureSizeQualifyingSpace(
        RuleQualifyingSpaceKind kind,
        RuleQualifyingSpaceSize size) implements RuleQualifyingSpace {
    public RuleFurnitureSizeQualifyingSpace {
        if (kind != RuleQualifyingSpaceKind.FURNITURE_SIZE) {
            throw new IllegalArgumentException("Expected qualifying-space kind FURNITURE_SIZE");
        }
        Objects.requireNonNull(size, "Furniture-size qualifying space requires size");
        if (size != RuleQualifyingSpaceSize.SAME_OR_LARGER_THAN_CREATURE) {
            throw new IllegalArgumentException("Furniture size must be same-or-larger-than-creature");
        }
    }
}
