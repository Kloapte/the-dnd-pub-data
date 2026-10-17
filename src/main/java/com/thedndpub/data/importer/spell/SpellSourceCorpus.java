package com.thedndpub.data.importer.spell;

import com.thedndpub.data.dte.spell.SpellDte;

import java.util.List;

public record SpellSourceCorpus(List<SpellSourceDocument> documents) {
    public SpellSourceCorpus {
        documents = List.copyOf(documents);
    }

    public List<SpellDte> spells() {
        return documents.stream()
                .flatMap(document -> document.data().spells().stream())
                .toList();
    }
}
