package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionSpellOperation(
        ActionOperationKind kind,
        String operation,
        List<String> alternatives,
        ActionLongCasting longCasting,
        String operationId) implements ActionOperation {

    public ActionSpellOperation {
        alternatives = alternatives == null ? List.of() : List.copyOf(alternatives);
    }
}
