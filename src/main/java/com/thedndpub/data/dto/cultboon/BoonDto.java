package com.thedndpub.data.dto.cultboon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BoonDto(
        BoonId id,
        String name,
        SourceReference source,
        String type,
        List<CultBoonReprintDto> reprintedAs,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public BoonDto {
        reprintedAs = List.copyOf(reprintedAs);
        crossCatalogReferences = List.copyOf(crossCatalogReferences);
    }

    public static BoonDto from(
            BoonDefinition definition,
            List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
        return new BoonDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.type(),
                definition.reprintedAs().stream().map(CultBoonReprintDto::from).toList(),
                crossCatalogReferences);
    }
}
