package com.thedndpub.data.dto.disease.effects;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseDamageRoll(Integer count, Integer die) {
}
