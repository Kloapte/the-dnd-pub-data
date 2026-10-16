package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** File-scoped import metadata, kept outside independently addressable definitions. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFileMetadata(String resource, List<ClassDefinitionKind> internalCopyKinds) {
    public ClassFileMetadata {
        internalCopyKinds = List.copyOf(internalCopyKinds);
    }
}
