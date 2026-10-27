package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RemoveSenseEffect.class, name = "REMOVE_SENSE"),
        @JsonSubTypes.Type(value = AutomaticCheckFailureEffect.class, name = "AUTOMATIC_CHECK_FAILURE"),
        @JsonSubTypes.Type(value = AttackRollModifierEffect.class, name = "ATTACK_ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = CheckRollModifierEffect.class, name = "CHECK_ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = MovementModifierEffect.class, name = "MOVEMENT_MODIFIER"),
        @JsonSubTypes.Type(value = HitPointMaxModifierEffect.class, name = "HIT_POINT_MAX_MODIFIER"),
        @JsonSubTypes.Type(value = FatalExhaustionThresholdEffect.class, name = "FATAL_EXHAUSTION_THRESHOLD"),
        @JsonSubTypes.Type(value = RestrictionEffect.class, name = "RESTRICTION"),
        @JsonSubTypes.Type(value = ActivityRestrictionEffect.class, name = "ACTIVITY_RESTRICTION"),
        @JsonSubTypes.Type(value = RelationalRestrictionEffect.class, name = "RELATIONAL_RESTRICTION"),
        @JsonSubTypes.Type(value = AutomaticSavingThrowFailureEffect.class, name = "AUTOMATIC_SAVING_THROW_FAILURE"),
        @JsonSubTypes.Type(value = ConditionInteractionEffect.class, name = "CONDITION_INTERACTION"),
        @JsonSubTypes.Type(value = MovementConstraintEffect.class, name = "MOVEMENT_CONSTRAINT"),
        @JsonSubTypes.Type(value = ConditionEndingEffect.class, name = "CONDITION_ENDING"),
        @JsonSubTypes.Type(value = GrappledMovementCostEffect.class, name = "GRAPPLED_MOVEMENT_COST"),
        @JsonSubTypes.Type(value = CriticalHitOnHitEffect.class, name = "CRITICAL_HIT_ON_HIT"),
        @JsonSubTypes.Type(value = PhysicalStateEffect.class, name = "PHYSICAL_STATE"),
        @JsonSubTypes.Type(value = DamageMitigationEffect.class, name = "DAMAGE_MITIGATION"),
        @JsonSubTypes.Type(value = DropHeldObjectsEffect.class, name = "DROP_HELD_OBJECTS"),
        @JsonSubTypes.Type(value = AwarenessRestrictionEffect.class, name = "AWARENESS_RESTRICTION"),
        @JsonSubTypes.Type(value = VisibilityCapabilityEffect.class, name = "VISIBILITY_CAPABILITY"),
        @JsonSubTypes.Type(value = CategoryImmunityEffect.class, name = "CATEGORY_IMMUNITY"),
        @JsonSubTypes.Type(value = SuspendActiveDiseasesEffect.class, name = "SUSPEND_ACTIVE_DISEASES")
})
public sealed interface ConditionEffect permits
        RemoveSenseEffect,
        AutomaticCheckFailureEffect,
        AttackRollModifierEffect,
        CheckRollModifierEffect,
        MovementModifierEffect,
        HitPointMaxModifierEffect,
        FatalExhaustionThresholdEffect,
        RestrictionEffect,
        ActivityRestrictionEffect,
        RelationalRestrictionEffect,
        AutomaticSavingThrowFailureEffect,
        ConditionInteractionEffect,
        MovementConstraintEffect,
        ConditionEndingEffect,
        GrappledMovementCostEffect,
        CriticalHitOnHitEffect,
        PhysicalStateEffect,
        DamageMitigationEffect,
        DropHeldObjectsEffect,
        AwarenessRestrictionEffect,
        VisibilityCapabilityEffect,
        CategoryImmunityEffect,
        SuspendActiveDiseasesEffect {

    ConditionEffectKind kind();
}
