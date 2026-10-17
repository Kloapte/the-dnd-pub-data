package com.thedndpub.data.dto.adventure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AdventureDto(
        AdventureId id,
        String name,
        SourceReference source,
        String group,
        LocalDate published,
        String author,
        List<String> aliases,
        String parentSource,
        Integer publishedOrder,
        String storyline,
        AdventureLevelRange levelRange) {
    public AdventureDto {
        aliases = List.copyOf(aliases);
    }

    public static AdventureDto from(AdventureDefinition definition) {
        return new AdventureDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.group(),
                definition.published(),
                definition.author(),
                definition.aliases(),
                definition.parentSource(),
                definition.publishedOrder(),
                definition.storyline(),
                definition.levelRange());
    }
}
