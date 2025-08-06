package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

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
    private SkillProficiencyDto skillProficiencies;
    private LanguageProficiencyDto languageProficiencies;
    private List<ArmorProficiencyType> armorProficiencies;
    private String soundPath;
    private List<AdditionalSpellDto> additionalSpells;
    private List<EntryDto> entries;
    private SourceDto copyOf;
}
