package com.thedndpub.data.publication.spell;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Immutable startup result, with one effective state per known review candidate. */
public final class SpellPublicationIndex {
    private final List<SpellPublicationCandidateDte> candidates;
    private final Map<String, SpellPublicationState> states;
    private final List<SpellPublicationAuthorization> authorized;

    SpellPublicationIndex(
            List<SpellPublicationCandidateDte> candidates,
            Map<String, SpellPublicationState> states,
            List<SpellPublicationAuthorization> authorized) {
        this.candidates = List.copyOf(candidates);
        this.states = Collections.unmodifiableMap(new LinkedHashMap<>(states));
        this.authorized = List.copyOf(authorized);
    }

    public List<SpellPublicationCandidateDte> candidates() {
        return candidates;
    }

    public Map<String, SpellPublicationState> states() {
        return states;
    }

    public List<SpellPublicationAuthorization> authorizedObservations() {
        return authorized;
    }

    public boolean isAuthorized(String occurrenceId) {
        return states.get(occurrenceId) == SpellPublicationState.AUTHORIZED;
    }
}
