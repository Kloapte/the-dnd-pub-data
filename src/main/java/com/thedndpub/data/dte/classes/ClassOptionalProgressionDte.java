package com.thedndpub.data.dte.classes;

import java.util.List;
import java.util.Map;

public record ClassOptionalProgressionDte(
        String name,
        List<String> featureType,
        ClassLevelProgressionDte progression,
        Map<Integer, List<String>> required) {
    public ClassOptionalProgressionDte {
        featureType = ClassDteCollections.list(featureType);
        required = ClassDteCollections.mapOfLists(required);
    }
}
