package com.thedndpub.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ToolProficiencyType {
    POISONERS_KIT("Poisoner's kit"),
    TINKERS_TOOLS("Tinker's tools"),
    THIEVES_TOOLS("Thieves' tools"),
    NAVIGATORS_TOOLS("Navigator's tools"),
    SMITHS_TOOLS("Smith's tools"),
    BREWERS_SUPPLIES("Brewer's supplies"),
    MASONS_TOOLS("Mason's tools"),

    ANY("Any"),
    ANY_MUSICAL_INSTRUMENT("Any musical instrument"),
    ANY_ARTISAN("Any artisan"),

    UNKNOWN("Unknown");

    private final String description;
    @Setter
    private boolean choice;

    ToolProficiencyType(String description) {
        this.description = description;
    }

    public static ToolProficiencyType fromString(String input) {
        try {
            return ToolProficiencyType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Tool Proficiency: " + input);
            return ToolProficiencyType.UNKNOWN;
        }
    }
}
