package com.thedndpub.data.dto.language;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageSourceCitation(String source, Integer page) {
}
