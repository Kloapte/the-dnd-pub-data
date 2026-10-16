package com.thedndpub.data.dte.condition;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

/** Typed source record for the adjacent status collection, which is not a condition or disease. */
public record StatusDte(
        String name,
        String source,
        Integer page,
        List<String> reprintedAs,
        List<EntryDte> entries,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        Boolean basicRules2024) {
}
