package com.thedndpub.data.dto.monster.support;

import com.fasterxml.jackson.core.JsonPointer;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.legendarygroup.LegendaryGroupEntryCopies;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;

import java.util.Objects;

/** One ordered top-level entry with its original authored node location, including inherited entries. */
public record LegendaryGroupBodyEntry(
        EntryDte content,
        SyntaxSourceOwner originOwner,
        String jsonPointer) {
    public LegendaryGroupBodyEntry {
        content = LegendaryGroupEntryCopies.copy(Objects.requireNonNull(content, "content"));
        Objects.requireNonNull(originOwner, "originOwner");
        JsonPointer.compile(Objects.requireNonNull(jsonPointer, "jsonPointer"));
        if (!jsonPointer.startsWith(originOwner.jsonPointer() + "/")) {
            throw new IllegalArgumentException("Body entry must belong to its original source owner");
        }
    }
}
