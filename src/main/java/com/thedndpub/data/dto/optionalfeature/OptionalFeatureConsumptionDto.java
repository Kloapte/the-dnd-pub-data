package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeatureConsumptionDto(String name, Integer amount, Integer amountMin, Integer amountMax) {
}
