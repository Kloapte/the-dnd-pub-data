package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyAbilityCheckVariant(RuleMechanicKind kind, String subtype, String componentId,
        String replaces, List<String> proficiencySources, Integer proficiencyCount, String appliesTo,
        Integer startsAtLevel) implements RuleMechanic {
    public RuleProficiencyAbilityCheckVariant {
        proficiencySources = proficiencySources == null ? List.of() : List.copyOf(proficiencySources);
    }
}
