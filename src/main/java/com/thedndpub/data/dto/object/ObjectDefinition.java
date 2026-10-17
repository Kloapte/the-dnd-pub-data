package com.thedndpub.data.dto.object;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.object.ObjectActionDte;
import com.thedndpub.data.dte.object.ObjectDefenseDte;
import com.thedndpub.data.dte.object.ObjectSpeedDte;
import com.thedndpub.data.dte.object.ObjectStatisticDte;
import com.thedndpub.data.dte.object.ObjectTokenReferenceDte;
import java.util.List;

public record ObjectDefinition(
        String id, String name, SourceReference source, String category, Boolean npc,
        List<String> size, ObjectStatisticDte armorClass, ObjectStatisticDte hitPoints,
        ObjectSpeedDte speed, Integer strength, Integer dexterity, Integer constitution,
        Integer intelligence, Integer wisdom, Integer charisma, List<String> senses,
        List<ObjectDefenseDte> damageImmunities, List<ObjectDefenseDte> damageResistances,
        List<ObjectDefenseDte> damageVulnerabilities, List<String> conditionImmunities,
        List<ObjectActionDte> actions, List<EntryDte> entries, List<String> reprintedAs,
        ObjectTokenReferenceDte token, List<ObjectTokenReferenceDte> alternateArt,
        Boolean srd) {
    public ObjectDefinition {
        size = copy(size);
        senses = copy(senses);
        damageImmunities = copy(damageImmunities);
        damageResistances = copy(damageResistances);
        damageVulnerabilities = copy(damageVulnerabilities);
        conditionImmunities = copy(conditionImmunities);
        actions = copy(actions);
        entries = copy(entries);
        reprintedAs = copy(reprintedAs);
        alternateArt = copy(alternateArt);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
