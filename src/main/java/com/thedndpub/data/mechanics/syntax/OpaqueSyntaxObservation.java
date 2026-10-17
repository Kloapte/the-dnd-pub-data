package com.thedndpub.data.mechanics.syntax;

import java.util.Objects;

public record OpaqueSyntaxObservation(MarkupTag tag, OpaqueTagCategory category) implements SyntaxObservation {
    public OpaqueSyntaxObservation {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(category, "category");
    }
}
