package com.thedndpub.data.dto.monster.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record GearReference(String item, String displayName, Integer quantity) {
}
