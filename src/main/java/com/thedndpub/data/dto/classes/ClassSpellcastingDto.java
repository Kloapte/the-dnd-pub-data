package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Public spellcasting metadata with positioned progression table projections. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSpellcastingDto(
        String casterProgression,
        String ability,
        List<ClassLevelCount> cantrips,
        List<ClassLevelCount> known,
        List<ClassLevelCount> prepared,
        String preparedExpression,
        String preparedChange,
        List<ClassLevelCount> fixedKnown,
        Boolean fixedAllowLowerLevel,
        List<ClassSpellLevelCount> fixedByLevel,
        List<ClassProgressionTableDto> slotTables,
        List<ClassProgressionTableDto> pactTables,
        List<ClassAdditionalSpellGroup> additionalSpells) {
    public ClassSpellcastingDto {
        cantrips = copy(cantrips);
        known = copy(known);
        prepared = copy(prepared);
        fixedKnown = copy(fixedKnown);
        fixedByLevel = copy(fixedByLevel);
        slotTables = copy(slotTables);
        pactTables = copy(pactTables);
        additionalSpells = copy(additionalSpells);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
