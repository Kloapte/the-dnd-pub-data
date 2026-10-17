package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = VehiclePaceDeserializer.class)
public sealed interface VehiclePaceDte permits VehicleNumericPaceDte, VehicleDetailedPaceDte {
}
