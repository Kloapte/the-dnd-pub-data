package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActivatedBy.class, name = "ACTIVATED_BY"),
        @JsonSubTypes.Type(value = AppliesForDuration.class, name = "APPLIES_FOR_DURATION"),
        @JsonSubTypes.Type(value = AppliesOnSaveOutcome.class, name = "APPLIES_ON_SAVE_OUTCOME"),
        @JsonSubTypes.Type(value = ConditionOnSaveOutcome.class, name = "CONDITION_ON_SAVE_OUTCOME"),
        @JsonSubTypes.Type(value = ConditionEndsOnSaveSuccess.class, name = "CONDITION_ENDS_ON_SAVE_SUCCESS"),
        @JsonSubTypes.Type(value = ConditionEndsWithSpell.class, name = "CONDITION_ENDS_WITH_SPELL"),
        @JsonSubTypes.Type(value = RequiresConcentration.class, name = "REQUIRES_CONCENTRATION"),
        @JsonSubTypes.Type(value = IncludesTriggeringAttack.class, name = "INCLUDES_TRIGGERING_ATTACK"),
        @JsonSubTypes.Type(value = ModifierOnSaveOutcome.class, name = "MODIFIER_ON_SAVE_OUTCOME"),
        @JsonSubTypes.Type(value = AppliesWhen.class, name = "APPLIES_WHEN"),
        @JsonSubTypes.Type(value = AppliesOnAttackRollOutcome.class, name = "APPLIES_ON_ATTACK_ROLL_OUTCOME"),
        @JsonSubTypes.Type(value = AppliesOnEachAttackRollOutcome.class, name = "APPLIES_ON_EACH_ATTACK_ROLL_OUTCOME"),
        @JsonSubTypes.Type(value = AppliesToAreaEffect.class, name = "APPLIES_TO_AREA_EFFECT")
})
public sealed interface MechanicContextBinding permits
        ActivatedBy,
        AppliesForDuration,
        AppliesOnSaveOutcome,
        ConditionOnSaveOutcome,
        ConditionEndsOnSaveSuccess,
        ConditionEndsWithSpell,
        RequiresConcentration,
        IncludesTriggeringAttack,
        ModifierOnSaveOutcome,
        AppliesWhen,
        AppliesOnAttackRollOutcome,
        AppliesOnEachAttackRollOutcome,
        AppliesToAreaEffect {

}
