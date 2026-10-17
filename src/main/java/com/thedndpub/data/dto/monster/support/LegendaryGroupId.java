package com.thedndpub.data.dto.monster.support;

import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.Objects;

/** Exact authored support identity; never a Monster catalog identity. */
public record LegendaryGroupId(String name, SourceIdentity source) implements Comparable<LegendaryGroupId> {
    public LegendaryGroupId {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Legendary group name must not be blank");
        }
    }

    public static LegendaryGroupId from(String name, String source) {
        return new LegendaryGroupId(name, new SourceIdentity(source));
    }

    public String value() {
        return "legendary-group:" + source.code() + ":" + name;
    }

    @Override
    public int compareTo(LegendaryGroupId other) {
        int sourceComparison = source.code().compareTo(other.source.code());
        return sourceComparison != 0 ? sourceComparison : name.compareTo(other.name);
    }
}
