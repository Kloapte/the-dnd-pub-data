package com.thedndpub.data.dto.race;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum SkillProficiencyType {
    ACROBATICS("Acrobatics"),
    PERCEPTION("Perception"),
    INTIMIDATION("Intimidation"),
    STEALTH("Stealth"),
    ATHLETICS("Athletics"),
    PERFORMANCE("Performance"),
    PERSUASION("Persuasion"),
    SURVIVAL("Survival"),
    HISTORY("History"),
    NATURE("Nature"),
    DECEPTION("Deception"),
    ANIMAL_HANDLING("Animal handling"),
    SLEIGHT_OF_HAND("Sleight of hand"),
    MEDICINE("Medicine"),
    INSIGHT("Insight"),
    INVESTIGATION("Investigation"),
    ARCANA("Arcana"),

    UNKNOWN("Unknown");

    private final String description;
    @Setter
    private boolean choice;

    SkillProficiencyType(String description) {
        this.description = description;
    }

    public static SkillProficiencyType fromString(String input) {
        try {
            return SkillProficiencyType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Skill Proficiency: " + input);
            return SkillProficiencyType.UNKNOWN;
        }
    }
}
