package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSpecialSpeedSelection(String kind, Boolean canSwitchDuringMove,
        Boolean subtractDistanceAlreadyMoved, Integer switchProhibitedWhenRemainingDistanceAtMost)
        implements RuleMovementDetails { }
