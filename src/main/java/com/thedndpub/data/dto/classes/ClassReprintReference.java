package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassReprintReference(
        ClassDefinitionKind kind,
        String targetId,
        ClassDefinitionIdentity identity) {
}
