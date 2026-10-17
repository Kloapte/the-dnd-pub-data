package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = VehicleNumberOrTextDeserializer.class)
public sealed interface VehicleNumberOrTextDte permits VehicleNumberDte, VehicleTextDte {
    String displayValue();
}
