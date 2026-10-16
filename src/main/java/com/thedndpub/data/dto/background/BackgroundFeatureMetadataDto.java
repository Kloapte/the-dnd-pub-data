package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundFeatureMetadataDto(String name, String entryType) {
}
