package com.thedndpub.data.dte.action;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ActionDte(
        String name,
        String source,
        Integer page,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        Boolean basicRules2024,
        List<ActionTimeDte> time,
        List<EntryDte> entries,
        List<ActionReprintDte> reprintedAs,
        List<String> seeAlsoAction,
        String fromVariant) {
}
