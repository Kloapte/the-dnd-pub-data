package com.thedndpub.data.dto.race;

import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.feat.FeatAdditionalSpellDte;
import com.thedndpub.data.dte.feat.FeatResistanceDte;
import com.thedndpub.data.dte.race.RaceAbilityDte;
import com.thedndpub.data.dte.race.RaceAgeDte;
import com.thedndpub.data.dte.race.RaceHeightAndWeightDte;
import com.thedndpub.data.dte.race.RaceLineageDte;
import com.thedndpub.data.dte.race.RaceSpeedDte;

import java.util.List;
import java.util.Map;

/** Typed, character-independent mechanical properties preserved from race source records. */
public record RaceMechanics(
        List<String> size,
        RaceSpeedDte speed,
        List<RaceAbilityDte> ability,
        List<String> traitTags,
        List<BackgroundGrantGroupDte> languageProficiencies,
        RaceAgeDte age,
        RaceLineageDte lineage,
        List<FeatAdditionalSpellDte> additionalSpells,
        Integer darkvision,
        Integer blindsight,
        List<FeatResistanceDte> resistances,
        List<String> implementationResistances,
        RaceHeightAndWeightDte heightAndWeight,
        String edition,
        List<String> creatureTypes,
        EntryDte sizeEntry,
        List<BackgroundGrantGroupDte> skillProficiencies,
        List<String> creatureTypeTags,
        List<BackgroundGrantGroupDte> toolProficiencies,
        List<BackgroundGrantGroupDte> weaponProficiencies,
        List<BackgroundGrantGroupDte> armorProficiencies,
        List<com.thedndpub.data.dte.race.RaceSkillToolLanguageGrantDte> skillToolLanguageProficiencies,
        List<BackgroundGrantGroupDte> featGrants,
        List<String> conditionImmunities,
        List<String> immunities,
        List<String> vulnerabilities,
        List<String> aliases,
        Map<String, Boolean> overwrite) {
    public RaceMechanics {
        size = immutable(size);
        ability = immutable(ability);
        traitTags = immutable(traitTags);
        languageProficiencies = immutable(languageProficiencies);
        additionalSpells = immutable(additionalSpells);
        resistances = immutable(resistances);
        implementationResistances = immutable(implementationResistances);
        creatureTypes = immutable(creatureTypes);
        skillProficiencies = immutable(skillProficiencies);
        creatureTypeTags = immutable(creatureTypeTags);
        toolProficiencies = immutable(toolProficiencies);
        weaponProficiencies = immutable(weaponProficiencies);
        armorProficiencies = immutable(armorProficiencies);
        skillToolLanguageProficiencies = immutable(skillToolLanguageProficiencies);
        featGrants = immutable(featGrants);
        conditionImmunities = immutable(conditionImmunities);
        immunities = immutable(immunities);
        vulnerabilities = immutable(vulnerabilities);
        aliases = immutable(aliases);
        overwrite = overwrite == null ? Map.of() : Map.copyOf(overwrite);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
