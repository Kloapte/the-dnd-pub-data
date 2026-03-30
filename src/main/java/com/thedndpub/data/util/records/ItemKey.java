package com.thedndpub.data.util.records;

public record ItemKey(String name, String source) {
    public ItemKey {
        name   = name   == null ? "" : name.trim().toLowerCase();
        source = source == null ? "" : source.trim().toLowerCase();
    }
}