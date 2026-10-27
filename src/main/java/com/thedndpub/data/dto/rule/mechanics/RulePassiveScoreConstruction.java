package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RulePassiveScoreConstruction(
        String kind,
        Integer base,
        Boolean addsCheckBonus,
        Integer advantageAdjustment,
        Integer disadvantageAdjustment) {
}
