package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyBonusLevelBasis(RuleMechanicKind kind, RuleMechanicSubtype subtype, String componentId,
        String levelBasis) implements RuleMechanic {
}
