package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionDcValue;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSavingThrowValue(
        String kind,
        List<String> abilities,
        String abilitySelection,
        ActionDcValue dc,
        Boolean mayChooseToFail,
        Boolean automaticFailureWhenAbilityScoreAbsent) {
    public RuleSavingThrowValue {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
