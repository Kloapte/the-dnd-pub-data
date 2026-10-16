package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundSummaryDto(
        String id,
        String name,
        SourceReference source,
        String edition,
        List<String> skillProficiencies,
        List<BackgroundFeatReferenceDto> feats,
        List<BackgroundAbilityChoiceDto> abilityChoices) {
    public BackgroundSummaryDto {
        skillProficiencies = List.copyOf(skillProficiencies);
        feats = List.copyOf(feats);
        abilityChoices = List.copyOf(abilityChoices);
    }
}
