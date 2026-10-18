package com.thedndpub.data.publication.spell;

/** Hashable decision payload. Metadata must come from a real, separately controlled human review. */
public record SpellPublicationDecisionDte(
        String eventId,
        int sequence,
        String occurrenceId,
        String candidateManifestSha256,
        String bindingSha256,
        String publicationScopeId,
        String attributionProposalSha256,
        SpellPublicationAction action,
        String reviewerId,
        String reviewerAuthorityRef,
        String rationale,
        String reviewedAt,
        String supersedesEventId,
        String previousEventSha256) {
}
