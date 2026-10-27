package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionAttackBonus(ActionAbility ability, Boolean proficiencyBonus) {
}
