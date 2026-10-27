package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionAttackRoll;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleAttackRoll(RuleMechanicKind kind, RuleMechanicSubtype subtype, String componentId,
        RuleComponentApplicability appliesWhen, ActionAttackRoll roll) implements RuleMechanic {
}
