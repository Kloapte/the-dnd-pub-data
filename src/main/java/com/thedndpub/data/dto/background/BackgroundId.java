package com.thedndpub.data.dto.background;

import java.util.Objects;

public record BackgroundId(String value) implements Comparable<BackgroundId> {
    public BackgroundId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public int compareTo(BackgroundId other) {
        return value.compareTo(other.value);
    }
}
