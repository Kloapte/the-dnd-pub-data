package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionSpellIdentification(
        ActionOperationKind kind,
        String operationId,
        String checkId,
        ActionAbility checkAbility,
        ActionReference skill,
        List<ActionSpellIdentificationMode> modes,
        String advantageWhen) implements ActionOperation {

    public ActionSpellIdentification {
        modes = modes == null ? List.of() : List.copyOf(modes);
    }
}
