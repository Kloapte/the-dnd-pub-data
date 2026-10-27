package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSocial(RuleMechanicKind kind, String subtype, String attitude, RuleAbilityCheck check,
        Double scoreThreshold, String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
