package com.thedndpub.data.mechanics.syntax;

import java.util.ArrayList;
import java.util.List;

public record MarkupParseResult(List<MarkupTag> tags) {
    public MarkupParseResult {
        tags = List.copyOf(tags);
    }

    /** Parent before children, retaining original source order among siblings. */
    public List<MarkupTag> preorder() {
        List<MarkupTag> result = new ArrayList<>();
        append(tags, result);
        return List.copyOf(result);
    }

    private static void append(List<MarkupTag> tags, List<MarkupTag> result) {
        for (MarkupTag tag : tags) {
            result.add(tag);
            append(tag.children(), result);
        }
    }
}
