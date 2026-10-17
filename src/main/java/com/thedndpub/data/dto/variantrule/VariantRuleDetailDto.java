package com.thedndpub.data.dto.variantrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VariantRuleDetailDto(
        VariantRuleId id,
        String name,
        SourceReference source,
        String ruleType,
        String rootEntryType,
        Integer sectionCount,
        Integer tableCount,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public VariantRuleDetailDto {
        crossCatalogReferences = crossCatalogReferences == null
                ? List.of()
                : List.copyOf(crossCatalogReferences);
    }

    public static VariantRuleDetailDto from(VariantRuleDefinition definition) {
        return new VariantRuleDetailDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.ruleType(),
                definition.rootEntryType(),
                definition.sectionCount(),
                definition.tableCount(),
                List.of());
    }

    public VariantRuleDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new VariantRuleDetailDto(
                id, name, source, ruleType, rootEntryType, sectionCount, tableCount, references);
    }
}
