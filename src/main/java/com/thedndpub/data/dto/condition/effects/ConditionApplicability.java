package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionApplicabilityKind;
import com.thedndpub.data.dto.condition.enums.ConditionDistanceRelation;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionApplicability(
        ConditionApplicabilityKind kind,
        ConditionActor role,
        ConditionActor observer,
        ConditionActor creature,
        ConditionActor otherCreature,
        Double distanceFeet,
        ConditionDistanceRelation relation) {
}
