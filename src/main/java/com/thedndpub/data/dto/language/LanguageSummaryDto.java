package com.thedndpub.data.dto.language;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageSummaryDto(
        LanguageId id,
        String name,
        String source,
        String type,
        String script,
        List<String> dialects) {
    public LanguageSummaryDto {
        dialects = List.copyOf(dialects);
    }

    public static LanguageSummaryDto from(NormalizedLanguageDefinition definition) {
        return new LanguageSummaryDto(
                definition.id(),
                definition.name(),
                definition.source().code(),
                definition.type(),
                definition.script(),
                definition.dialects());
    }
}
