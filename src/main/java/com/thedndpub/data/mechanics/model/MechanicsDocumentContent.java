package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.review.MechanicsReviewSummary;

import java.util.List;

/** Hash protocol v1 document payload; excludes its own revision to avoid self-reference. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsDocumentContent(
        MechanicsOwnerReference owner,
        List<SourceArtifactRevision> sources,
        MechanicsProcessing processing,
        List<MechanicRecord> interpretations,
        List<MechanicsObservationReference> observations,
        List<UnsupportedMechanicsConstruct> unsupported,
        List<MechanicsEvidence> evidence,
        MechanicsCoverage coverage,
        MechanicsReviewSummary reviewSummary) {
    public MechanicsDocumentContent {
        sources = ModelChecks.list(sources);
        interpretations = ModelChecks.list(interpretations);
        observations = ModelChecks.list(observations);
        unsupported = ModelChecks.list(unsupported);
        evidence = ModelChecks.list(evidence);
    }
}
