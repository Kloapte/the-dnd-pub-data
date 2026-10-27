package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActivityRestrictionEffect(
        ConditionEffectKind kind,
        ConditionActor actor,
        List<String> activities,
        ConditionApplicability appliesWhen,
        String speechLimitation) implements ConditionEffect {
    public ActivityRestrictionEffect {
        activities = activities == null ? List.of() : List.copyOf(activities);
    }
}
