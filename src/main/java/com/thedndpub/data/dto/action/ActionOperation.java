package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActionAttackResolution.class, name = "ATTACK_RESOLUTION"),
        @JsonSubTypes.Type(value = ActionRollModifier.class, name = "ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = ActionCheckOrContest.class, name = "CHECK_OR_CONTEST"),
        @JsonSubTypes.Type(value = ActionSavingThrow.class, name = "SAVING_THROW"),
        @JsonSubTypes.Type(value = ActionMovementIncrease.class, name = "MOVEMENT_INCREASE"),
        @JsonSubTypes.Type(value = ActionForcedMovement.class, name = "FORCED_MOVEMENT"),
        @JsonSubTypes.Type(value = ActionMovementPermission.class, name = "MOVEMENT_PERMISSION"),
        @JsonSubTypes.Type(value = ActionTriggeredMovement.class, name = "TRIGGERED_MOVEMENT"),
        @JsonSubTypes.Type(value = ActionTraverseCreatureSpace.class, name = "TRAVERSE_CREATURE_SPACE"),
        @JsonSubTypes.Type(value = ActionEnterCreatureSpace.class, name = "ENTER_CREATURE_SPACE"),
        @JsonSubTypes.Type(value = ActionMoveTargetWithinReach.class, name = "MOVE_TARGET_TO_SPACE_WITHIN_REACH"),
        @JsonSubTypes.Type(value = ActionMoveWithGrappledTarget.class, name = "MOVE_WITH_GRAPPLED_TARGET"),
        @JsonSubTypes.Type(value = ActionObjectOperation.class, name = "OBJECT_OPERATION"),
        @JsonSubTypes.Type(value = ActionSpellOperation.class, name = "SPELL_OPERATION"),
        @JsonSubTypes.Type(value = ActionRecoveryOperation.class, name = "RECOVERY_OPERATION"),
        @JsonSubTypes.Type(value = ActionConditionOperation.class, name = "APPLY_CONDITION"),
        @JsonSubTypes.Type(value = ActionEndConditionOperation.class, name = "END_CONDITION"),
        @JsonSubTypes.Type(value = ActionEndStatusOperation.class, name = "END_STATUS"),
        @JsonSubTypes.Type(value = ActionObjectUse.class, name = "OBJECT_USE"),
        @JsonSubTypes.Type(value = ActionAttackContestedByCheck.class, name = "ATTACK_CONTESTED_BY_CHECK"),
        @JsonSubTypes.Type(value = ActionHelpAssistance.class, name = "HELP_ASSISTANCE"),
        @JsonSubTypes.Type(value = ActionHideProcedure.class, name = "HIDE_PROCEDURE"),
        @JsonSubTypes.Type(value = ActionInfluenceProcedure.class, name = "INFLUENCE_PROCEDURE"),
        @JsonSubTypes.Type(value = ActionSpellIdentification.class, name = "SPELL_IDENTIFICATION")
})
public sealed interface ActionOperation permits
        ActionAttackResolution,
        ActionRollModifier,
        ActionCheckOrContest,
        ActionSavingThrow,
        ActionMovementIncrease,
        ActionForcedMovement,
        ActionMovementPermission,
        ActionTriggeredMovement,
        ActionTraverseCreatureSpace,
        ActionEnterCreatureSpace,
        ActionMoveTargetWithinReach,
        ActionMoveWithGrappledTarget,
        ActionObjectOperation,
        ActionSpellOperation,
        ActionRecoveryOperation,
        ActionConditionEffectOperation,
        ActionObjectUse,
        ActionAttackContestedByCheck,
        ActionHelpAssistance,
        ActionHideProcedure,
        ActionInfluenceProcedure,
        ActionSpellIdentification {

    ActionOperationKind kind();
}
