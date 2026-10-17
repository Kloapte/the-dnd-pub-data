package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = VehicleStringOrListDeserializer.class)
public sealed interface VehicleStringOrListDte permits VehicleStringDte, VehicleStringListDte {
    List<String> values();
}
