package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionRecoveryOperation(
        ActionOperationKind kind,
        String operationId,
        String operation,
        Double amount,
        String unit,
        String targetState,
        ActionOperationApplicability appliesWhen) implements ActionOperation {
}
