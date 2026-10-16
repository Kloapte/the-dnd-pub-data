package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemLightDto(Integer bright, Integer dim, String shape) {
}
