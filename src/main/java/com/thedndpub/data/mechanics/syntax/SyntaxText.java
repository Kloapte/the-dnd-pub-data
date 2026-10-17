package com.thedndpub.data.mechanics.syntax;

import java.util.Objects;

/** Exact JSON-decoded source string supplied to the parser. Internal content only. */
public record SyntaxText(SyntaxTextLocation location, String text) {
    public SyntaxText {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(text, "text");
    }
}
