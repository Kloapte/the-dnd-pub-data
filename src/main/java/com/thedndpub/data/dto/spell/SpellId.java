package com.thedndpub.data.dto.spell;

import java.util.Objects;

public record SpellId(String value) {
    public SpellId {
        Objects.requireNonNull(value, "value");
        if (!value.matches("spell:[a-z0-9-]+:[a-z0-9-]+")) {
            throw new IllegalArgumentException("Invalid spell ID: " + value);
        }
    }
}
