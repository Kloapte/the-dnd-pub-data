package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsBenchmarkReference(
        String suiteId,
        String fixtureId,
        String version,
        String resultSha256,
        @JsonProperty(required = true) boolean passed) {
    public MechanicsBenchmarkReference {
        ModelChecks.text(suiteId, "suiteId");
        ModelChecks.text(fixtureId, "fixtureId");
        ModelChecks.text(version, "version");
        ModelChecks.digest(resultSha256);
    }
}
