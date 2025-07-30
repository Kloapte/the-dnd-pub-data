package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChoiceDto {
    private List<String> from;
    private Integer amountOfBonus;

    private static final List<String> ABILITIES = List.of("dexterity", "wisdom", "charisma", "strength", "constitution", "intelligence");

    public static ChoiceDto ANY_ABILITY_1() {
        ChoiceDto choice = new ChoiceDto();
        choice.setFrom(ABILITIES);
        choice.setAmountOfBonus(1);
        return choice;
    }

    public static ChoiceDto ANY_ABILITY_2() {
        ChoiceDto choice = new ChoiceDto();
        choice.setFrom(ABILITIES);
        choice.setAmountOfBonus(2);
        return choice;
    }
}
