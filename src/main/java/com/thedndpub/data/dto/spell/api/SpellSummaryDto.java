package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellSummaryDto(
        String id,
        String name,
        SourceReference source,
        Integer level,
        SpellSchoolDto school,
        Boolean ritual,
        Boolean concentration,
        List<SpellCastingTimeDto> castingTimes,
        SpellRangeDto range,
        List<String> damageTypes) {
    public SpellSummaryDto {
        castingTimes = List.copyOf(castingTimes);
        damageTypes = List.copyOf(damageTypes);
    }
}
