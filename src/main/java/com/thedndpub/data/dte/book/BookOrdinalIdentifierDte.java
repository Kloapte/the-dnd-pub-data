package com.thedndpub.data.dte.book;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = BookOrdinalIdentifierDeserializer.class)
public sealed interface BookOrdinalIdentifierDte permits NumericBookOrdinalIdentifierDte,
        TextBookOrdinalIdentifierDte {
    String value();
}
