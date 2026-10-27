package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleJumpDistanceFormula(String kind, Integer baseFeet, String ability, Integer minimumFeet,
        String input) { }
