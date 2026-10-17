package com.thedndpub.data.dto.cultboon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BoonSummaryDto(BoonId id, String name, SourceReference source, String type) {
    public static BoonSummaryDto from(BoonDefinition definition) {
        return new BoonSummaryDto(definition.id(), definition.name(), definition.source(), definition.type());
    }
}
