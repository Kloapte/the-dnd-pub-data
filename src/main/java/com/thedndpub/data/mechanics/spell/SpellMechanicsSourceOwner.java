package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;

/** Actual authored root location, including owners with no indexed formula tags. */
public record SpellMechanicsSourceOwner(
        SpellId id,
        SourceReference source,
        SourceArtifactRevision revision,
        String jsonPointer) {
    public SpellMechanicsSourceOwner {
        ModelChecks.required(id, "id");
        ModelChecks.required(source, "source");
        ModelChecks.required(revision, "revision");
        ModelChecks.pointer(jsonPointer);
    }
}
