package com.thedndpub.data.dto.sense;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.Objects;

public record SenseDefinition(SenseId id, String name, SourceReference source) {
    public SenseDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
    }
}
