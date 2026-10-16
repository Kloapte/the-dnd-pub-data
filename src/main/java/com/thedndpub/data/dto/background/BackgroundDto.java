package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundDto(
        String id,
        String name,
        SourceReference source,
        String edition,
        List<BackgroundAbilityChoiceDto> abilityChoices,
        List<BackgroundGrantGroupDte> skillProficiencies,
        List<BackgroundGrantGroupDte> toolProficiencies,
        List<BackgroundGrantGroupDte> languageProficiencies,
        List<BackgroundGrantGroupDte> skillToolLanguageProficiencies,
        List<BackgroundGrantGroupDte> featGrants,
        List<BackgroundFeatReferenceDto> feats,
        List<BackgroundEquipmentPackageDto> startingEquipment,
        List<BackgroundFeatureMetadataDto> features,
        List<BackgroundCharacteristicTableDto> characteristicTables,
        List<BackgroundAdditionalSpellDto> additionalSpells,
        List<BackgroundPrerequisiteDto> prerequisites,
        List<SourceReference> additionalSources,
        List<SourceReference> otherSources,
        List<BackgroundReprintDto> reprintedAs) {
    public BackgroundDto {
        abilityChoices = List.copyOf(abilityChoices);
        skillProficiencies = List.copyOf(skillProficiencies);
        toolProficiencies = List.copyOf(toolProficiencies);
        languageProficiencies = List.copyOf(languageProficiencies);
        skillToolLanguageProficiencies = List.copyOf(skillToolLanguageProficiencies);
        featGrants = List.copyOf(featGrants);
        feats = List.copyOf(feats);
        startingEquipment = List.copyOf(startingEquipment);
        features = List.copyOf(features);
        characteristicTables = List.copyOf(characteristicTables);
        additionalSpells = List.copyOf(additionalSpells);
        prerequisites = List.copyOf(prerequisites);
        additionalSources = List.copyOf(additionalSources);
        otherSources = List.copyOf(otherSources);
        reprintedAs = List.copyOf(reprintedAs);
    }
}
