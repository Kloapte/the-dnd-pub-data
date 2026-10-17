package com.thedndpub.data.dto.bastion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FacilityDetailDto(
        String id,
        String name,
        SourceReference source,
        String facilityType,
        Integer level,
        List<String> space,
        List<FacilityHireling> hirelings,
        List<String> orders,
        List<FacilityPrerequisiteDto> prerequisites,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public FacilityDetailDto {
        space = copy(space);
        hirelings = copy(hirelings);
        orders = copy(orders);
        prerequisites = copy(prerequisites);
        crossCatalogReferences = copy(crossCatalogReferences);
    }

    public static FacilityDetailDto from(FacilityDefinition definition) {
        return new FacilityDetailDto(definition.id(), definition.name(), definition.source(),
                definition.facilityType(), definition.level(), definition.space(), definition.hirelings(),
                definition.orders(), definition.prerequisites().stream().map(FacilityPrerequisiteDto::from).toList(),
                List.of());
    }

    public FacilityDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new FacilityDetailDto(id, name, source, facilityType, level, space, hirelings, orders,
                prerequisites, references);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
