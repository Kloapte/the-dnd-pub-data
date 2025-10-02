package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeaponProficiencyDto {
    private List<WeaponProficiencyType> proficiencies;
    private List<ChoiceDto> choices;
}
