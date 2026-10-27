package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;
import com.thedndpub.data.dto.action.enums.ActionSavingThrowAbilitySelection;
import com.thedndpub.data.dto.action.enums.ActionSavingThrowFailureOutcome;
import com.thedndpub.data.dto.action.enums.ActionSavingThrowMovementOutcome;
import com.thedndpub.data.dto.action.enums.ActionSavingThrowSuccessOutcome;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionSavingThrow(
        ActionOperationKind kind,
        String operationId,
        ActionActor actor,
        List<ActionAbility> abilities,
        ActionDifficultyClass dc,
        ActionSavingThrowAbilitySelection abilitySelection,
        ActionSavingThrowSuccessOutcome successOutcome,
        ActionSavingThrowFailureOutcome failureOutcome,
        ActionSavingThrowMovementOutcome movementOutcome) implements ActionOperation {

    public ActionSavingThrow {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
