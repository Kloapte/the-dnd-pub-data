package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatReferenceDto(String name, SourceIdentity source) {
}
