package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionForcedMovement(
        ActionOperationKind kind,
        String movement,
        String target,
        Double distance,
        String unit,
        String alternativeOutcome,
        ActionOperationApplicability appliesWhen,
        String operationId) implements ActionOperation {
}
