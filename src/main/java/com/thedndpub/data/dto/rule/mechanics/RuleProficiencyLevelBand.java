package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyLevelBand(Integer minimumLevel, Integer maximumLevel, Integer bonus) {
    public RuleProficiencyLevelBand {
        if (minimumLevel == null || minimumLevel < 1 || minimumLevel > 30
                || maximumLevel == null || maximumLevel < 1 || maximumLevel > 30
                || bonus == null || bonus < 2 || bonus > 9) {
            throw new IllegalArgumentException("Proficiency level band values are outside the schema constraints");
        }
    }
}
