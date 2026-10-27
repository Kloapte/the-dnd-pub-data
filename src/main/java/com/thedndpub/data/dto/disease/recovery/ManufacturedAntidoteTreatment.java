package com.thedndpub.data.dto.disease.recovery;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ManufacturedAntidoteTreatment(
        String kind,
        String action,
        String outcome,
        Boolean onlyCureMethod) implements DiseaseTreatment {
}
