package com.thedndpub.data.dto.traphazard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HazardSummaryDto(
        HazardId id, String name, SourceReference source, String type, List<TrapRatingDto> rating) {
    public HazardSummaryDto {
        rating = rating == null ? List.of() : List.copyOf(rating);
    }

    public static HazardSummaryDto from(HazardDefinition definition) {
        return new HazardSummaryDto(definition.id(), definition.name(), definition.source(), definition.type(),
                definition.rating());
    }
}
