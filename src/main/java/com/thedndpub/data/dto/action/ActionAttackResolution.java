package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionCreatureOrObject;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionAttackResolution(
        ActionOperationKind kind,
        ActionAttackRoll attack,
        ActionCreatureOrObject target,
        Boolean replacesOneAttack,
        String operationId) implements ActionOperation {
}
