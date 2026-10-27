package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionD20RollModifier;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleTestModifier(RuleMechanicKind kind, String subtype, ActionD20RollModifier modifier,
        String test, String direction, Double amount, Integer diceCount, String select,
        Boolean cancelsOpposite, Boolean multipleInstancesStack, String componentId,
        RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
