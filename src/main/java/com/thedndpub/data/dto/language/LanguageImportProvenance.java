package com.thedndpub.data.dto.language;

import java.util.List;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;

public record LanguageImportProvenance(
        String sourceFile,
        Integer page,
        List<EntryDte> entries,
        List<String> reprintedAs,
        List<String> typicalSpeakers,
        String origin,
        List<String> fonts,
        List<SourceReference> additionalSources,
        List<SourceReference> otherSources,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        Boolean basicRules2024,
        Boolean hasFluffImages) {
    public LanguageImportProvenance {
        reprintedAs = List.copyOf(reprintedAs);
        typicalSpeakers = List.copyOf(typicalSpeakers);
        entries = List.copyOf(entries);
        fonts = List.copyOf(fonts);
        additionalSources = List.copyOf(additionalSources);
        otherSources = List.copyOf(otherSources);
    }
}
