package com.thedndpub.data.dto.language;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageDto(
        LanguageId id,
        String name,
        String source,
        String type,
        String script,
        List<String> dialects) {
    public LanguageDto {
        dialects = List.copyOf(dialects);
    }

    public static LanguageDto from(NormalizedLanguageDefinition definition) {
        return new LanguageDto(
                definition.id(),
                definition.name(),
                definition.source().code(),
                definition.type(),
                definition.script(),
                definition.dialects());
    }
}
