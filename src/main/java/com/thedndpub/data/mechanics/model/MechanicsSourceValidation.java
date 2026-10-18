package com.thedndpub.data.mechanics.model;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.services.SourceRegistry;

/** Optional explicit integration check for adapters; model construction does not create registry identities. */
public final class MechanicsSourceValidation {
    private MechanicsSourceValidation() {
    }

    public static void validate(MechanicsDocument document, SourceRegistry registry) {
        registered(document.owner().source().identity(), registry);
        for (MechanicsEvidence evidence : document.evidence()) {
            registered(evidence.source().identity(), registry);
        }
    }

    private static void registered(SourceIdentity identity, SourceRegistry registry) {
        var source = registry.find(identity.code());
        ModelChecks.require(source != null && source.identity().equals(identity), "Unknown or noncanonical mechanics source");
    }
}
