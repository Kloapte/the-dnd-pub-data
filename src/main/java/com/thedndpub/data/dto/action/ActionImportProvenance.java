package com.thedndpub.data.dto.action;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ActionImportProvenance(
        List<EntryDte> entries,
        List<String> seeAlsoAction,
        String fromVariant,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        Boolean basicRules2024) {
    public ActionImportProvenance {
        entries = List.copyOf(entries);
        seeAlsoAction = List.copyOf(seeAlsoAction);
    }
}
