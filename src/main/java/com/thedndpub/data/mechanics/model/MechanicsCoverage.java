package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsCoverage(
        CoverageStatus status,
        String scopeId,
        String scopeVersion,
        List<String> consideredSourcePaths,
        List<String> unassessedSourcePaths,
        @JsonProperty(required = true) int unsupportedCount) {
    public MechanicsCoverage {
        ModelChecks.required(status, "status");
        consideredSourcePaths = ModelChecks.list(consideredSourcePaths);
        unassessedSourcePaths = ModelChecks.list(unassessedSourcePaths);
        consideredSourcePaths.forEach(ModelChecks::pointer);
        unassessedSourcePaths.forEach(ModelChecks::pointer);
        ModelChecks.require(unsupportedCount >= 0, "Unsupported count cannot be negative");
        if (status != CoverageStatus.NOT_ASSESSED) {
            ModelChecks.text(scopeId, "scopeId");
            ModelChecks.text(scopeVersion, "scopeVersion");
        }
        if (status == CoverageStatus.COMPLETE_WITHIN_SCOPE) {
            ModelChecks.require(unsupportedCount == 0 && unassessedSourcePaths.isEmpty(),
                    "A complete scope cannot omit sections or unsupported constructs");
        }
    }
}
