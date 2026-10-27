package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record IngestTaintedMaterialExposure(String kind, String material, String event, String outcome)
        implements DiseaseExposure {
}
