package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundItemReferenceDto(
        String uid,
        String name,
        String source,
        String itemId,
        Resolution resolution) {
    public enum Resolution {
        RESOLVED,
        UNRESOLVED,
        AMBIGUOUS
    }
}
