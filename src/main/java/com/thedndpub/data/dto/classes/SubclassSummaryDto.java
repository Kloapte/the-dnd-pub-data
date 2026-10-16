package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SubclassSummaryDto(String id, String name, String shortName, SourceReference source,
        String parentClassId, ClassSourceMetadataDto sources) {
}
