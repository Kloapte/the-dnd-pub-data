package com.thedndpub.data.dte.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(using = ObjectActionDeserializer.class)
public record ObjectActionDte(String type, String name, List<ObjectActionContentDte> entries) {
    public ObjectActionDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
