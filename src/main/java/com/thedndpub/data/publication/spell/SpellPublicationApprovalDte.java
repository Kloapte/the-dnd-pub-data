package com.thedndpub.data.publication.spell;

/** Attestation over exact content. Trust comes from the protected deployment policy, not these strings alone. */
public record SpellPublicationApprovalDte(
        String reviewerId,
        String authorityReference,
        String rationale,
        String reviewedAt,
        String approvalRecordSha256,
        String contentSha256) {
}
