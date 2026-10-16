package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseSummaryDto(DiseaseId id, String name, SourceIdentity source) {
    public static DiseaseSummaryDto from(DiseaseDefinition definition) {
        return new DiseaseSummaryDto(definition.id(), definition.name(), definition.source().identity());
    }
}
