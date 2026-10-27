package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionMovementPermission(
        ActionOperationKind kind,
        String permission,
        String duration,
        String operationId) implements ActionOperation {
}
