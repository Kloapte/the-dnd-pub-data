package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureChoice(
        Integer count,
        String style,
        List<ClassFeatureNode> options) implements ClassFeatureNode {
    public ClassFeatureChoice {
        options = options == null ? List.of() : List.copyOf(options);
    }
}
