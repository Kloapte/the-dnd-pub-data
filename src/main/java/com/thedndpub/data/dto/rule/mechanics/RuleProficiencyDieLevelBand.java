package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleProficiencyDie;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyDieLevelBand(
        Integer minimumLevel,
        Integer maximumLevel,
        Integer proficiencyBonus,
        RuleProficiencyDie die) {
    public RuleProficiencyDieLevelBand {
        if (minimumLevel == null || minimumLevel < 1 || minimumLevel > 20
                || maximumLevel == null || maximumLevel < 1 || maximumLevel > 20
                || proficiencyBonus == null || proficiencyBonus < 2 || proficiencyBonus > 6) {
            throw new IllegalArgumentException("Proficiency die level band values are outside schema constraints");
        }
        Objects.requireNonNull(die, "Proficiency die level band requires die");
    }
}
