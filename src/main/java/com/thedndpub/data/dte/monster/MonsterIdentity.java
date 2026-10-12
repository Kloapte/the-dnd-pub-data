package com.thedndpub.data.dte.monster;

import java.util.Locale;

/**
 * Stable source identity used only while resolving 5eTools monster references.
 */
public record MonsterIdentity(String name, String source) implements Comparable<MonsterIdentity> {
    public MonsterIdentity {
        if (name == null || name.isBlank() || source == null || source.isBlank()) {
            throw new IllegalArgumentException("Monster identity requires nonblank name and source");
        }
    }

    public static MonsterIdentity from(String name, String source) {
        return new MonsterIdentity(name.trim().toLowerCase(Locale.ROOT), source.trim().toLowerCase(Locale.ROOT));
    }

    @Override
    public int compareTo(MonsterIdentity other) {
        int nameOrder = name.compareTo(other.name);
        return nameOrder != 0 ? nameOrder : source.compareTo(other.source);
    }
}
