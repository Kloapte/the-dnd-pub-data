package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = VehicleHitPointsDeserializer.class)
public sealed interface VehicleHitPointsDte permits VehicleNumericHitPointsDte, VehicleDetailedHitPointsDte {
}
