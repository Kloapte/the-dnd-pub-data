package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassExternalReference(
        ClassExternalKind kind,
        String name,
        SourceIdentity source,
        String targetId,
        ClassReferenceStatus status) {
}
