package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AttackRollModifierEffect.class, name = "ATTACK_ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = AbilityScoreChangeEffect.class, name = "ABILITY_SCORE_CHANGE"),
        @JsonSubTypes.Type(value = DamageVulnerabilityEffect.class, name = "DAMAGE_VULNERABILITY"),
        @JsonSubTypes.Type(value = SpeedChangeEffect.class, name = "SPEED_CHANGE"),
        @JsonSubTypes.Type(value = DamageOnTriggerEffect.class, name = "DAMAGE_ON_TRIGGER"),
        @JsonSubTypes.Type(value = DiseaseRestrictionEffect.class, name = "DISEASE_RESTRICTION"),
        @JsonSubTypes.Type(value = PhysiologicalCapabilityEffect.class, name = "PHYSIOLOGICAL_CAPABILITY"),
        @JsonSubTypes.Type(value = TransformationOutcomeEffect.class, name = "TRANSFORMATION_OUTCOME"),
        @JsonSubTypes.Type(value = AbilityRollModifierEffect.class, name = "ABILITY_ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = RollModifierTrackEffect.class, name = "ROLL_MODIFIER_TRACK"),
        @JsonSubTypes.Type(value = ConditionLevelChangeEffect.class, name = "CONDITION_LEVEL_CHANGE"),
        @JsonSubTypes.Type(
                value = RepeatingConditionLevelChangeEffect.class,
                name = "REPEATING_CONDITION_LEVEL_CHANGE"),
        @JsonSubTypes.Type(value = TriggeredConditionEffect.class, name = "TRIGGERED_CONDITION"),
        @JsonSubTypes.Type(value = ConditionGatedCheckEffect.class, name = "CONDITION_GATED_CHECK"),
        @JsonSubTypes.Type(value = ConditionRelationEffect.class, name = "CONDITION_RELATION"),
        @JsonSubTypes.Type(value = ReferencedProcedureEffect.class, name = "REFERENCED_PROCEDURE"),
        @JsonSubTypes.Type(
                value = TriggeredRecoveryRestrictionEffect.class,
                name = "TRIGGERED_RECOVERY_RESTRICTION"),
        @JsonSubTypes.Type(value = SpellCastingSaveEffect.class, name = "SPELL_CASTING_SAVE"),
        @JsonSubTypes.Type(value = DeathAtConditionLevelEffect.class, name = "DEATH_AT_CONDITION_LEVEL"),
        @JsonSubTypes.Type(value = FoodSporeContaminationEffect.class, name = "FOOD_SPORE_CONTAMINATION"),
        @JsonSubTypes.Type(value = SuspendedLocationEffect.class, name = "SUSPENDED_LOCATION"),
        @JsonSubTypes.Type(value = EscapeCheckEffect.class, name = "ESCAPE_CHECK"),
        @JsonSubTypes.Type(value = BehavioralDriveEffect.class, name = "BEHAVIORAL_DRIVE"),
        @JsonSubTypes.Type(value = ConditionThresholdOutcomeEffect.class, name = "CONDITION_THRESHOLD_OUTCOME")
})
public sealed interface DiseaseEffect permits
        AttackRollModifierEffect,
        AbilityScoreChangeEffect,
        DamageVulnerabilityEffect,
        SpeedChangeEffect,
        DamageOnTriggerEffect,
        DiseaseRestrictionEffect,
        PhysiologicalCapabilityEffect,
        TransformationOutcomeEffect,
        AbilityRollModifierEffect,
        RollModifierTrackEffect,
        ConditionLevelChangeEffect,
        RepeatingConditionLevelChangeEffect,
        TriggeredConditionEffect,
        ConditionGatedCheckEffect,
        ConditionRelationEffect,
        ReferencedProcedureEffect,
        TriggeredRecoveryRestrictionEffect,
        SpellCastingSaveEffect,
        DeathAtConditionLevelEffect,
        FoodSporeContaminationEffect,
        SuspendedLocationEffect,
        EscapeCheckEffect,
        BehavioralDriveEffect,
        ConditionThresholdOutcomeEffect {
    DiseaseEffectKind kind();
}
