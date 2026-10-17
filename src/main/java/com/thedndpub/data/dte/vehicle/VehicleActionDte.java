package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

@JsonDeserialize(using = VehicleActionDeserializer.class)
public sealed interface VehicleActionDte permits VehicleActionTextDte, VehicleActionObjectDte {
}
