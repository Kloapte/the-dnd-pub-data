package com.thedndpub.data.dto.adventure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AdventureSummaryDto(
        AdventureId id,
        String name,
        SourceIdentity source,
        String group,
        LocalDate published,
        AdventureLevelRange levelRange,
        String storyline) {
    public static AdventureSummaryDto from(AdventureDefinition definition) {
        return new AdventureSummaryDto(
                definition.id(),
                definition.name(),
                definition.source().identity(),
                definition.group(),
                definition.published(),
                definition.levelRange(),
                definition.storyline());
    }
}
