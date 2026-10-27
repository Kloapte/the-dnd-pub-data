package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDeprivationSavingThrow(ActionAbility ability, Integer baseDc, Integer increasePerConsecutiveInterval) {
}
