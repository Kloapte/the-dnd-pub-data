package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FormulaObservationReference(
        String id,
        SpellId spellId,
        @JsonProperty(required = true) int occurrenceIndex,
        SourceArtifactRevision source,
        SyntaxEvidence evidence) implements MechanicsObservationReference {
    public FormulaObservationReference {
        ModelChecks.text(id, "id");
        ModelChecks.required(spellId, "spellId");
        ModelChecks.required(source, "source");
        ModelChecks.required(evidence, "evidence");
        ModelChecks.require(occurrenceIndex >= 0, "Formula occurrence index cannot be negative");
        ModelChecks.require(source.resourcePath().equals(evidence.location().owner().resourcePath())
                && source.resourceSha256().equals(evidence.location().owner().resourceSha256()),
                "Formula reference source/evidence mismatch");
    }
}
