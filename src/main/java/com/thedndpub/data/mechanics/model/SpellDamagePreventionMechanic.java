package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellDamagePreventionMechanic(
        SpellMentionReference spell) implements MechanicPayload {
    public SpellDamagePreventionMechanic {
        ModelChecks.required(spell, "spell");
    }
}
