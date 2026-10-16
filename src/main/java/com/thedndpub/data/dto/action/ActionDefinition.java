package com.thedndpub.data.dto.action;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

public record ActionDefinition(
        ActionId id,
        String name,
        SourceReference source,
        List<ActionTiming> time,
        List<ActionReference> reprintedAs) {
    public ActionDefinition {
        time = List.copyOf(time);
        reprintedAs = List.copyOf(reprintedAs);
    }
}
