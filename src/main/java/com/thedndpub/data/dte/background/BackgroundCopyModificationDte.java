package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = BackgroundCopyModificationDeserializer.class)
public record BackgroundCopyModificationDte(java.util.List<BackgroundEntryModificationDte> operations) {
    public BackgroundCopyModificationDte {
        operations = java.util.List.copyOf(operations);
    }
}
