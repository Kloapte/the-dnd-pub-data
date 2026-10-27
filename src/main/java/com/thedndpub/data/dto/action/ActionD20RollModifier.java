package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.action.enums.ActionRollDirection;
import com.thedndpub.data.dto.action.enums.ActionRollType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionD20RollModifier(
        String kind,
        ActionRollType roll,
        ActionRollDirection direction,
        Double amount,
        Integer diceCount,
        String select,
        Boolean cancelsOpposite,
        Boolean multipleInstancesStack,
        ActionAbility ability) {
}
