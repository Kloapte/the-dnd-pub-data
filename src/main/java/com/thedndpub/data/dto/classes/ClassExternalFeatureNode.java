package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassExternalFeatureNode(
        ClassExternalReference reference,
        String name,
        Boolean required,
        Boolean preserveConsumes,
        Boolean preservePrerequisite) implements ClassFeatureNode {
}
