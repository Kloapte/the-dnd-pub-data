package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RaceDto {
    private String name;
    private SourceType source;
    private String sourceDescription;
    private VisionDto vision;
    private MovementDto movement;
    private SizeDto size;
    private AbilityDto ability;
    private WeaponProficiencyDto weaponProficiencies;
}
