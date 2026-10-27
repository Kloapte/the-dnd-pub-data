package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionConditionOperation(
        ActionOperationKind kind,
        ActionReference target,
        ActionOperationApplicability appliesWhen,
        String recipient,
        ActionActor actor,
        String operationId) implements ActionConditionEffectOperation {
}
