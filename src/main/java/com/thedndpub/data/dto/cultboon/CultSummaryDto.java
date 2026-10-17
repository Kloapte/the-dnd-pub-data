package com.thedndpub.data.dto.cultboon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CultSummaryDto(CultId id, String name, SourceReference source, String type) {
    public static CultSummaryDto from(CultDefinition definition) {
        return new CultSummaryDto(definition.id(), definition.name(), definition.source(), definition.type());
    }
}
