package com.thedndpub.data.dto.race;

import lombok.Getter;

@Getter
public enum AbilityType {
    DEXTERITY("Dexterity"),
    WISDOM("Wisdom"),
    CHARISMA("Charisma"),
    STRENGTH("Strength"),
    CONSTITUTION("Constitution"),
    INTELLIGENCE("Intelligence"),

    UNKNOWN("Unknown");

    private final String description;

    AbilityType(String description) {
        this.description = description;
    }

    public static AbilityType fromString(String input) {
        try {
            return AbilityType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Ability Type: " + input);
            return AbilityType.UNKNOWN;
        }
    }
}
