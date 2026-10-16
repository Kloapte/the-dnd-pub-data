package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dte.classes.ClassRootDte;
import com.thedndpub.data.importer.classes.ClassSourceLocation;
import com.thedndpub.data.importer.classes.ClassSourceRecord;

import java.util.List;
import java.util.Set;

/** Typed authored and effective source material, retained solely for import audit and later projections. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassImportProvenance(
        ClassSourceRecord authored,
        ClassRootDte effective,
        List<ClassSourceLocation> copyBases,
        Set<String> inheritedFields,
        Set<String> suppressedMetadata) {
    public ClassImportProvenance {
        copyBases = List.copyOf(copyBases);
        inheritedFields = Set.copyOf(inheritedFields);
        suppressedMetadata = Set.copyOf(suppressedMetadata);
    }
}
