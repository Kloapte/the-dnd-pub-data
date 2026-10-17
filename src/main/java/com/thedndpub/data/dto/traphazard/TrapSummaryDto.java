package com.thedndpub.data.dto.traphazard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TrapSummaryDto(TrapId id, String name, SourceReference source, String type, List<TrapRatingDto> rating) {
    public TrapSummaryDto {
        rating = rating == null ? List.of() : List.copyOf(rating);
    }

    public static TrapSummaryDto from(TrapDefinition definition) {
        return new TrapSummaryDto(definition.id(), definition.name(), definition.source(), definition.type(),
                definition.rating());
    }
}
