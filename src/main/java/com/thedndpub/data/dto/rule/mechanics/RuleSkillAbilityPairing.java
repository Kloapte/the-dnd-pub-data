package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.RuleReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSkillAbilityPairing(RuleReference skill, String usualAbility, String alternativeAbility,
        String context, String status) {
}
