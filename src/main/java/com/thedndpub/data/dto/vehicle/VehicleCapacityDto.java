package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleCapacityDto(BigDecimal value, String authoredText) {
}
