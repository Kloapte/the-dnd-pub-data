package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatSummaryDto(FeatId id, String name, SourceReference source, String category) {
    public static FeatSummaryDto from(FeatDefinition feat) {
        return new FeatSummaryDto(feat.id(), feat.name(), feat.source(), feat.category());
    }
}
