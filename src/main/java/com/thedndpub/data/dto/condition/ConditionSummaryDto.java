package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionSummaryDto(ConditionId id, String name, SourceIdentity source) {
    public static ConditionSummaryDto from(ConditionDefinition definition) {
        return new ConditionSummaryDto(definition.id(), definition.name(), definition.source().identity());
    }
}
