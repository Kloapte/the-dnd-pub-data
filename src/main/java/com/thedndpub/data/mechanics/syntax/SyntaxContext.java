package com.thedndpub.data.mechanics.syntax;

/** Original container location and its authored type label, when present. */
public record SyntaxContext(String jsonPointer, String authoredType) {
    public SyntaxContext {
        SyntaxPointers.requirePointer(jsonPointer);
    }
}
