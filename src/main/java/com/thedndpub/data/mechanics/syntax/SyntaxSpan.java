package com.thedndpub.data.mechanics.syntax;

/** Java String UTF-16 offsets: inclusive start and exclusive end. */
public record SyntaxSpan(int startInclusive, int endExclusive) {
    public SyntaxSpan {
        if (startInclusive < 0 || endExclusive < startInclusive) {
            throw new IllegalArgumentException("Invalid UTF-16 span");
        }
    }

    public String extract(String original) {
        return original.substring(startInclusive, endExclusive);
    }
}
