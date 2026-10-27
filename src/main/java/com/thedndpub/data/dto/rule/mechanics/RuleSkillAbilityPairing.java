package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.rule.RuleReference;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleSkillAbilityContext;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSkillAbilityPairing(RuleReference skill, ActionAbility usualAbility, ActionAbility alternativeAbility,
        RuleSkillAbilityContext context, String status) {
}
