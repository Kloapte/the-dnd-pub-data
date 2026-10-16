package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassAbilityEntryDte(
        String type,
        String name,
        List<String> attributes) implements ClassEntryDte {
    public ClassAbilityEntryDte {
        attributes = ClassDteCollections.list(attributes);
    }
}
