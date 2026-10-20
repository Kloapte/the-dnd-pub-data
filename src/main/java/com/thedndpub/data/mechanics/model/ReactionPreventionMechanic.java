package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** A source-backed prohibition on the affected subject taking reactions. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ReactionPreventionMechanic() implements MechanicPayload {
}
