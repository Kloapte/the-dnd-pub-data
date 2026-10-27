package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionTraverseCreatureSpace(
        ActionOperationKind kind,
        String operationId,
        ActionActor actor,
        String target,
        String permission,
        String uses,
        String checkId,
        ActionOperationApplicability appliesWhen) implements ActionOperation {
}
