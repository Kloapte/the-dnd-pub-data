package com.thedndpub.data.dte.monster;

public record MonsterSourceLocation(String filename, int arrayIndex) {
    public MonsterSourceLocation {
        if (filename == null || filename.isBlank() || arrayIndex < 0) {
            throw new IllegalArgumentException("Invalid monster source location");
        }
    }
}
