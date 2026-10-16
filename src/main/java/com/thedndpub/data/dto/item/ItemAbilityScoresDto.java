package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemAbilityScoresDto(Integer strength, Integer dexterity, Integer constitution,
                                   Integer intelligence, Integer wisdom, Integer charisma) {
}
