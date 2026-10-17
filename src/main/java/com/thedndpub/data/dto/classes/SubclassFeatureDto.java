package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SubclassFeatureDto(String id, String name, SourceReference source, String parentClassId,
        String parentSubclassId, int level, Integer order, Boolean variant,
        ClassFeatureMechanicsDto mechanics, ClassSourceMetadataDto sources,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public SubclassFeatureDto {
        crossCatalogReferences = crossCatalogReferences == null ? List.of() : List.copyOf(crossCatalogReferences);
    }

    public SubclassFeatureDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new SubclassFeatureDto(id, name, source, parentClassId, parentSubclassId, level, order, variant,
                mechanics, sources, references);
    }
}
