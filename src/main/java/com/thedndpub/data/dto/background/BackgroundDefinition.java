package com.thedndpub.data.dto.background;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.background.BackgroundAbilityChoiceDte;
import com.thedndpub.data.dte.background.BackgroundDte;
import com.thedndpub.data.dte.background.BackgroundEquipmentPackageDte;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record BackgroundDefinition(
        BackgroundId id,
        String name,
        SourceReference source,
        String edition,
        List<BackgroundAbilityChoiceDte> ability,
        List<BackgroundGrantGroupDte> skillProficiencies,
        List<BackgroundGrantGroupDte> toolProficiencies,
        List<BackgroundGrantGroupDte> languageProficiencies,
        List<BackgroundGrantGroupDte> skillToolLanguageProficiencies,
        List<BackgroundGrantGroupDte> feats,
        List<BackgroundEquipmentPackageDte> startingEquipment,
        List<EntryDte> entries,
        BackgroundDte effectiveRecord,
        List<SourceReference> additionalSources,
        List<SourceReference> otherSources,
        List<String> reprintedAs,
        BackgroundDte sourceRecord,
        BackgroundId parentId) {
    public BackgroundDefinition {
        ability = List.copyOf(ability);
        skillProficiencies = List.copyOf(skillProficiencies);
        toolProficiencies = List.copyOf(toolProficiencies);
        languageProficiencies = List.copyOf(languageProficiencies);
        skillToolLanguageProficiencies = List.copyOf(skillToolLanguageProficiencies);
        feats = List.copyOf(feats);
        startingEquipment = List.copyOf(startingEquipment);
        entries = List.copyOf(entries);
        additionalSources = List.copyOf(additionalSources);
        otherSources = List.copyOf(otherSources);
        reprintedAs = List.copyOf(reprintedAs);
    }
}
