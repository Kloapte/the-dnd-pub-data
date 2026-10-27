package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseInterval(Double amount, String kind, Integer count, Integer die, String unit) {
}
