package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionObjectOperation(
        ActionOperationKind kind,
        String operation,
        String activation,
        ActionTiming timing,
        ActionInteractionAllowance interactionAllowance,
        String operationId) implements ActionOperation {
}
