package com.thedndpub.data.dto.charoption;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CharacterCreationOptionDto(
        CharacterOptionId id,
        String name,
        SourceReference source,
        List<String> optionTypes,
        List<CharacterOptionRacePrerequisiteDto> prerequisites,
        List<CharacterOptionReprintDto> reprintedAs,
        List<SourceReference> otherSources) {
    public CharacterCreationOptionDto {
        optionTypes = List.copyOf(optionTypes);
        prerequisites = List.copyOf(prerequisites);
        reprintedAs = List.copyOf(reprintedAs);
        otherSources = List.copyOf(otherSources);
    }

    public static CharacterCreationOptionDto from(CharacterOptionDefinition definition) {
        List<CharacterOptionRacePrerequisiteDto> publicPrerequisites = definition.prerequisites().stream()
                .filter(prerequisite -> !prerequisite.races().isEmpty())
                .map(prerequisite -> new CharacterOptionRacePrerequisiteDto(
                        prerequisite.races().stream().map(CharacterOptionRaceRequirement::name).toList()))
                .toList();
        return new CharacterCreationOptionDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.optionTypes().stream().map(CharacterOptionType::code).toList(),
                publicPrerequisites,
                definition.reprintedAs().stream().map(CharacterOptionReprintDto::from).toList(),
                definition.otherSources());
    }
}
