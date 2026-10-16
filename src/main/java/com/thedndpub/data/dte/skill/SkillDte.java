package com.thedndpub.data.dte.skill;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record SkillDte(
        String name,
        String source,
        Integer page,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        List<String> reprintedAs,
        String ability,
        List<EntryDte> entries) {
    public SkillDte {
        reprintedAs = reprintedAs == null ? null : List.copyOf(reprintedAs);
        entries = entries == null ? null : List.copyOf(entries);
    }
}
