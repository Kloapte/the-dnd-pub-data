package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleFlankingComparison;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleFlankingSizeGroup;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFlankingHexSeparation(RuleFlankingSizeGroup targetSizeGroup, Integer hexesBetween,
        RuleFlankingComparison comparison) {
}
