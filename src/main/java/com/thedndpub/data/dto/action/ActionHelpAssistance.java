package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionHelpAssistance(
        ActionOperationKind kind,
        String operationId,
        String mode,
        ActionActor actor,
        String beneficiary,
        String task,
        String selectedProficiency,
        String feasibilityDecision,
        String range,
        String opponent,
        Integer opponentWithinFeetOfHelper,
        String roll,
        ActionD20RollModifier modifier,
        String qualifyingRoll,
        String expiresAt,
        ActionOperationApplicability appliesWhen) implements ActionOperation {
}
