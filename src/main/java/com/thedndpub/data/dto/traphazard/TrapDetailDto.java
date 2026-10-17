package com.thedndpub.data.dto.traphazard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TrapDetailDto(
        TrapId id,
        String name,
        SourceReference source,
        String type,
        List<TrapRatingDto> rating,
        Integer initiative,
        List<String> durationTypes,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public TrapDetailDto {
        rating = rating == null ? List.of() : List.copyOf(rating);
        durationTypes = durationTypes == null ? List.of() : List.copyOf(durationTypes);
        crossCatalogReferences = crossCatalogReferences == null ? List.of() : List.copyOf(crossCatalogReferences);
    }

    public static TrapDetailDto from(TrapDefinition definition) {
        return new TrapDetailDto(definition.id(), definition.name(), definition.source(), definition.type(),
                definition.rating(), definition.initiative(), definition.durationTypes(), List.of());
    }

    public TrapDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new TrapDetailDto(id, name, source, type, rating, initiative, durationTypes, references);
    }
}
