package com.thedndpub.data.dto.race;

import java.util.Objects;

public record RaceId(String value) implements Comparable<RaceId> {
    public RaceId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public int compareTo(RaceId other) {
        return value.compareTo(other.value);
    }
}
