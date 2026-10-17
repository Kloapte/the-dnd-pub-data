package com.thedndpub.data.dto.traphazard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HazardDetailDto(
        HazardId id,
        String name,
        SourceReference source,
        String type,
        List<TrapRatingDto> rating,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public HazardDetailDto {
        rating = rating == null ? List.of() : List.copyOf(rating);
        crossCatalogReferences = crossCatalogReferences == null ? List.of() : List.copyOf(crossCatalogReferences);
    }

    public static HazardDetailDto from(HazardDefinition definition) {
        return new HazardDetailDto(definition.id(), definition.name(), definition.source(), definition.type(),
                definition.rating(), List.of());
    }

    public HazardDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new HazardDetailDto(id, name, source, type, rating, references);
    }
}
