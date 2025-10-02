package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolProficiencyDto {
    private List<ToolProficiencyType> proficiencies;
    private List<ChoiceDto> choices;
}
