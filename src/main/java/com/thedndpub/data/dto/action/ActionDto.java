package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionDto(
        ActionId id,
        String name,
        SourceReference source,
        List<ActionTiming> time,
        List<ActionReference> reprintedAs) {
    public ActionDto {
        time = List.copyOf(time);
        reprintedAs = List.copyOf(reprintedAs);
    }

    public static ActionDto from(ActionDefinition definition) {
        return new ActionDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.time(),
                definition.reprintedAs());
    }
}
