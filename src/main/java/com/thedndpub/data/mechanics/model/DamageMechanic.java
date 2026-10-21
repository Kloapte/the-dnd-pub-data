package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageMechanic(
        AmountExpression amount,
        DamageTypeSelection damageType,
        CharacterLevelDamageScaling characterLevelScaling,
        SpellSlotDamageScaling spellSlotScaling,
        DamageTiming timing,
        ActionActivatedDamage actionActivation) implements MechanicPayload {
    public DamageMechanic(
            AmountExpression amount,
            DamageTypeSelection damageType,
            CharacterLevelDamageScaling characterLevelScaling,
            SpellSlotDamageScaling spellSlotScaling,
            DamageTiming timing) {
        this(amount, damageType, characterLevelScaling, spellSlotScaling, timing, null);
    }

    public DamageMechanic(AmountExpression amount, DamageTypeSelection damageType) {
        this(amount, damageType, null, null, null, null);
    }

    public DamageMechanic(
            AmountExpression amount,
            DamageTypeSelection damageType,
            CharacterLevelDamageScaling characterLevelScaling) {
        this(amount, damageType, characterLevelScaling, null, null, null);
    }

    public DamageMechanic(
            AmountExpression amount,
            DamageTypeSelection damageType,
            CharacterLevelDamageScaling characterLevelScaling,
            SpellSlotDamageScaling spellSlotScaling) {
        this(amount, damageType, characterLevelScaling, spellSlotScaling, null, null);
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
            DiceAmount base = scalingBaseDice(amount);
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

    private static DiceAmount scalingBaseDice(AmountExpression amount) {
        if (amount instanceof DiceAmount dice) {
            return dice;
        }
        ModelChecks.require(amount instanceof SumAmount,
                "Spell-slot damage scaling requires a dice base amount");
        List<AmountExpression> terms = ((SumAmount) amount).terms();
        List<DiceAmount> diceTerms = terms.stream().filter(DiceAmount.class::isInstance)
                .map(DiceAmount.class::cast).toList();
        ModelChecks.require(diceTerms.size() == 1,
                "Spell-slot scaling requires exactly one dice term in a compositional amount");
        return diceTerms.getFirst();
    }
}
