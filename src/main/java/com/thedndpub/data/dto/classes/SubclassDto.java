package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SubclassDto(String id, String name, String shortName, SourceReference source,
        String parentClassId, String edition, List<ClassFeatureReferenceDto> features,
        List<ClassProgressionTableDto> tables, List<ClassChoiceProgression> choices,
        ClassSpellcastingDto spellcasting, ClassSourceMetadataDto sources,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public SubclassDto {
        features = features == null ? List.of() : List.copyOf(features);
        tables = tables == null ? List.of() : List.copyOf(tables);
        choices = choices == null ? List.of() : List.copyOf(choices);
        crossCatalogReferences = crossCatalogReferences == null ? List.of() : List.copyOf(crossCatalogReferences);
    }

    public SubclassDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new SubclassDto(id, name, shortName, source, parentClassId, edition, features, tables, choices,
                spellcasting, sources, references);
    }
}
