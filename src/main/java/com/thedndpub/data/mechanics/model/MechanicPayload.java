package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArmorClassModifierMechanic.class, name = "ARMOR_CLASS_MODIFIER"),
        @JsonSubTypes.Type(value = ReactionActivationMechanic.class, name = "REACTION_ACTIVATION"),
        @JsonSubTypes.Type(value = EffectDurationMechanic.class, name = "EFFECT_DURATION"),
        @JsonSubTypes.Type(value = PersistentSpellEffectMechanic.class, name = "PERSISTENT_SPELL_EFFECT"),
        @JsonSubTypes.Type(value = EnchantedObjectCollectionMechanic.class, name = "ENCHANTED_OBJECT_COLLECTION"),
        @JsonSubTypes.Type(value = AttackTriggeredAreaEffectMechanic.class, name = "ATTACK_TRIGGERED_AREA_EFFECT"),
        @JsonSubTypes.Type(value = ConditionalAttackChainMechanic.class, name = "CONDITIONAL_ATTACK_CHAIN"),
        @JsonSubTypes.Type(value = SpellDamagePreventionMechanic.class, name = "SPELL_DAMAGE_PREVENTION"),
        @JsonSubTypes.Type(value = DamageMechanic.class, name = "DAMAGE"),
        @JsonSubTypes.Type(value = HealingMechanic.class, name = "HEALING"),
        @JsonSubTypes.Type(value = SavingThrowRequirementMechanic.class, name = "SAVING_THROW_REQUIREMENT"),
        @JsonSubTypes.Type(value = SpellSlotScalingMechanic.class, name = "SPELL_SLOT_SCALING"),
        @JsonSubTypes.Type(value = GenericScalingMechanic.class, name = "GENERIC_SCALING"),
        @JsonSubTypes.Type(value = ConditionChangeMechanic.class, name = "CONDITION_CHANGE"),
        @JsonSubTypes.Type(value = ConditionChoiceMechanic.class, name = "CONDITION_CHOICE"),
        @JsonSubTypes.Type(value = ConditionTerminationEventMechanic.class, name = "CONDITION_TERMINATION_EVENT"),
        @JsonSubTypes.Type(value = StatisticModifierMechanic.class, name = "STATISTIC_MODIFIER"),
        @JsonSubTypes.Type(value = DamageResistanceMechanic.class, name = "DAMAGE_RESISTANCE"),
        @JsonSubTypes.Type(value = AttackRollMechanic.class, name = "ATTACK_ROLL"),
        @JsonSubTypes.Type(value = HitPointRecoveryPreventionMechanic.class, name = "HIT_POINT_RECOVERY_PREVENTION"),
        @JsonSubTypes.Type(value = ReactionPreventionMechanic.class, name = "REACTION_PREVENTION"),
        @JsonSubTypes.Type(value = AttackRollModifierMechanic.class, name = "ATTACK_ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = DamageInstanceSeriesMechanic.class, name = "DAMAGE_INSTANCE_SERIES")
})
public sealed interface MechanicPayload permits
        ArmorClassModifierMechanic,
        ReactionActivationMechanic,
        EffectDurationMechanic,
        PersistentSpellEffectMechanic,
        EnchantedObjectCollectionMechanic,
        AttackTriggeredAreaEffectMechanic,
        ConditionalAttackChainMechanic,
        SpellDamagePreventionMechanic,
        DamageMechanic,
        HealingMechanic,
        SavingThrowRequirementMechanic,
        SpellSlotScalingMechanic,
        GenericScalingMechanic,
        ConditionChangeMechanic,
        ConditionChoiceMechanic,
        ConditionTerminationEventMechanic,
        StatisticModifierMechanic,
        DamageResistanceMechanic,
        AttackRollMechanic,
        HitPointRecoveryPreventionMechanic,
        ReactionPreventionMechanic,
        AttackRollModifierMechanic,
        DamageInstanceSeriesMechanic {

}
