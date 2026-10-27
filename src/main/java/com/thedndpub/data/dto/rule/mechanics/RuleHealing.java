package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleHealingMethod;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleHealingUnit;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleHealing(RuleMechanicKind kind, RuleMechanicSubtype subtype, RuleHealingMethod method, Double amount,
        RuleHealingUnit unit,
        String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
