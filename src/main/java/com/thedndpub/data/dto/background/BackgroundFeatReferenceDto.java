package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundFeatReferenceDto(
        String uid,
        String name,
        String source,
        String selection,
        String featId,
        String resolution) {
}
