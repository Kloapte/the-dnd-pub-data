package com.thedndpub.data.dte.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.common.entry.EntryDte;
import java.util.List;

public record ObjectSourceDte(
        String name,
        Boolean isNpc,
        String source,
        Integer page,
        List<String> size,
        String objectType,
        ObjectStatisticDte ac,
        ObjectStatisticDte hp,
        ObjectSpeedDte speed,
        Integer str,
        Integer dex,
        Integer con,
        Integer wis,
        Integer cha,
        List<String> senses,
        List<ObjectDefenseDte> immune,
        List<ObjectDefenseDte> resist,
        List<ObjectDefenseDte> vulnerable,
        List<String> conditionImmune,
        List<ObjectActionDte> actionEntries,
        @JsonProperty("int") Integer intValue,
        List<String> reprintedAs,
        ObjectTokenReferenceDte token,
        List<ObjectTokenReferenceDte> altArt,
        String tokenCredit,
        Boolean tokenCustom,
        Boolean hasToken,
        Boolean hasFluffImages,
        Boolean srd,
        List<EntryDte> entries) {
    public ObjectSourceDte {
        size = copy(size);
        senses = copy(senses);
        immune = copy(immune);
        resist = copy(resist);
        vulnerable = copy(vulnerable);
        conditionImmune = copy(conditionImmune);
        actionEntries = copy(actionEntries);
        reprintedAs = copy(reprintedAs);
        altArt = copy(altArt);
        entries = copy(entries);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
