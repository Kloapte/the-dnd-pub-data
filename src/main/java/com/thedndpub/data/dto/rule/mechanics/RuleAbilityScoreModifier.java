package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleAbilityScoreModifier(
        String componentId,
        RuleMechanicKind kind,
        RuleMechanicSubtype subtype,
        Integer minimumScore,
        Integer maximumScore,
        List<RuleAbilityScoreBand> scoreBands,
        List<ActionAbility> abilities,
        String appliesTo) implements RuleMechanic {
    public RuleAbilityScoreModifier {
        scoreBands = scoreBands == null ? List.of() : List.copyOf(scoreBands);
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
