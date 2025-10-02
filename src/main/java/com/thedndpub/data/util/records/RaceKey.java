package com.thedndpub.data.util.records;

public record RaceKey(String name, String source) {
    public RaceKey {
        name   = name   == null ? "" : name.trim().toLowerCase();
        source = source == null ? "" : source.trim().toLowerCase();
    }
}