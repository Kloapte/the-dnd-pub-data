package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.feat.FeatAdditionalSpellDte;
import java.util.List;

public record RaceVersionDte(
        String name,
        String source,
        @JsonProperty("_mod") RaceModificationDte modification,
        @JsonProperty("_abstract") RaceVersionAbstractDte abstractTemplate,
        @JsonProperty("_implementations") List<RaceVersionImplementationDte> implementations,
        List<String> resist,
        Integer darkvision,
        RaceSpeedDte speed,
        List<String> traitTags,
        List<BackgroundGrantGroupDte> skillProficiencies,
        List<FeatAdditionalSpellDte> additionalSpells,
        java.util.Map<String, Boolean> overwrite,
        List<BackgroundGrantGroupDte> weaponProficiencies) {
    public RaceVersionDte {
        implementations = implementations == null ? null : List.copyOf(implementations);
        resist = resist == null ? null : List.copyOf(resist);
        traitTags = traitTags == null ? null : List.copyOf(traitTags);
        skillProficiencies = skillProficiencies == null ? null : List.copyOf(skillProficiencies);
        additionalSpells = additionalSpells == null ? null : List.copyOf(additionalSpells);
        overwrite = overwrite == null ? null : java.util.Map.copyOf(overwrite);
        weaponProficiencies = weaponProficiencies == null ? null : List.copyOf(weaponProficiencies);
        skillProficiencies = skillProficiencies == null ? null : List.copyOf(skillProficiencies);
    }
}
