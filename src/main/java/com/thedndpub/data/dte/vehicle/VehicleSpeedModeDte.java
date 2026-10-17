package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = VehicleSpeedModeDeserializer.class)
public sealed interface VehicleSpeedModeDte permits VehicleNumericSpeedModeDte, VehicleConditionalSpeedModeDte {
}
