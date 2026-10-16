package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dte.classes.ClassRootDte;

import java.util.List;
import java.util.Set;

public record ResolvedClassRecord(
        ClassRootDte data,
        ClassSourceRecord authored,
        List<ClassSourceLocation> copyBases,
        Set<String> inheritedFields,
        Set<String> suppressedMetadata) {
    public ResolvedClassRecord {
        copyBases = List.copyOf(copyBases);
        inheritedFields = Set.copyOf(inheritedFields);
        suppressedMetadata = Set.copyOf(suppressedMetadata);
    }
}
