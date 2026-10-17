package com.thedndpub.data.dto.vehicle;

import com.thedndpub.data.dte.vehicle.VehicleArmorClassDte;
import com.thedndpub.data.dte.vehicle.VehicleDetailedArmorClassDte;
import com.thedndpub.data.dte.vehicle.VehicleDetailedHitPointsDte;
import com.thedndpub.data.dte.vehicle.VehicleDetailedPaceDte;
import com.thedndpub.data.dte.vehicle.VehicleDetailedSpeedDte;
import com.thedndpub.data.dte.vehicle.VehicleHitPointsDte;
import com.thedndpub.data.dte.vehicle.VehicleHullDte;
import com.thedndpub.data.dte.vehicle.VehicleNumberDte;
import com.thedndpub.data.dte.vehicle.VehicleNumberOrTextDte;
import com.thedndpub.data.dte.vehicle.VehicleNumericArmorClassDte;
import com.thedndpub.data.dte.vehicle.VehicleNumericHitPointsDte;
import com.thedndpub.data.dte.vehicle.VehicleNumericPaceDte;
import com.thedndpub.data.dte.vehicle.VehicleNumericSpeedDte;
import com.thedndpub.data.dte.vehicle.VehiclePaceDte;
import com.thedndpub.data.dte.vehicle.VehicleSpeedDte;
import com.thedndpub.data.dte.vehicle.VehicleSpeedModeDte;
import com.thedndpub.data.dte.vehicle.VehicleConditionalSpeedModeDte;
import com.thedndpub.data.dte.vehicle.VehicleNumericSpeedModeDte;

import java.util.List;

final class VehicleDtoProjection {
    private VehicleDtoProjection() {
    }

    static VehicleArmorClassDto armorClass(VehicleArmorClassDte value) {
        if (value instanceof VehicleNumericArmorClassDte numeric) {
            return new VehicleArmorClassDto(numeric.value(), List.of());
        }
        if (value instanceof VehicleDetailedArmorClassDte detailed) {
            List<VehicleArmorClassComponentDto> components = detailed.values().stream()
                    .map(component -> new VehicleArmorClassComponentDto(component.ac(), component.from()))
                    .toList();
            return new VehicleArmorClassDto(null, components);
        }
        return null;
    }

    static VehicleHitPointsDto hitPoints(VehicleHitPointsDte value) {
        if (value instanceof VehicleNumericHitPointsDte numeric) {
            return new VehicleHitPointsDto(numeric.value(), null, null, null, null, null);
        }
        if (value instanceof VehicleDetailedHitPointsDte detailed) {
            return new VehicleHitPointsDto(null, detailed.average(), detailed.formula(), detailed.hp(), detailed.dt(),
                    detailed.mt());
        }
        return null;
    }

    static VehicleSpeedDto speed(VehicleSpeedDte value) {
        if (value instanceof VehicleNumericSpeedDte numeric) {
            return new VehicleSpeedDto(numeric.value(), null, null, null, null);
        }
        if (value instanceof VehicleDetailedSpeedDte detailed) {
            return new VehicleSpeedDto(null, detailed.walk(), speedMode(detailed.fly()), speedMode(detailed.swim()),
                    detailed.canHover());
        }
        return null;
    }

    static VehiclePaceDto pace(VehiclePaceDte value) {
        if (value instanceof VehicleNumericPaceDte numeric) {
            return new VehiclePaceDto(numeric.value(), null, null, null);
        }
        if (value instanceof VehicleDetailedPaceDte detailed) {
            return new VehiclePaceDto(
                    null,
                    display(detailed.fly()),
                    display(detailed.swim()),
                    display(detailed.walk()));
        }
        return null;
    }

    static VehicleHullDto hull(VehicleHullDte value) {
        if (value == null) {
            return null;
        }
        return new VehicleHullDto(value.ac(), value.acFrom(), value.hp(), value.dt());
    }

    static VehicleCapacityDto capacity(VehicleNumberOrTextDte value) {
        if (value instanceof VehicleNumberDte number) {
            return new VehicleCapacityDto(number.value(), null);
        }
        if (value instanceof com.thedndpub.data.dte.vehicle.VehicleTextDte text) {
            return new VehicleCapacityDto(null, text.value());
        }
        return null;
    }

    static VehicleActionThresholdsDto thresholds(
            com.thedndpub.data.dte.vehicle.VehicleActionThresholdsDte value) {
        if (value == null) {
            return null;
        }
        return new VehicleActionThresholdsDto(value.zero(), value.one(), value.two(), value.three());
    }

    private static VehicleSpeedModeDto speedMode(VehicleSpeedModeDte value) {
        if (value instanceof VehicleNumericSpeedModeDte numeric) {
            return new VehicleSpeedModeDto(numeric.value());
        }
        if (value instanceof VehicleConditionalSpeedModeDte conditional) {
            return new VehicleSpeedModeDto(conditional.number());
        }
        return null;
    }

    private static String display(VehicleNumberOrTextDte value) {
        if (value instanceof VehicleNumberDte number) {
            return number.value().toPlainString();
        }
        return value == null ? null : value.displayValue();
    }
}
