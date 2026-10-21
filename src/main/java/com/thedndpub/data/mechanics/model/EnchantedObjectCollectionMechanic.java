package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** A finite set of pre-existing objects individually enchanted by a spell. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EnchantedObjectCollectionMechanic(
        EnchantedObjectCategory objectCategory,
        int maximumObjectCount,
        boolean objectsCreatedBySpell,
        AttackActivationTiming enchantmentActivationTiming,
        SpellCastingRangeKind spellCastingRange,
        DurationSpecification enchantmentDuration,
        EnchantedObjectUseConsumption useConsumption,
        boolean consumptionAppliesOnlyToUsedObject,
        boolean recastingEndsUnusedEnchantments) implements MechanicPayload {
    public EnchantedObjectCollectionMechanic {
        ModelChecks.required(objectCategory, "objectCategory");
        ModelChecks.require(maximumObjectCount > 0 && maximumObjectCount <= 3,
                "Magic Stone affects up to three objects");
        ModelChecks.require(!objectsCreatedBySpell,
                "Enchanted objects must not be represented as created by the spell");
        ModelChecks.require(enchantmentActivationTiming == AttackActivationTiming.BONUS_ACTION,
                "Magic Stone enchants objects when cast as a bonus action");
        ModelChecks.required(spellCastingRange, "spellCastingRange");
        ModelChecks.required(enchantmentDuration, "enchantmentDuration");
        ModelChecks.required(useConsumption, "useConsumption");
        ModelChecks.require(consumptionAppliesOnlyToUsedObject,
                "Using one enchanted object ends only that object's enchantment");
        ModelChecks.require(recastingEndsUnusedEnchantments,
                "Recasting Magic Stone ends enchantments on previously affected objects");
    }
}
