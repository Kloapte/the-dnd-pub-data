package com.thedndpub.data.dto.charoption;

import java.util.Objects;

/** Unqualified authored race name; it has no inferred race source or target ID. */
public record CharacterOptionRaceRequirement(String name) {
    public CharacterOptionRaceRequirement {
        Objects.requireNonNull(name, "name");
    }
}
