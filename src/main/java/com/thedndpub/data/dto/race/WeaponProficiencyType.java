package com.thedndpub.data.dto.race;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum WeaponProficiencyType {
    LIGHT_HAMMER("Light hammer"),
    FIREARMS("Firearms"),
    BATTLEAXE("Battleaxe"),
    HANDAXE("Handaxe"),
    WARHAMMER("Warhammer"),
    LONGSWORD("Longsword"),
    SHORTSWORD("Shortsword"),
    SHORTBOW("Shortbow"),
    LONGBOW("Longbow"),
    SPEAR("Spear"),
    JAVELIN("Javelin"),
    RAPIER("Rapier"),
    HAND_CROSSBOW("Hand crossbow"),
    LIGHT_CROSSBOW("Light crossbow"),
    TRIDENT("Trident"),
    NET("Net"),
    GREATSWORD("Greatsword"),

    MARTIAL_WEAPON("Martial weapon"),
    MUNDANE("Mundane"),

    UNKNOWN("Unknown");

    private final String description;
    @Setter
    private boolean choice;

    WeaponProficiencyType(String description) {
        this.description = description;
    }

    public static WeaponProficiencyType fromString(String input) {
        try {
            return WeaponProficiencyType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Weapon Proficiency: " + input);
            return WeaponProficiencyType.UNKNOWN;
        }
    }
}
