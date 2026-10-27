package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyBonusScaling(RuleMechanicKind kind, String subtype, String componentId,
        String checkType, Boolean requiresSkillProficiency, Integer multiplier,
        Boolean unlessAlreadyDoubledByAnotherFeature, String assignment, Integer maximumInstancesPerSkill)
        implements RuleMechanic {
}
