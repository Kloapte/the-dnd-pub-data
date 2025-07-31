package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RaceDto {
    private String name;
    private SourceDto source;
    private String sourceDescription;
    private CreatureTypeDto creatureType;
    private AgeDto age;
    private VisionDto vision;
    private MovementDto movement;
    private SizeDto size;
    private AbilityDto ability;
    private ModifiersDto modifiers;
    private ChoiceDto feat;
    private WeaponProficiencyDto weaponProficiencies;
    private ToolProficiencyDto toolProficiencies;
}
