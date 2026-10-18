package com.thedndpub.data.publication.spell;

import java.util.List;

public record SpellPublicationDecisionsDte(
        int schemaVersion,
        String headEventSha256,
        List<SpellPublicationDecisionEventDte> events) {
    public SpellPublicationDecisionsDte {
        events = List.copyOf(events);
    }
}
