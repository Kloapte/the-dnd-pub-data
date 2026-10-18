package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArmorClassModifierMechanic.class, name = "ARMOR_CLASS_MODIFIER"),
        @JsonSubTypes.Type(value = ReactionActivationMechanic.class, name = "REACTION_ACTIVATION"),
        @JsonSubTypes.Type(value = EffectDurationMechanic.class, name = "EFFECT_DURATION"),
        @JsonSubTypes.Type(value = SpellDamagePreventionMechanic.class, name = "SPELL_DAMAGE_PREVENTION"),
        @JsonSubTypes.Type(value = DamageMechanic.class, name = "DAMAGE"),
        @JsonSubTypes.Type(value = HealingMechanic.class, name = "HEALING"),
        @JsonSubTypes.Type(value = SavingThrowRequirementMechanic.class, name = "SAVING_THROW_REQUIREMENT")
})
public sealed interface MechanicPayload permits
        ArmorClassModifierMechanic,
        ReactionActivationMechanic,
        EffectDurationMechanic,
        SpellDamagePreventionMechanic,
        DamageMechanic,
        HealingMechanic,
        SavingThrowRequirementMechanic {

}
