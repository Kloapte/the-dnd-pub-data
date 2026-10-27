package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionObjectUse(
        ActionOperationKind kind,
        String activationRequirement,
        String operationId,
        String additionalObjectInteraction,
        Boolean itemDefinedAction) implements ActionOperation {
}
