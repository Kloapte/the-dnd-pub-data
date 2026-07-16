package com.thedndpub.data.dte.books;

import tools.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.books.deserializers.HeaderDeserializer;

import java.util.List;

public record ContentDte(
    String name,

    @JsonDeserialize(using = HeaderDeserializer.class)
    List<String> headers,

    OrdinalDte ordinal
){}
