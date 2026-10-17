package com.thedndpub.data.dto.charoption;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CharacterOptionRacePrerequisiteDto(List<String> raceNames) {
    public CharacterOptionRacePrerequisiteDto {
        raceNames = List.copyOf(raceNames);
    }
}
