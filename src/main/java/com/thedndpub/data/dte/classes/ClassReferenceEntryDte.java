package com.thedndpub.data.dte.classes;

public record ClassReferenceEntryDte(
        String type,
        String classFeature,
        String subclassFeature,
        String optionalfeature,
        String feat,
        String name,
        ClassReferencePreserveDte preserve,
        ClassEntryMetadataDte data) implements ClassEntryDte {
}
