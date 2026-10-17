package com.thedndpub.data.dto.bastion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FacilitySummaryDto(
        String id,
        String name,
        SourceReference source,
        String facilityType,
        Integer level,
        List<String> space) {
    public FacilitySummaryDto {
        space = space == null ? List.of() : List.copyOf(space);
    }

    public static FacilitySummaryDto from(FacilityDefinition definition) {
        return new FacilitySummaryDto(definition.id(), definition.name(), definition.source(),
                definition.facilityType(), definition.level(), definition.space());
    }
}
