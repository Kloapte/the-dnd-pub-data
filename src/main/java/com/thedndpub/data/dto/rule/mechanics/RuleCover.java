package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleCover(RuleMechanicKind kind, String subtype, String level, Integer armorClassBonus,
        Integer savingThrowBonus, String savingThrowAbility, String degreeSelection, Boolean blocksTargeting,
        String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
