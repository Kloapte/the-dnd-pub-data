package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SubclassFeatureDte(
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
        List<ClassEntryDte> entries,
        String subclassShortName,
        String subclassSource,
        @JsonProperty("_copy") ClassCopyDte copy) implements ClassRootDte {
    public SubclassFeatureDte {
        otherSources = ClassDteCollections.list(otherSources);
        entries = ClassDteCollections.list(entries);
    }
}
