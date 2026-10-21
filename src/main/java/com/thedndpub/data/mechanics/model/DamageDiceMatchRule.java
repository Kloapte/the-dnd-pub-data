package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** A condition over individual die faces in one damage roll. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageDiceMatchRule(
        MechanicRevisionReference damage,
        int dieSides,
        int minimumMatchingDice,
        DamageDicePopulation population,
        ExternalDamageDiceTreatment externalDiceTreatment) {
    public DamageDiceMatchRule {
        ModelChecks.required(damage, "damage");
        ModelChecks.require(dieSides >= 2, "Damage dice require at least two sides");
        ModelChecks.require(minimumMatchingDice >= 2, "A matching-dice trigger requires two or more dice");
        ModelChecks.required(population, "population");
        ModelChecks.required(externalDiceTreatment, "externalDiceTreatment");
    }
}
