package com.thedndpub.data.importer.spell;

import com.thedndpub.data.dte.spell.SpellFileDte;

import java.util.Objects;

public record SpellSourceDocument(String resourceName, SpellFileDte data) {
    public SpellSourceDocument {
        Objects.requireNonNull(resourceName, "resourceName");
        Objects.requireNonNull(data, "data");
    }
}
