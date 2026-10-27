package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleUnarmedOption(String componentId, RuleMechanicKind kind, String subtype, String option,
        RuleFailedSave onFailedSave, String targetLimit, Boolean requiresFreeHand,
        RuleComponentApplicability appliesWhen, Integer reachFeet) implements RuleMechanic {
}
