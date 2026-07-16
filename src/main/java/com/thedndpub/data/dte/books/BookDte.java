package com.thedndpub.data.dte.books;

import com.thedndpub.data.dte.books.deserializers.AliasDeserializer;
import com.thedndpub.data.dte.generic.deserializers.LocalDateDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;
import java.util.List;

public record BookDte(
        String name,
        String id,
        String source,
        String group,
        CoverDte cover,

        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate published,

        String author,

        List<ContentDte> contents,

        @JsonDeserialize(using = AliasDeserializer.class)
        String alias
) {}
