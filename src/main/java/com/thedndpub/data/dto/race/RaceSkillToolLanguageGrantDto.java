package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceSkillToolLanguageGrantDto(List<RaceSkillToolLanguageChoiceDto> choose) {
    public RaceSkillToolLanguageGrantDto {
        choose = choose == null ? List.of() : List.copyOf(choose);
    }
}
