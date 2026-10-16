package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassGeneratedTablesDte(
        Boolean tableInclude,
        List<String> reprintedAs) {
    public ClassGeneratedTablesDte {
        reprintedAs = ClassDteCollections.list(reprintedAs);
    }
}
