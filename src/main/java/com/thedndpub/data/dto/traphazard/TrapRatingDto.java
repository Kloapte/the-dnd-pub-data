package com.thedndpub.data.dto.traphazard;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TrapRatingDto(Integer tier, String threat) {
}
