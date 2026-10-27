package com.thedndpub.data.dto.language.speakers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.enums.LanguageTagSelectorKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageCreatureTagSelector(LanguageTagSelectorKind kind, String value, String source) {
}
