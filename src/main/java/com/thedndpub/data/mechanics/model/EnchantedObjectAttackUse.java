package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Separates the user of an enchanted object from the spell caster who owns its magic. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EnchantedObjectAttackUse(
        MechanicRevisionReference enchantedObjectCollection,
        List<EnchantedObjectUserRole> permittedAttackActors,
        AttackModifierAttribution attackModifierAttribution,
        EnchantedObjectDelivery delivery) {
    public EnchantedObjectAttackUse {
        ModelChecks.required(enchantedObjectCollection, "enchantedObjectCollection");
        permittedAttackActors = ModelChecks.list(permittedAttackActors);
        ModelChecks.require(permittedAttackActors.equals(List.of(EnchantedObjectUserRole.SPELL_CASTER,
                        EnchantedObjectUserRole.OTHER_CREATURE)),
                "The caster or another creature may use an enchanted stone");
        ModelChecks.required(attackModifierAttribution, "attackModifierAttribution");
        ModelChecks.required(delivery, "delivery");
    }
}
