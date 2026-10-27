package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleSocialAttitude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSocial(RuleMechanicKind kind, RuleMechanicSubtype subtype, RuleSocialAttitude attitude,
        RuleAbilityCheck check,
        Double scoreThreshold, String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
