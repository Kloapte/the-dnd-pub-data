package com.thedndpub.data.dto.skill;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum SkillAbility {
    STRENGTH("str"),
    DEXTERITY("dex"),
    INTELLIGENCE("int"),
    WISDOM("wis"),
    CHARISMA("cha");

    private final String code;

    SkillAbility(String code) {
        this.code = code;
    }

    @JsonValue
    public String code() {
        return code;
    }

    public static SkillAbility fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Skill ability is required");
        }
        String normalizedCode = code.trim().toLowerCase(Locale.ROOT);
        for (SkillAbility ability : values()) {
            if (ability.code.equals(normalizedCode)) {
                return ability;
            }
        }
        throw new IllegalArgumentException("Unsupported skill ability code '" + code + "'");
    }
}
