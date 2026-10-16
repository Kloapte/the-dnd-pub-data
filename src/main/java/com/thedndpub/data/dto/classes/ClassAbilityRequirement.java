package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassAbilityRequirement(
        ClassRequirementKind kind,
        List<ClassAbilityThreshold> thresholds,
        List<ClassAbilityRequirement> groups) {
    public ClassAbilityRequirement {
        thresholds = thresholds == null ? List.of() : List.copyOf(thresholds);
        groups = groups == null ? List.of() : List.copyOf(groups);
    }
}
