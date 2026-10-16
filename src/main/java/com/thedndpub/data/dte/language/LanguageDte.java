package com.thedndpub.data.dte.language;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record LanguageDte(
        String name,
        String source,
        Integer page,
        List<String> reprintedAs,
        List<String> typicalSpeakers,
        String type,
        String script,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        Boolean basicRules2024,
        String origin,
        List<EntryDte> entries,
        Boolean hasFluffImages,
        List<String> fonts,
        List<LanguageSourceReferenceDte> additionalSources,
        List<String> dialects,
        List<LanguageSourceReferenceDte> otherSources) {
}
