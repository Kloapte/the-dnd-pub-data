package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VehicleActionThresholdsDte(
        @JsonProperty("0") Integer zero,
        @JsonProperty("1") Integer one,
        @JsonProperty("2") Integer two,
        @JsonProperty("3") Integer three) {
}
