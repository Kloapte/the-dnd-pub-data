package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleSurfaceMaterial;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSurfaceMaterialQualifyingSpace(
        RuleQualifyingSpaceKind kind,
        List<RuleSurfaceMaterial> materials) implements RuleQualifyingSpace {
    public RuleSurfaceMaterialQualifyingSpace {
        if (kind != RuleQualifyingSpaceKind.SURFACE_MATERIAL) {
            throw new IllegalArgumentException("Expected qualifying-space kind SURFACE_MATERIAL");
        }
        Objects.requireNonNull(materials, "Surface-material qualifying space requires materials");
        if (materials.isEmpty() || new HashSet<>(materials).size() != materials.size()) {
            throw new IllegalArgumentException("Surface materials must be nonempty and unique");
        }
        materials = List.copyOf(materials);
    }
}
