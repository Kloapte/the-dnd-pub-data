package com.thedndpub.data.dto.feat;

import com.thedndpub.data.dto.race.RaceResistanceDto;

import java.util.List;

public record FeatDefenses(
        List<String> conditionImmunities,
        List<String> damageImmunities,
        List<RaceResistanceDto> resistances) {
    public FeatDefenses {
        conditionImmunities = copy(conditionImmunities);
        damageImmunities = copy(damageImmunities);
        resistances = copy(resistances);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
