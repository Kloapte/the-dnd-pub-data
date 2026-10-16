package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionSummaryDto(ActionId id, String name, SourceIdentity source) {
    public static ActionSummaryDto from(ActionDefinition definition) {
        return new ActionSummaryDto(definition.id(), definition.name(), definition.source().identity());
    }
}
