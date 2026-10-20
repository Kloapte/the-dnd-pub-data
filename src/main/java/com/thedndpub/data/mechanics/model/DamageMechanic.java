package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageMechanic(
        AmountExpression amount,
        DamageTypeSelection damageType,
        CharacterLevelDamageScaling characterLevelScaling,
        SpellSlotDamageScaling spellSlotScaling,
        DamageTiming timing) implements MechanicPayload {
    public DamageMechanic(AmountExpression amount, DamageTypeSelection damageType) {
        this(amount, damageType, null, null, null);
    }

    public DamageMechanic(
            AmountExpression amount,
            DamageTypeSelection damageType,
            CharacterLevelDamageScaling characterLevelScaling) {
        this(amount, damageType, characterLevelScaling, null, null);
    }

    public DamageMechanic(
            AmountExpression amount,
            DamageTypeSelection damageType,
            CharacterLevelDamageScaling characterLevelScaling,
            SpellSlotDamageScaling spellSlotScaling) {
        this(amount, damageType, characterLevelScaling, spellSlotScaling, null);
    }

    public DamageMechanic {
        ModelChecks.required(amount, "amount");
        ModelChecks.required(damageType, "damageType");
        if (characterLevelScaling != null) {
            ModelChecks.require(amount instanceof DiceAmount,
                    "Character-level damage scaling requires a dice base amount");
            DiceAmount previous = (DiceAmount) amount;
            for (CharacterLevelDamageTier tier : characterLevelScaling.tiers()) {
                ModelChecks.require(tier.amount().sides() == previous.sides()
                                && tier.amount().modifier() == previous.modifier()
                                && tier.amount().count() > previous.count(),
                        "Character-level damage tiers must increase the same dice formula");
                previous = tier.amount();
            }
        }
        ModelChecks.require(characterLevelScaling == null || spellSlotScaling == null,
                "Damage cannot use character-level and spell-slot scaling together");
        if (spellSlotScaling != null) {
            ModelChecks.require(amount instanceof DiceAmount,
                    "Spell-slot damage scaling requires a dice base amount");
            DiceAmount base = (DiceAmount) amount;
            ModelChecks.require(spellSlotScaling.increment().sides() == base.sides()
                            && spellSlotScaling.increment().modifier() == 0,
                    "Spell-slot damage scaling must add dice matching the base die size");
            long maximumCount = (long) base.count()
                    + (long) spellSlotScaling.incrementsAtSlotLevel(9) * spellSlotScaling.increment().count();
            ModelChecks.require(maximumCount <= Integer.MAX_VALUE,
                    "Spell-slot damage progression overflows an integer");
        }
        if (amount instanceof FractionOfAmount) {
            ModelChecks.require(characterLevelScaling == null && spellSlotScaling == null,
                    "A fraction-of-amount inherits scaling from its base claim");
        }
    }
}
