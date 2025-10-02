package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbilityDto {
    private List<ChoiceDto> choices;
    private Integer dexterity;
    private Integer wisdom;
    private Integer charisma;
    private Integer strength;
    private Integer constitution;
    private Integer intelligence;
}