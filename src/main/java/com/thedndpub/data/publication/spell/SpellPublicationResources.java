package com.thedndpub.data.publication.spell;

/** Parsed immutable runtime inputs and fingerprints of the actual resource bytes. */
public record SpellPublicationResources(
        SpellPublicationPolicyDte policy,
        SpellPublicationCandidatesDte candidates,
        String candidatesSha256,
        SpellPublicationEvidenceDte evidence,
        String evidenceSha256,
        SpellPublicationNoticesDte notices,
        String noticesSha256,
        SpellPublicationDecisionsDte decisions,
        String decisionsSha256) {
}
