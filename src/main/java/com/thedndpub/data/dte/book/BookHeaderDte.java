package com.thedndpub.data.dte.book;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = BookHeaderDeserializer.class)
public sealed interface BookHeaderDte permits TextBookHeaderDte, StructuredBookHeaderDte {
}
