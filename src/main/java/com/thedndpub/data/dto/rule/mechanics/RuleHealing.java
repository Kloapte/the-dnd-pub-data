package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleHealing(RuleMechanicKind kind, String subtype, String method, Double amount, String unit,
        String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
