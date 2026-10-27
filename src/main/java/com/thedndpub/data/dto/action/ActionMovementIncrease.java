package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionMovementIncrease(
        ActionOperationKind kind,
        String movement,
        Double amount,
        String unit,
        ActionOperationApplicability appliesWhen,
        String operationId,
        String speedSelection) implements ActionOperation {
}
