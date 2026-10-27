package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionTriggeredMovement(
        ActionOperationKind kind,
        ActionTrigger trigger,
        String response,
        String operationId) implements ActionOperation {
}
