package com.thedndpub.data.dto.treasure;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TreasureRollRangeDto(Integer min, Integer max) {
}
