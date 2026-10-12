package com.thedndpub.data.dto.monster.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AbilityScores(Integer strength, Integer dexterity, Integer constitution,
                            Integer intelligence, Integer wisdom, Integer charisma) {
}
