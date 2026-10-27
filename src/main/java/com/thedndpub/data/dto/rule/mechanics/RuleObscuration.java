package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleObscuration(RuleMechanicKind kind, String subtype, String level, String sightEffect,
        String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
