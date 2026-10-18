package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dto.spell.SpellId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DeclaredSpellDurationReference(
        SpellId spellId,
        @JsonProperty(required = true) int durationIndex,
        SourceArtifactRevision source) implements DurationSpecification {
    public DeclaredSpellDurationReference {
        ModelChecks.required(spellId, "spellId");
        ModelChecks.required(source, "source");
        ModelChecks.require(durationIndex >= 0, "Duration source index cannot be negative");
    }
}
