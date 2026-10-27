package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.action.enums.ActionDifficultyClassKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionDifficultyClass(
        ActionDifficultyClassKind kind,
        Integer value,
        Integer base,
        ActionAbility ability,
        Boolean proficiencyBonus,
        String spellRole,
        Integer minimum,
        String targetRole) {
}
