package com.thedndpub.data.dto.monster.identity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Reference(String name, String source) {
}
