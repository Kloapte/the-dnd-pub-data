package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionHideProcedure(
        ActionOperationKind kind,
        String operationId,
        ActionActor actor,
        String checkId,
        List<String> concealmentOptions,
        Boolean outOfEnemyLineOfSight,
        ActionReference condition,
        ActionOperationApplicability appliesWhen,
        ActionHideDiscoveryDc discoveryDc,
        List<ActionTrigger> endsWhen) implements ActionOperation {

    public ActionHideProcedure {
        concealmentOptions = concealmentOptions == null ? List.of() : List.copyOf(concealmentOptions);
        endsWhen = endsWhen == null ? List.of() : List.copyOf(endsWhen);
    }
}
