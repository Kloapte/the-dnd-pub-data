package com.thedndpub.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ArmorProficiencyType {
    LIGHT("Common"),
    MEDIUM("Other"),
    HEAVY("Auran"),

    UNKNOWN("Unknown");

    private final String description;

    ArmorProficiencyType(String description) {
        this.description = description;
    }

    public static ArmorProficiencyType fromString(String input) {
        try {
            return ArmorProficiencyType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Armor Proficiency: " + input);
            return ArmorProficiencyType.UNKNOWN;
        }
    }
}
