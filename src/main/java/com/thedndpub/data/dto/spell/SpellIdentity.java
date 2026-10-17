package com.thedndpub.data.dto.spell;

import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.Objects;

public record SpellIdentity(String name, SourceIdentity source) {
    public SpellIdentity {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Spell name must not be blank");
        }
    }
}
