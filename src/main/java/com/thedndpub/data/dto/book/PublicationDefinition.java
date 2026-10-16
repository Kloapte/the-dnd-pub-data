package com.thedndpub.data.dto.book;

import java.time.LocalDate;
import java.util.List;

public record PublicationDefinition(
        PublicationId id,
        String name,
        String group,
        LocalDate published,
        LocalDate revised,
        String author,
        List<String> aliases,
        String parentSource) {
    public PublicationDefinition {
        aliases = List.copyOf(aliases);
    }
}
