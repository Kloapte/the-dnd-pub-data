package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionGatedCheckEffect(
        DiseaseEffectKind kind,
        String roll,
        String ability,
        String direction,
        DiseaseReference condition,
        Integer dc,
        String dcFormula,
        String outcome,
        List<String> creatureGroupsWithAdvantage,
        Integer minimumLevel) implements DiseaseEffect {
    public ConditionGatedCheckEffect {
        creatureGroupsWithAdvantage = creatureGroupsWithAdvantage == null
                ? List.of()
                : List.copyOf(creatureGroupsWithAdvantage);
    }
}
