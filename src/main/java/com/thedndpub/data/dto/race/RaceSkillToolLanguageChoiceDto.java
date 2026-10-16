package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceSkillToolLanguageChoiceDto(List<String> from, Integer count) {
    public RaceSkillToolLanguageChoiceDto {
        from = from == null ? List.of() : List.copyOf(from);
    }
}
