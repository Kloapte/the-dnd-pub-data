package com.thedndpub.data.publication.spell;

import java.util.Objects;

public record SpellPublicationDecisionEventDte(SpellPublicationDecisionDte decision, String sha256) {
    public SpellPublicationDecisionEventDte {
        Objects.requireNonNull(decision, "decision");
    }
}
