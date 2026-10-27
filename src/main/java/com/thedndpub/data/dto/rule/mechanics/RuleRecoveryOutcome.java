package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleRestoreAll.class, name = "RESTORE_ALL"),
        @JsonSubTypes.Type(value = RuleRestoreFraction.class, name = "RESTORE_FRACTION"),
        @JsonSubTypes.Type(value = RuleReduceConditionLevel.class, name = "REDUCE_CONDITION_LEVEL"),
        @JsonSubTypes.Type(value = RuleEndEffect.class, name = "END_EFFECT"),
        @JsonSubTypes.Type(value = RuleAdvantageOnSaves.class, name = "ADVANTAGE_ON_SAVES"),
        @JsonSubTypes.Type(value = RuleNoRestRecovery.class, name = "NO_REST_RECOVERY"),
        @JsonSubTypes.Type(value = RuleRestoreLevelFraction.class, name = "RESTORE_LEVEL_FRACTION"),
        @JsonSubTypes.Type(value = RuleSpendResourceToRecover.class, name = "SPEND_RESOURCE_TO_RECOVER"),
        @JsonSubTypes.Type(value = RuleRechargeAsDefinedByFeature.class, name = "RECHARGE_AS_DEFINED_BY_FEATURE"),
        @JsonSubTypes.Type(value = RuleResetLimitedUse.class, name = "RESET_LIMITED_USE"),
        @JsonSubTypes.Type(value = RuleIncreaseConditionLevel.class, name = "INCREASE_CONDITION_LEVEL"),
        @JsonSubTypes.Type(value = RuleRestoreReducedAbilityScore.class, name = "RESTORE_ONE_REDUCED_ABILITY_SCORE"),
        @JsonSubTypes.Type(value = RuleNoConditionLevelReduction.class, name = "NO_CONDITION_LEVEL_REDUCTION")
})
public sealed interface RuleRecoveryOutcome permits RuleRestoreAll, RuleRestoreFraction, RuleReduceConditionLevel,
        RuleEndEffect, RuleAdvantageOnSaves, RuleNoRestRecovery, RuleRestoreLevelFraction,
        RuleSpendResourceToRecover, RuleRechargeAsDefinedByFeature, RuleResetLimitedUse,
        RuleIncreaseConditionLevel, RuleRestoreReducedAbilityScore, RuleNoConditionLevelReduction {
    String kind();
}
