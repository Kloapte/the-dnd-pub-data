package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureDto(String id, String name, SourceReference source, String parentClassId,
        int level, Integer order, Boolean variant, ClassFeatureMechanicsDto mechanics, ClassSourceMetadataDto sources,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public ClassFeatureDto {
        crossCatalogReferences = crossCatalogReferences == null ? List.of() : List.copyOf(crossCatalogReferences);
    }

    public ClassFeatureDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new ClassFeatureDto(id, name, source, parentClassId, level, order, variant, mechanics, sources,
                references);
    }
}
