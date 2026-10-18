package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SourceArtifactRevision(
        String datasetVersion,
        String resourcePath,
        String resourceSha256) {
    public SourceArtifactRevision {
        ModelChecks.text(datasetVersion, "datasetVersion");
        ModelChecks.resource(resourcePath);
        ModelChecks.digest(resourceSha256);
    }
}
