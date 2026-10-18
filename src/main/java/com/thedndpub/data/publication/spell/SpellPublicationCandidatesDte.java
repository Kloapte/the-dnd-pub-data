package com.thedndpub.data.publication.spell;

import java.util.List;

public record SpellPublicationCandidatesDte(int schemaVersion, List<SpellPublicationCandidateDte> candidates) {
    public SpellPublicationCandidatesDte {
        candidates = List.copyOf(candidates);
    }
}
