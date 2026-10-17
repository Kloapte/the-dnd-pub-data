package com.thedndpub.data.mechanics.syntax;

import java.util.Objects;

public record FormulaSyntaxObservation(MarkupTag tag, SyntaxExpression expression) implements SyntaxObservation {
    public FormulaSyntaxObservation {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(expression, "expression");
    }
}
