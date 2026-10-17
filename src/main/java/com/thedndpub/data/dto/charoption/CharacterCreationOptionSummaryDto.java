package com.thedndpub.data.dto.charoption;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CharacterCreationOptionSummaryDto(
        CharacterOptionId id,
        String name,
        SourceReference source,
        List<String> optionTypes) {
    public CharacterCreationOptionSummaryDto {
        optionTypes = List.copyOf(optionTypes);
    }

    public static CharacterCreationOptionSummaryDto from(CharacterOptionDefinition definition) {
        return new CharacterCreationOptionSummaryDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.optionTypes().stream().map(CharacterOptionType::code).toList());
    }
}
