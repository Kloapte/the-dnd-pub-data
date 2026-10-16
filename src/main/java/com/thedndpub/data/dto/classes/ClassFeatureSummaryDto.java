package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureSummaryDto(String id, String name, SourceReference source, String parentClassId,
        int level, Integer order, Boolean variant) {
}
