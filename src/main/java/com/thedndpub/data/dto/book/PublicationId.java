package com.thedndpub.data.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PublicationId(String value) {
}
