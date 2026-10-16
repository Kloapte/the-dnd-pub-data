package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureStatblock(
        ClassExternalReference reference,
        SourceReference source,
        String style) implements ClassFeatureNode {
}
