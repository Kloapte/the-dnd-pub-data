package com.thedndpub.data.publication.spell;

import java.util.List;

public record SpellPublicationNoticesDte(
        int schemaVersion,
        String attributionProposalSha256,
        SpellPublicationApprovalDte approval,
        List<SpellPublicationNoticeDte> notices) {
    public SpellPublicationNoticesDte {
        notices = List.copyOf(notices);
    }

    public SpellPublicationNoticesDte withoutApproval() {
        return new SpellPublicationNoticesDte(schemaVersion, attributionProposalSha256, null, notices);
    }
}
