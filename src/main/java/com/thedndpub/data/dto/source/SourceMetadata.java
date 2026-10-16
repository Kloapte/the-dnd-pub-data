package com.thedndpub.data.dto.source;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Descriptive source metadata, separate from the source's publication record. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SourceMetadata(String kind, String title) {
}
