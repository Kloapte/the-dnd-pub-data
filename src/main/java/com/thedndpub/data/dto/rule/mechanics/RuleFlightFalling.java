package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleFlightFallingTrigger;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFlightFalling(String kind, List<RuleFlightFallingTrigger> fallingTriggers, Boolean hoverPreventsFall)
        implements RuleMovementDetails {
    public RuleFlightFalling {
        fallingTriggers = fallingTriggers == null ? List.of() : List.copyOf(fallingTriggers);
    }
}
