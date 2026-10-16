package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSpellcasting(
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
        List<ClassProgressionTable> slotTables,
        List<ClassProgressionTable> pactTables,
        List<ClassAdditionalSpellGroup> additionalSpells) {
    public ClassSpellcasting {
        cantrips = cantrips == null ? List.of() : List.copyOf(cantrips);
        known = known == null ? List.of() : List.copyOf(known);
        prepared = prepared == null ? List.of() : List.copyOf(prepared);
        fixedKnown = fixedKnown == null ? List.of() : List.copyOf(fixedKnown);
        fixedByLevel = fixedByLevel == null ? List.of() : List.copyOf(fixedByLevel);
        slotTables = slotTables == null ? List.of() : List.copyOf(slotTables);
        pactTables = pactTables == null ? List.of() : List.copyOf(pactTables);
        additionalSpells = additionalSpells == null ? List.of() : List.copyOf(additionalSpells);
    }
}
