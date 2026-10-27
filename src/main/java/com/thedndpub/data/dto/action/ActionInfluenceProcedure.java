package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionInfluenceProcedure(
        ActionOperationKind kind,
        String operationId,
        String target,
        String willingnessInput,
        List<ActionInfluenceBranch> branches) implements ActionOperation {

    public ActionInfluenceProcedure {
        branches = branches == null ? List.of() : List.copyOf(branches);
    }
}
