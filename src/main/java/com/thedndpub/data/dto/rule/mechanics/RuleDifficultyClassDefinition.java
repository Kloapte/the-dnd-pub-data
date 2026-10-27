package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDifficultyClassTarget;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDifficultyClassDefinition(
        RuleMechanicKind kind,
        RuleMechanicSubtype subtype,
        String componentId,
        List<RuleDifficultyClassTarget> targetNumberFor) implements RuleMechanic {
    public RuleDifficultyClassDefinition {
        targetNumberFor = targetNumberFor == null ? List.of() : List.copyOf(targetNumberFor);
    }
}
