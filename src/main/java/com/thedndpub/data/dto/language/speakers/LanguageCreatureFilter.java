package com.thedndpub.data.dto.language.speakers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.enums.LanguageSpeakerTargetType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageCreatureFilter(
        LanguageSpeakerTargetType targetType,
        List<String> creatureTypes,
        List<LanguageCreatureTag> tags,
        List<String> sources,
        LanguageCreatureTagSelector selector) {
    public LanguageCreatureFilter {
        creatureTypes = creatureTypes == null ? List.of() : List.copyOf(creatureTypes);
        tags = tags == null ? List.of() : List.copyOf(tags);
        sources = sources == null ? List.of() : List.copyOf(sources);
    }
}
