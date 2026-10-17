package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = VehicleSpeedDeserializer.class)
public sealed interface VehicleSpeedDte permits VehicleNumericSpeedDte, VehicleDetailedSpeedDte {
}
