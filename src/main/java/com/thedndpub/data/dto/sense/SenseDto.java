package com.thedndpub.data.dto.sense;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseDto(SenseId id, String name, SourceReference source) {
    public static SenseDto from(SenseDefinition definition) {
        return new SenseDto(definition.id(), definition.name(), definition.source());
    }
}
