package com.thedndpub.data.dte.books;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.books.deserializers.HeaderDeserializer;
import com.thedndpub.data.dte.generic.deserializers.LocalDateDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class ContentDte {
    private String name;
    @JsonDeserialize(using = HeaderDeserializer.class)
    private List<String> headers;
    private OrdinalDte ordinal;
}
