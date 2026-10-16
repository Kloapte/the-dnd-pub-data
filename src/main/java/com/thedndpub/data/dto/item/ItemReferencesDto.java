package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemReferencesDto(
        List<String> classFeatures,
        List<String> optionalFeatures,
        List<String> vehicles,
        List<String> decks,
        List<String> aliases) {
    public ItemReferencesDto {
        classFeatures = List.copyOf(classFeatures);
        optionalFeatures = List.copyOf(optionalFeatures);
        vehicles = List.copyOf(vehicles);
        decks = List.copyOf(decks);
        aliases = List.copyOf(aliases);
    }
}
