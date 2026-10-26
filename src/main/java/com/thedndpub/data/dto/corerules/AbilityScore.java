package com.thedndpub.data.dto.corerules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AbilityScore {
    STRENGTH("strength"),
    DEXTERITY("dexterity"),
    CONSTITUTION("constitution"),
    INTELLIGENCE("intelligence"),
    WISDOM("wisdom"),
    CHARISMA("charisma");

    private final String canonicalValue;

    AbilityScore(String canonicalValue) {
        this.canonicalValue = canonicalValue;
    }

    @JsonValue
    public String canonicalValue() {
        return canonicalValue;
    }

    @JsonCreator
    public static AbilityScore fromCanonicalValue(String value) {
        for (AbilityScore ability : values()) {
            if (ability.canonicalValue.equals(value)) return ability;
        }
        throw new IllegalArgumentException("Unknown canonical ability score: " + value);
    }
}
