package com.thedndpub.data.mechanics.syntax;

import java.util.Objects;

/** The original tag name identifies a label; its arguments and scope remain uninterpreted. */
public record LabelSyntaxObservation(MarkupTag tag) implements SyntaxObservation {
    public LabelSyntaxObservation {
        Objects.requireNonNull(tag, "tag");
    }
}
