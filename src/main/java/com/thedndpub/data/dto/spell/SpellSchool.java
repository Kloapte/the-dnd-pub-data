package com.thedndpub.data.dto.spell;

import java.util.Arrays;
import java.util.Locale;

/** Authoritative mapping between 5eTools school codes and readable school names. */
public enum SpellSchool {
    ABJURATION("A", "Abjuration"),
    CONJURATION("C", "Conjuration"),
    DIVINATION("D", "Divination"),
    ENCHANTMENT("E", "Enchantment"),
    ILLUSION("I", "Illusion"),
    NECROMANCY("N", "Necromancy"),
    TRANSMUTATION("T", "Transmutation"),
    EVOCATION("V", "Evocation");

    private final String code;
    private final String displayName;

    SpellSchool(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String code() {
        return code;
    }

    public String displayName() {
        return displayName;
    }

    public static SpellSchool fromCode(String code) {
        return Arrays.stream(values())
                .filter(school -> school.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown spell school code '" + code + "'"));
    }

    public static SpellSchool fromCodeOrName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Spell school filter must not be blank");
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(school -> school.code.toLowerCase(Locale.ROOT).equals(normalized)
                        || school.displayName.toLowerCase(Locale.ROOT).equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown spell school '" + value + "'"));
    }
}
