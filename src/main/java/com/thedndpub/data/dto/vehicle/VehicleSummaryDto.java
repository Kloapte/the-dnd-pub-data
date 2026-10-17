package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleSummaryDto(
        VehicleId id,
        String name,
        SourceReference source,
        String vehicleType,
        List<String> size,
        VehicleArmorClassDto armorClass,
        VehicleHitPointsDto hitPoints,
        VehicleSpeedDto speed) {
    public VehicleSummaryDto {
        size = size == null ? List.of() : List.copyOf(size);
    }

    public static VehicleSummaryDto from(VehicleDefinition definition) {
        var data = definition.mechanics();
        return new VehicleSummaryDto(definition.id(), definition.name(), definition.source(),
                definition.vehicleType(), data.size() == null ? List.of() : data.size().values(),
                VehicleDtoProjection.armorClass(data.armorClass()), VehicleDtoProjection.hitPoints(data.hitPoints()),
                VehicleDtoProjection.speed(data.speed()));
    }
}
