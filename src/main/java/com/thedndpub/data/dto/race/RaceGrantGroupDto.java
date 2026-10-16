package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceGrantGroupDto(List<RaceGrantDto> grants) {
    public RaceGrantGroupDto {
        grants = grants == null ? List.of() : List.copyOf(grants);
    }
}
