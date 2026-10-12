package com.thedndpub.data.dto.monster.content;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ContentText(String value) implements ContentValue {
}
