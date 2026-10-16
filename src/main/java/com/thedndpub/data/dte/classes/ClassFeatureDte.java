package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassFeatureDte(
        String name,
        String source,
        Integer page,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024,
        String className,
        String classSource,
        Integer level,
        Integer header,
        String type,
        Boolean isClassFeatureVariant,
        ClassFeatureConsumptionDte consumes,
        List<ClassSourceReferenceDte> otherSources,
        List<ClassEntryDte> entries) implements ClassRootDte {
    public ClassFeatureDte {
        otherSources = ClassDteCollections.list(otherSources);
        entries = ClassDteCollections.list(entries);
    }
}
