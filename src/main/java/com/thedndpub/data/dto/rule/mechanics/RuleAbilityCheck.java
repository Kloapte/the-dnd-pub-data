package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionDcValue;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.rule.RuleReference;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleAbilityCheck(
        RuleMechanicKind kind,
        RuleMechanicSubtype subtype,
        List<ActionAbility> abilities,
        RuleReference skill,
        RuleReference tool,
        ActionDcValue dc,
        Boolean passiveScore,
        RulePassiveScoreConstruction passiveScoreConstruction,
        String componentId,
        RuleComponentApplicability appliesWhen) implements RuleMechanic {
    public RuleAbilityCheck {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
