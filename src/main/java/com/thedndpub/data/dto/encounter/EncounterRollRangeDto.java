package com.thedndpub.data.dto.encounter;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EncounterRollRangeDto(Integer min, Integer max) {
}
