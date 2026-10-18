package com.thedndpub.data.publication.spell;

import java.util.List;

public record SpellPublicationPolicyDte(
        int schemaVersion,
        String datasetVersion,
        String scopeId,
        String scopeSha256,
        String candidateManifestSha256,
        String attributionProposalSha256,
        int candidateCount,
        String candidatesSha256,
        String evidenceSha256,
        String noticesSha256,
        String decisionsSha256,
        List<SpellPublicationAuthorityDte> authorities,
        SpellPublicationApprovalDte releaseApproval) {
    public SpellPublicationPolicyDte {
        authorities = List.copyOf(authorities);
    }

    public SpellPublicationPolicyDte withoutReleaseApproval() {
        return new SpellPublicationPolicyDte(
                schemaVersion, datasetVersion, scopeId, scopeSha256, candidateManifestSha256,
                attributionProposalSha256, candidateCount, candidatesSha256, evidenceSha256,
                noticesSha256, decisionsSha256, authorities, null);
    }
}
