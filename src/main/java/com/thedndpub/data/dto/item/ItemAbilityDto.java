package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemAbilityDto(
        Integer strength,
        Integer dexterity,
        Integer constitution,
        Integer intelligence,
        Integer wisdom,
        Integer charisma,
        List<String> from,
        Integer count,
        Integer amount,
        List<ItemAbilityChoiceDto> choose,
        ItemAbilityScoresDto staticScores) {
    public ItemAbilityDto {
        from = List.copyOf(from);
        choose = List.copyOf(choose);
    }
}
