package com.thedndpub.data.dto.monster.identity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TypeTag(String tag, String prefix, Boolean prefixHidden) {
}
