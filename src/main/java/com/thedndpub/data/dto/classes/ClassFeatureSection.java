package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureSection(
        String kind,
        String name,
        String text,
        List<ClassFeatureNode> entries,
        List<ClassFeatureNode> items) implements ClassFeatureNode {
    public ClassFeatureSection {
        entries = entries == null ? List.of() : List.copyOf(entries);
        items = items == null ? List.of() : List.copyOf(items);
    }
}
