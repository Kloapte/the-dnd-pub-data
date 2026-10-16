package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SubclassFeatureSummaryDto(String id, String name, SourceReference source, String parentClassId,
        String parentSubclassId, int level, Integer order, Boolean variant) {
}
