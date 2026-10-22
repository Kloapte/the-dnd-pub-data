package com.thedndpub.data.dto.source;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PublicationKind {
    @JsonProperty("book")
    BOOK,
    @JsonProperty("adventure")
    ADVENTURE
}
