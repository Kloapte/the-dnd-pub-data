package com.thedndpub.data.mechanics.syntax;

import java.util.Objects;

public record SyntaxEvidence(SyntaxTextLocation location, SyntaxSpan span) {
    public SyntaxEvidence {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(span, "span");
    }
}
