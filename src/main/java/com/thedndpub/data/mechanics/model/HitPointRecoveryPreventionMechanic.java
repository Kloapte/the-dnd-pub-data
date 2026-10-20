package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** A source-backed prohibition on the affected subject regaining hit points. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HitPointRecoveryPreventionMechanic() implements MechanicPayload {
}
