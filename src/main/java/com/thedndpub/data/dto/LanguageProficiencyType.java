package com.thedndpub.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum LanguageProficiencyType {
    COMMON("Common", true),
    OTHER("Other", false),
    AURAN("Auran", false),
    CELESTIAL("Celestial", false),
    GOBLIN("Goblin", true),
    SYLVAN("Sylvan", false),
    DRACONIC("Draconic", false),
    DWARVISH("Dwarvish", true),
    ELVISH("Elvish", true),
    GIANT("Giant", true),
    PRIMORDIAL("Primordial", false),
    GNOMISH("Gnomish", true),
    TERRAN("Terran", false),
    UNDERCOMMON("Undercommon", false),
    ORC("Orc", true),
    HALFLING("Halfling", true),
    AQUAN("Aquan", false),
    INFERNAL("Infernal", false),
    ABYSSAL("Abyssal", false),

    UNKNOWN("Unknown", false);

    private final String description;
    private final boolean standard;
    @Setter
    private boolean choice;

    LanguageProficiencyType(String description, boolean standard) {
        this.description = description;
        this.standard = standard;
    }

    public static LanguageProficiencyType fromString(String input) {
        try {
            return LanguageProficiencyType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Language Proficiency: " + input);
            return LanguageProficiencyType.UNKNOWN;
        }
    }
}
