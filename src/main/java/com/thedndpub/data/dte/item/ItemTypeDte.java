package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ItemTypeDte(
        String name,
        String abbreviation,
        String source,
        Integer page,
        List<ItemReprintDte> reprintedAs,
        List<EntryDte> entries,
        Boolean basicRules2024,
        @JsonProperty("_copy") ItemTypeCopyDte copy) {
}
