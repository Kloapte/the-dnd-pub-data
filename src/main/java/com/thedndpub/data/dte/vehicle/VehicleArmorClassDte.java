package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = VehicleArmorClassDeserializer.class)
public sealed interface VehicleArmorClassDte permits VehicleNumericArmorClassDte, VehicleDetailedArmorClassDte {
}
