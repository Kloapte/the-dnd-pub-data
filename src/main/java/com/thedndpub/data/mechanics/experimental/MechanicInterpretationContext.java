package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;

import java.util.Map;

public record MechanicInterpretationContext(
        SpellId ownerId,
        SourceArtifactRevision sourceRevision,
        Map<String, SourceFragment> fragments,
        Map<String, String> evidenceIdsByFragment) {
    public MechanicInterpretationContext {
        ModelChecks.required(ownerId, "ownerId");
        ModelChecks.required(sourceRevision, "sourceRevision");
        fragments = Map.copyOf(fragments);
        evidenceIdsByFragment = Map.copyOf(evidenceIdsByFragment);
        ModelChecks.require(fragments.keySet().containsAll(evidenceIdsByFragment.keySet()),
                "Evidence fragment index contains an unknown source fragment");
    }
}
