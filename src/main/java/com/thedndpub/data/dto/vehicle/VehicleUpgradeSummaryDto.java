package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleUpgradeSummaryDto(
        VehicleUpgradeId id,
        String name,
        SourceReference source,
        List<String> upgradeType) {
    public VehicleUpgradeSummaryDto {
        upgradeType = upgradeType == null ? List.of() : List.copyOf(upgradeType);
    }

    public static VehicleUpgradeSummaryDto from(VehicleUpgradeDefinition definition) {
        return new VehicleUpgradeSummaryDto(definition.id(), definition.name(), definition.source(),
                definition.upgradeType());
    }
}
