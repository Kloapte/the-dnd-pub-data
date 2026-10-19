package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** A damage result on one roll outcome, attached to the affected DamageMechanic context. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AppliesOnSaveOutcome(
        MechanicRevisionReference savingThrow,
        SaveOutcome outcome,
        DamageOutcome damageOutcome,
        List<String> evidenceIds) implements MechanicContextBinding {
    public AppliesOnSaveOutcome {
        ModelChecks.required(savingThrow, "savingThrow");
        ModelChecks.required(outcome, "outcome");
        ModelChecks.required(damageOutcome, "damageOutcome");
        ModelChecks.require(outcome == SaveOutcome.FAILURE && damageOutcome == DamageOutcome.FULL_DAMAGE
                        || outcome == SaveOutcome.SUCCESS && (damageOutcome == DamageOutcome.HALF_DAMAGE
                        || damageOutcome == DamageOutcome.NO_DAMAGE),
                "Unsupported damage result for saving throw outcome");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Save outcome binding requires evidence");
    }

    public AppliesOnSaveOutcome(
            MechanicRevisionReference savingThrow,
            SaveOutcome outcome,
            List<String> evidenceIds) {
        this(savingThrow, outcome, legacyDamageOutcome(outcome), evidenceIds);
    }

    private static DamageOutcome legacyDamageOutcome(SaveOutcome outcome) {
        ModelChecks.require(outcome == SaveOutcome.FAILURE,
                "Successful saves require an explicit damage outcome");
        return DamageOutcome.FULL_DAMAGE;
    }
}
