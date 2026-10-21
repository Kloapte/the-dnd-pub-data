package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** A non-creature effect created by a spell and available only through explicit activations. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PersistentSpellEffectMechanic(
        DurationSpecification duration,
        boolean concentrationRequired,
        boolean independentlyActingCreature,
        boolean requiresActivation,
        Boolean endsWhenSpellIsCastAgain,
        HitGatedEffectEstablishment establishment,
        List<PersistentEffectTerminationCondition> terminationConditions) implements MechanicPayload {
    public PersistentSpellEffectMechanic(
            DurationSpecification duration,
            boolean concentrationRequired,
            boolean independentlyActingCreature,
            boolean requiresActivation,
            boolean endsWhenSpellIsCastAgain) {
        this(duration, concentrationRequired, independentlyActingCreature, requiresActivation,
                endsWhenSpellIsCastAgain, null, List.of());
    }

    public PersistentSpellEffectMechanic {
        ModelChecks.required(duration, "duration");
        terminationConditions = ModelChecks.list(terminationConditions);
        ModelChecks.require(terminationConditions.stream().distinct().count() == terminationConditions.size(),
                "Persistent effect termination conditions must be unique");
        ModelChecks.require(!independentlyActingCreature,
                "Persistent spell effects cannot be modeled as independently acting creatures");
        ModelChecks.require(requiresActivation, "Persistent spell-effect attacks require explicit activation");
    }
}
