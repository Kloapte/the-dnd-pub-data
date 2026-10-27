package com.thedndpub.data.dto.language;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageSourceFlags(Boolean srd, Boolean srd52, Boolean basicRules, Boolean basicRules2024) {
}
