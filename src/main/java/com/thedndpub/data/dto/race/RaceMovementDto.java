package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceMovementDto(
        RaceMovementValueDto walk,
        RaceMovementValueDto fly,
        RaceMovementValueDto swim,
        RaceMovementValueDto climb,
        RaceMovementValueDto burrow) {
}
