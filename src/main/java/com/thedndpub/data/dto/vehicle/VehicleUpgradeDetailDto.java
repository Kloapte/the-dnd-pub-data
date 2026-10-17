package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleUpgradeDetailDto(
        VehicleUpgradeId id,
        String name,
        SourceReference source,
        List<String> upgradeType,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public VehicleUpgradeDetailDto {
        upgradeType = upgradeType == null ? List.of() : List.copyOf(upgradeType);
        crossCatalogReferences = crossCatalogReferences == null ? List.of() : List.copyOf(crossCatalogReferences);
    }

    public static VehicleUpgradeDetailDto from(VehicleUpgradeDefinition definition) {
        return new VehicleUpgradeDetailDto(definition.id(), definition.name(), definition.source(),
                definition.upgradeType(), List.of());
    }

    public VehicleUpgradeDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new VehicleUpgradeDetailDto(id, name, source, upgradeType, references);
    }
}
