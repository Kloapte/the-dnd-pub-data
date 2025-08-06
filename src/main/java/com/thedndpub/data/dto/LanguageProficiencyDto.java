package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LanguageProficiencyDto {
    private List<LanguageProficiencyType> proficiencies;
    private List<ChoiceDto> choices;
}
