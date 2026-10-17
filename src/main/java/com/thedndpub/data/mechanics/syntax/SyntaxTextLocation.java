package com.thedndpub.data.mechanics.syntax;

import java.util.List;
import java.util.Objects;

public record SyntaxTextLocation(
        SyntaxSourceOwner owner,
        String jsonPointer,
        List<SyntaxContext> hierarchy,
        SyntaxOrigin origin) {
    public SyntaxTextLocation {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(origin, "origin");
        SyntaxPointers.requirePointer(jsonPointer);
        hierarchy = List.copyOf(hierarchy);
        if (!SyntaxPointers.contains(owner.jsonPointer(), jsonPointer)) {
            throw new IllegalArgumentException("Text location must belong to its supplied owner");
        }
        String previous = owner.jsonPointer();
        for (SyntaxContext context : hierarchy) {
            if (!SyntaxPointers.contains(previous, context.jsonPointer())
                    || !SyntaxPointers.contains(context.jsonPointer(), jsonPointer)) {
                throw new IllegalArgumentException("Hierarchy must follow the original ancestor order");
            }
            previous = context.jsonPointer();
        }
    }
}
