package com.thedndpub.data.dto.language.speakers;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageCreatureTag(String group, String value) {
}
