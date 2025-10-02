package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalSpellDto {
    private String name;
    private String subRaceName;
    private int level;
    private boolean rest;
    private boolean daily;
    private AdditionalSpellType type;
    private AbilityType ability;
    private List<ChoiceDto> abilityChoices;
    private List<ChoiceDto> spellChoices;
}
