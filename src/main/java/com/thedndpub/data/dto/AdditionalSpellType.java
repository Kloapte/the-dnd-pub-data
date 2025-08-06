package com.thedndpub.data.dto;

import lombok.Getter;

@Getter
public enum AdditionalSpellType {
    KNOWN("Known"),
    INNATE("Innate"),

    UNKNOWN("Unknown");

    private final String description;

    AdditionalSpellType(String description) {
        this.description = description;
    }

    public static AdditionalSpellType fromString(String input) {
        try {
            return AdditionalSpellType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Additional Spell Type: " + input);
            return AdditionalSpellType.UNKNOWN;
        }
    }
}
