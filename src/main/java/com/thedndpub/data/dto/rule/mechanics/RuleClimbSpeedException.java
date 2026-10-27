package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleClimbSpeedException(String kind, String speedType, String appliesTo,
        Boolean replacesSpeed, Boolean waivesClimbingExtraMovementCost) implements RuleMovementDetails { }
