package com.thedndpub.data.dto.sense;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseSummaryDto(SenseId id, String name, SourceReference source) {
    public static SenseSummaryDto from(SenseDefinition definition) {
        return new SenseSummaryDto(definition.id(), definition.name(), definition.source());
    }
}
