package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleObscurationLevel;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleObscurationSightEffect;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleObscuration(RuleMechanicKind kind, RuleMechanicSubtype subtype, RuleObscurationLevel level,
        RuleObscurationSightEffect sightEffect,
        String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
