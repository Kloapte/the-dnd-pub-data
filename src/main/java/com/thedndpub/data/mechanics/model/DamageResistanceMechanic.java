package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageResistanceMechanic(
        DamageProtectionOperation operation,
        DamageTypeSelection damageTypes,
        DamageApplicability applicability) implements MechanicPayload {
    public DamageResistanceMechanic {
        ModelChecks.required(operation, "operation");
        ModelChecks.required(damageTypes, "damageTypes");
        ModelChecks.required(applicability, "applicability");
        ModelChecks.require(!(damageTypes instanceof UnknownDamageType),
                "Unknown damage types must remain uninterpreted");
    }
}
