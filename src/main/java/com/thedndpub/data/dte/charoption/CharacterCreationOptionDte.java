package com.thedndpub.data.dte.charoption;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record CharacterCreationOptionDte(
        String name,
        String source,
        Integer page,
        List<CharacterOptionPrerequisiteDte> prerequisite,
        List<String> optionType,
        List<EntryDte> entries,
        List<CharacterOptionSourceReferenceDte> otherSources,
        Boolean hasFluffImages,
        List<CharacterOptionReprintDte> reprintedAs) {
    public CharacterCreationOptionDte {
        prerequisite = prerequisite == null ? null : List.copyOf(prerequisite);
        optionType = optionType == null ? null : List.copyOf(optionType);
        entries = entries == null ? null : List.copyOf(entries);
        otherSources = otherSources == null ? null : List.copyOf(otherSources);
        reprintedAs = reprintedAs == null ? null : List.copyOf(reprintedAs);
    }
}
