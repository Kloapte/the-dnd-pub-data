package com.thedndpub.data.dte.books;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.books.deserializers.AliasDeserializer;
import com.thedndpub.data.dte.generic.deserializers.LocalDateDeserializer;
import com.thedndpub.data.dte.items.deserializers.SrdDeserializer;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookDte {
    private String name;
    private String id;
    private String source;
    private String group;
    private CoverDte cover;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate published;
    private String author;
    List<ContentDte> contents;
    @JsonDeserialize(using = AliasDeserializer.class)
    private String alias;
}
