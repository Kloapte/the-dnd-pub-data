package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellDto(
        String id,
        String name,
        SourceReference source,
        List<String> aliases,
        Integer level,
        SpellSchoolDto school,
        List<SpellCastingTimeDto> castingTimes,
        Boolean ritual,
        SpellRangeDto range,
        SpellComponentsDto components,
        List<SpellDurationDto> durations,
        SpellMechanicsDto mechanics,
        List<SpellScalingDto> scaling,
        SpellSourceRelationshipsDto sourceRelationships) {
    public SpellDto {
        aliases = List.copyOf(aliases);
        castingTimes = List.copyOf(castingTimes);
        durations = List.copyOf(durations);
        scaling = List.copyOf(scaling);
    }
}
