package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsSourceRevisionDto(
        String id,
        String datasetVersion,
        String resourcePath,
        String resourceSha256) {
    public MechanicsSourceRevisionDto {
        ModelChecks.text(id, "id");
        ModelChecks.text(datasetVersion, "datasetVersion");
        ModelChecks.resource(resourcePath);
        ModelChecks.digest(resourceSha256);
    }
}
