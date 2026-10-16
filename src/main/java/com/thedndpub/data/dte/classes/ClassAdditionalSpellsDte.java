package com.thedndpub.data.dte.classes;

import java.util.Map;

public record ClassAdditionalSpellsDte(
        String name,
        String ability,
        String resourceName,
        Map<String, ClassSpellLevelDte> known,
        Map<String, ClassSpellLevelDte> prepared,
        Map<String, ClassSpellLevelDte> expanded,
        Map<String, ClassSpellLevelDte> innate) {
    public ClassAdditionalSpellsDte {
        known = ClassDteCollections.map(known);
        prepared = ClassDteCollections.map(prepared);
        expanded = ClassDteCollections.map(expanded);
        innate = ClassDteCollections.map(innate);
    }
}
