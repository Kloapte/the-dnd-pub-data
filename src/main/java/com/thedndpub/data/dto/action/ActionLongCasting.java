package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionLongCasting(
        ActionLongCastingWhen when,
        Boolean magicActionEachTurn,
        Boolean concentrationRequired,
        Boolean failureOnConcentrationLoss,
        Boolean spellSlotExpendedOnFailure) {
}
