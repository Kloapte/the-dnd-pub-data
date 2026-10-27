package com.thedndpub.data.dto.action;

import com.thedndpub.data.dto.action.enums.ActionActor;

public sealed interface ActionConditionEffectOperation extends ActionOperation permits
        ActionConditionOperation,
        ActionEndConditionOperation,
        ActionEndStatusOperation {

    ActionReference target();

    ActionOperationApplicability appliesWhen();

    String recipient();

    ActionActor actor();

    String operationId();
}
