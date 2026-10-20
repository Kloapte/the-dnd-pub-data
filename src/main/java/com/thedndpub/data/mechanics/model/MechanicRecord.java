package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.review.MechanicsReviewState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** An immutable proposed claim, never an executable effect or a publication authorization. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicRecord(
        MechanicId id,
        MechanicRevision revision,
        MechanicPayload payload,
        MechanicContext context,
        List<MechanicEvidenceAssociation> evidenceAssociations,
        MechanicDerivation derivation,
        MechanicsAssessment assessment,
        MechanicsReviewState review,
        MechanicsLicensing licensing) {
    public MechanicRecord {
        ModelChecks.required(revision, "revision");
        evidenceAssociations = ModelChecks.list(evidenceAssociations);
        ModelChecks.require(!evidenceAssociations.isEmpty(), "Interpreted claims require source evidence");
        ModelChecks.require(assessment != null && assessment.completeness() != MechanicsCompleteness.UNINTERPRETED,
                "Uninterpreted syntax belongs in observations or unsupported constructs");
        MechanicRevisionContent content = new MechanicRevisionContent(
                id, payload, context, evidenceAssociations, derivation, assessment);
        ModelChecks.require(MechanicsHashes.claimHash(content).equals(revision.contentSha256()),
                "Claim revision fingerprint does not bind its actual content");
        MechanicRevisionReference reference = revision.reference(id);
        var paths = MechanicsClaimPaths.paths(content);
        var evidenceIds = new HashSet<String>();
        for (MechanicEvidenceAssociation association : evidenceAssociations) {
            ModelChecks.require(paths.contains(association.claimPath()), "Evidence association targets a nonexistent claim path");
            evidenceIds.add(association.evidenceId());
        }
        for (MechanicContextBinding binding : context.bindings()) {
            ModelChecks.require(evidenceIds.containsAll(MechanicsBindings.evidenceIds(binding)),
                    "Context bindings require fingerprint-bound evidence associations");
        }
        review = review == null ? MechanicsReviewState.pending(reference) : review;
        ModelChecks.require(review.claim().equals(reference), "Review state addresses a different current claim revision");
        List<MechanicsFinding> findings = new ArrayList<>(MechanicsQuality.findings(content));
        if (review.humanDecision() != null
                && assessment.completeness() == MechanicsCompleteness.PARTIAL
                && !review.humanDecision().decision().acceptsPartialScope()) {
            MechanicsQuality.add(findings, MechanicsFindingCode.PARTIAL_SCOPE_NOT_ACCEPTED, "/payload");
        }
        review = review.withFindings(findings);
        licensing = licensing == null ? UnassessedMechanicsLicensing.pending() : licensing;
        if (!MechanicsLicensingScopes.matches(licensing, reference)) {
            licensing = new UnassessedMechanicsLicensing(
                    LicensingReviewReason.STALE_CLAIM_REVISION, List.of(), List.of(licensing));
        }
    }

    public static MechanicRecord propose(
            MechanicId id,
            MechanicPayload payload,
            MechanicContext context,
            List<MechanicEvidenceAssociation> associations,
            MechanicDerivation derivation,
            MechanicsAssessment assessment) {
        MechanicRevisionContent content = new MechanicRevisionContent(
                id, payload, context, associations, derivation, assessment);
        return new MechanicRecord(id, new MechanicRevision(1, MechanicsHashes.claimHash(content), null),
                payload, context, associations, derivation, assessment, null, null);
    }

    /** Creates a pending revision linked to a caller-verified historical pending claim. */
    public static MechanicRecord proposeSupersedingPending(
            MechanicId id,
            MechanicRevisionReference predecessor,
            MechanicPayload payload,
            MechanicContext context,
            List<MechanicEvidenceAssociation> associations,
            MechanicDerivation derivation,
            MechanicsAssessment assessment) {
        ModelChecks.require(predecessor != null && predecessor.id().equals(id),
                "A superseding claim requires its own prior revision reference");
        MechanicRevisionContent content = new MechanicRevisionContent(
                id, payload, context, associations, derivation, assessment);
        MechanicRevision revision = new MechanicRevision(predecessor.number() + 1,
                MechanicsHashes.claimHash(content), predecessor);
        return new MechanicRecord(id, revision, payload, context, associations, derivation,
                assessment, null, null);
    }

    public MechanicRecord withReviewFindings(List<MechanicsFinding> findings) {
        return new MechanicRecord(id, revision, payload, context, evidenceAssociations, derivation,
                assessment, review.withFindings(findings), licensing);
    }

}
