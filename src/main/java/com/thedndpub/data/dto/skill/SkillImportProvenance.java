package com.thedndpub.data.dto.skill;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

/** Source-only presentation text and availability flags, excluded from public skill DTOs. */
public record SkillImportProvenance(
        String sourceFile,
        List<EntryDte> entries,
        List<String> reprintedAs,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52) {
    public SkillImportProvenance {
        entries = entries == null ? List.of() : List.copyOf(entries);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
