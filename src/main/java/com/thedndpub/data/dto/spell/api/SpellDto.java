package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionDto;

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
        SpellSourceRelationshipsDto sourceRelationships,
        SpellFormulaProjectionStatus formulaProjectionStatus,
        SpellMechanicsInspectionDto interpretedMechanics) {
    public SpellDto {
        aliases = List.copyOf(aliases);
        castingTimes = List.copyOf(castingTimes);
        durations = List.copyOf(durations);
        scaling = List.copyOf(scaling);
    }

    /** P1A construction preserves ordinary detail responses without an inspection envelope. */
    public SpellDto(
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
            SpellSourceRelationshipsDto sourceRelationships,
            SpellFormulaProjectionStatus formulaProjectionStatus) {
        this(id, name, source, aliases, level, school, castingTimes, ritual, range, components,
                durations, mechanics, scaling, sourceRelationships, formulaProjectionStatus, null);
    }

    /** Legacy construction has no formula index and therefore cannot claim a projection status. */
    public SpellDto(
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
        this(id, name, source, aliases, level, school, castingTimes, ritual, range, components,
                durations, mechanics, scaling, sourceRelationships, null);
    }
}
