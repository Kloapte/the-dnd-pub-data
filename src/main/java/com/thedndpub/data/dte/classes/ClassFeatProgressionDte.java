package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassFeatProgressionDte(
        String name,
        List<String> category,
        ClassLevelProgressionDte progression) {
    public ClassFeatProgressionDte {
        category = ClassDteCollections.list(category);
    }
}
