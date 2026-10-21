package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.review.MechanicsReviewSummary;

import java.util.List;

/** Fully joined canonical snapshot. Dedicated inspection DTOs project it without exposing the entire audit record. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsDocument(
        MechanicsOwnerReference owner,
        MechanicsDocumentRevision revision,
        List<SourceArtifactRevision> sources,
        MechanicsProcessing processing,
        List<MechanicRecord> interpretations,
        List<MechanicsObservationReference> observations,
        List<UnsupportedMechanicsConstruct> unsupported,
        List<MechanicsEvidence> evidence,
        MechanicsCoverage coverage,
        MechanicsReviewSummary reviewSummary) {
    public MechanicsDocument {
        ModelChecks.required(owner, "owner");
        ModelChecks.required(processing, "processing");
        ModelChecks.required(coverage, "coverage");
        sources = ModelChecks.list(sources);
        interpretations = ModelChecks.list(interpretations);
        observations = ModelChecks.list(observations);
        unsupported = ModelChecks.list(unsupported);
        evidence = ModelChecks.list(evidence);
        ModelChecks.require(!sources.isEmpty(), "A mechanics document requires pinned source revisions");
        interpretations = MechanicsDocumentValidator.prepare(owner, sources, processing, interpretations, observations,
                unsupported, evidence, coverage);
        MechanicsReviewSummary calculated = MechanicsReviewSummary.from(
                interpretations.stream().map(MechanicRecord::review).toList());
        ModelChecks.require(reviewSummary == null || reviewSummary.equals(calculated), "Review summary is not authoritative");
        reviewSummary = calculated;
        MechanicsDocumentContent content = new MechanicsDocumentContent(owner, sources, processing,
                interpretations, observations, unsupported, evidence, coverage, reviewSummary);
        ModelChecks.required(revision, "revision");
        ModelChecks.require(revision.contentSha256().equals(
                        MechanicsHashes.documentHash(content, revision.effectiveHashProtocolVersion())),
                "Document revision fingerprint mismatch");
    }

    public static MechanicsDocument create(
            MechanicsOwnerReference owner,
            long revisionNumber,
            List<SourceArtifactRevision> sources,
            MechanicsProcessing processing,
            List<MechanicRecord> interpretations,
            List<MechanicsObservationReference> observations,
            List<UnsupportedMechanicsConstruct> unsupported,
            List<MechanicsEvidence> evidence,
            MechanicsCoverage coverage) {
        ModelChecks.required(owner, "owner");
        ModelChecks.required(processing, "processing");
        ModelChecks.required(coverage, "coverage");
        sources = ModelChecks.list(sources);
        interpretations = ModelChecks.list(interpretations);
        observations = ModelChecks.list(observations);
        unsupported = ModelChecks.list(unsupported);
        evidence = ModelChecks.list(evidence);
        interpretations = MechanicsDocumentValidator.prepare(owner, sources, processing, interpretations,
                observations, unsupported, evidence, coverage);
        MechanicsReviewSummary summary = MechanicsReviewSummary.from(
                interpretations.stream().map(MechanicRecord::review).toList());
        MechanicsDocumentContent content = new MechanicsDocumentContent(owner, sources, processing,
                interpretations, observations, unsupported, evidence, coverage, summary);
        MechanicsDocumentRevision revision = new MechanicsDocumentRevision(
                revisionNumber, MechanicsHashes.documentHash(content), MechanicsHashes.DOCUMENT_HASH_PROTOCOL);
        return new MechanicsDocument(owner, revision,
                sources, processing, interpretations, observations, unsupported, evidence, coverage, summary);
    }
}
