package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** A condition effect whose source explicitly says it ends when its spell ends. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionEndsWithSpell(List<String> evidenceIds) implements MechanicContextBinding {
    public ConditionEndsWithSpell {
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Spell ending relationship requires evidence");
    }
}
