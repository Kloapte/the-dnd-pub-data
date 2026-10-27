package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionGrappledMovementCost(
        String kind,
        Double multiplier,
        Integer waivedWhenTargetAtLeastSizesSmaller) {
}
