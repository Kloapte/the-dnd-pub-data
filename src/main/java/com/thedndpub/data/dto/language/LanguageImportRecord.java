package com.thedndpub.data.dto.language;

public record LanguageImportRecord(
        NormalizedLanguageDefinition definition,
        LanguageImportProvenance provenance) {
}
