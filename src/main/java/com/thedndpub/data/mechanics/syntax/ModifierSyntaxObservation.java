package com.thedndpub.data.mechanics.syntax;

import java.util.Objects;

public record ModifierSyntaxObservation(MarkupTag tag, SyntaxExpression expression) implements SyntaxObservation {
    public ModifierSyntaxObservation {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(expression, "expression");
    }
}
