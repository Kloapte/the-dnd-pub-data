package com.thedndpub.data.dto.encounter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EncounterSummaryDto(EncounterId id, String name, SourceIdentity source, Integer page,
        int tableCount, int rowCount) {
    public static EncounterSummaryDto from(EncounterDefinition definition) {
        return new EncounterSummaryDto(definition.id(), definition.name(), definition.source().identity(),
                definition.source().page(), definition.tables().size(), definition.rowCount());
    }
}
