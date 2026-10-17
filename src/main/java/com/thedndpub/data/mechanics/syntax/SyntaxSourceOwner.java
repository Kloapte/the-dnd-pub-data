package com.thedndpub.data.mechanics.syntax;

/** A source occurrence, never a newly assigned catalog identity. */
public record SyntaxSourceOwner(
        String resourcePath,
        String resourceSha256,
        String collection,
        String jsonPointer) {
    public SyntaxSourceOwner {
        if (resourcePath == null || resourcePath.isBlank() || collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("Resource and collection are required");
        }
        if (resourceSha256 != null && !resourceSha256.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("Resource fingerprint must be a SHA-256 hexadecimal value");
        }
        SyntaxPointers.requirePointer(jsonPointer);
    }
}
