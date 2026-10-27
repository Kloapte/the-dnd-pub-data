package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AutomaticSavingThrowFailureEffect(
        ConditionEffectKind kind,
        ConditionActor actor,
        List<String> abilities,
        ConditionApplicability appliesWhen) implements ConditionEffect {
    public AutomaticSavingThrowFailureEffect {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
