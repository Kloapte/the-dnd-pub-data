package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.effects.ConditionEffect;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionRecord(
        String id,
        String name,
        ConditionSourceMetadata sourceMetadata,
        ConditionLevelMechanics levelMechanics,
        List<ConditionEffect> effects,
        List<ConditionReference> references) {

    public ConditionRecord {
        effects = effects == null ? List.of() : List.copyOf(effects);
        references = references == null ? List.of() : List.copyOf(references);
    }
}
