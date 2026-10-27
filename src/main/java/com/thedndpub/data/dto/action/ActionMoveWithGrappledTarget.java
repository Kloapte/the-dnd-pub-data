package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionMoveWithGrappledTarget(
        ActionOperationKind kind,
        String operationId,
        ActionActor actor,
        String target,
        ActionReference condition,
        List<String> modes,
        ActionGrappledMovementCost speedCost) implements ActionOperation {

    public ActionMoveWithGrappledTarget {
        modes = modes == null ? List.of() : List.copyOf(modes);
    }
}

