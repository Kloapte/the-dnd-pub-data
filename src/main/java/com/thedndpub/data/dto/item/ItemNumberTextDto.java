package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemNumberTextDto(Integer number, String text) {
}
