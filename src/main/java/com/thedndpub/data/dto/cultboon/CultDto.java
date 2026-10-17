package com.thedndpub.data.dto.cultboon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CultDto(
        CultId id,
        String name,
        SourceReference source,
        String type,
        List<SourceReference> otherSources,
        List<CultBoonReprintDto> reprintedAs,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public CultDto {
        otherSources = List.copyOf(otherSources);
        reprintedAs = List.copyOf(reprintedAs);
        crossCatalogReferences = List.copyOf(crossCatalogReferences);
    }

    public static CultDto from(
            CultDefinition definition,
            List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
        return new CultDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.type(),
                definition.otherSources(),
                definition.reprintedAs().stream().map(CultBoonReprintDto::from).toList(),
                crossCatalogReferences);
    }
}
