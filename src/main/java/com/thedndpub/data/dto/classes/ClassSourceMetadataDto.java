package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSourceMetadataDto(List<SourceReference> otherSources, List<ClassReprintDto> reprintedAs) {
    public ClassSourceMetadataDto {
        otherSources = otherSources == null ? List.of() : List.copyOf(otherSources);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
