package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;

import java.util.List;

/** Experimental candidate projection; it is deliberately separate from canonical inspection claims. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalSpellMechanicsDto(
        String origin,
        String engineVersion,
        String datasetVersion,
        String sourceResourcePath,
        String sourceResourceSha256,
        ExperimentalMechanicsOutcome outcome,
        List<String> appliedRecognizers,
        List<MechanicInspectionDto> candidateClaims,
        List<UnsupportedMechanicsConstruct> unsupported,
        List<EvidenceLocatorDto> evidence,
        List<String> unaccountedSourcePaths,
        List<String> diagnostics,
        MechanicsCoverage coverage,
        ExperimentalMechanicsComparisonDto canonicalComparison,
        boolean reviewed,
        boolean publicationAuthorized) {
    public ExperimentalSpellMechanicsDto {
        ModelChecks.require("EXPERIMENTAL".equals(origin), "Experimental inspection origin must be explicit");
        ModelChecks.text(engineVersion, "engineVersion");
        ModelChecks.text(datasetVersion, "datasetVersion");
        ModelChecks.text(sourceResourcePath, "sourceResourcePath");
        ModelChecks.text(sourceResourceSha256, "sourceResourceSha256");
        ModelChecks.required(outcome, "outcome");
        appliedRecognizers = sorted(appliedRecognizers);
        candidateClaims = ModelChecks.list(candidateClaims);
        unsupported = ModelChecks.list(unsupported);
        evidence = ModelChecks.list(evidence);
        unaccountedSourcePaths = sorted(unaccountedSourcePaths);
        diagnostics = sorted(diagnostics);
        ModelChecks.required(coverage, "coverage");
        ModelChecks.required(canonicalComparison, "canonicalComparison");
        ModelChecks.require(!reviewed && !publicationAuthorized,
                "Experimental candidates cannot be reviewed or publication-authorized");
    }

    private static List<String> sorted(List<String> values) {
        return ModelChecks.list(values).stream().distinct().sorted().toList();
    }
}
