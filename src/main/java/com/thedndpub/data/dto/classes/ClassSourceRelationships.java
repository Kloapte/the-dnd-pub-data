package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSourceRelationships(
        List<SourceReference> otherSources,
        List<ClassReprintReference> reprints,
        ClassPublicationFlags publicationFlags) {
    public ClassSourceRelationships {
        otherSources = otherSources == null ? List.of() : List.copyOf(otherSources);
        reprints = reprints == null ? List.of() : List.copyOf(reprints);
    }
}
