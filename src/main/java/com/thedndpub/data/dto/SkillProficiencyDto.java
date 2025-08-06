package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dte.race.SkillProficiencyDte;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkillProficiencyDto {
    private List<SkillProficiencyType> proficiencies;
    private List<ChoiceDto> choices;
}
