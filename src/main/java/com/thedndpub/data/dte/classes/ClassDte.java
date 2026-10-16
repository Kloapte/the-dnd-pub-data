package com.thedndpub.data.dte.classes;

import java.util.List;
import java.util.Map;

public record ClassDte(
        String name,
        String source,
        Integer page,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024,
        String edition,
        Boolean isSidekick,
        Boolean hasFluff,
        Boolean hasFluffImages,
        List<ClassSourceReferenceDte> otherSources,
        List<String> reprintedAs,
        ClassHitDiceDte hd,
        List<String> proficiency,
        List<ClassAbilityFlagsDte> primaryAbility,
        String subclassTitle,
        ClassProficienciesDte startingProficiencies,
        ClassStartingEquipmentDte startingEquipment,
        ClassMulticlassingDte multiclassing,
        List<ClassFeatureUnlockDte> classFeatures,
        List<ClassProgressionTableDte> classTableGroups,
        List<ClassOptionalProgressionDte> optionalfeatureProgression,
        List<ClassFeatProgressionDte> featProgression,
        String casterProgression,
        String spellcastingAbility,
        List<Integer> cantripProgression,
        List<Integer> spellsKnownProgression,
        List<Integer> preparedSpellsProgression,
        String preparedSpellsChange,
        List<ClassAdditionalSpellsDte> additionalSpells,
        String preparedSpells,
        List<Integer> spellsKnownProgressionFixed,
        Boolean spellsKnownProgressionFixedAllowLowerLevel,
        Map<Integer, Map<Integer, Integer>> spellsKnownProgressionFixedByLevel) implements ClassRootDte, ClassSpellcastingSourceDte {
    public ClassDte {
        otherSources = ClassDteCollections.list(otherSources);
        reprintedAs = ClassDteCollections.list(reprintedAs);
        proficiency = ClassDteCollections.list(proficiency);
        primaryAbility = ClassDteCollections.list(primaryAbility);
        classFeatures = ClassDteCollections.list(classFeatures);
        classTableGroups = ClassDteCollections.list(classTableGroups);
        optionalfeatureProgression = ClassDteCollections.list(optionalfeatureProgression);
        featProgression = ClassDteCollections.list(featProgression);
        cantripProgression = ClassDteCollections.list(cantripProgression);
        spellsKnownProgression = ClassDteCollections.list(spellsKnownProgression);
        preparedSpellsProgression = ClassDteCollections.list(preparedSpellsProgression);
        additionalSpells = ClassDteCollections.list(additionalSpells);
        spellsKnownProgressionFixed = ClassDteCollections.list(spellsKnownProgressionFixed);
        spellsKnownProgressionFixedByLevel = ClassDteCollections.nestedMap(spellsKnownProgressionFixedByLevel);
    }
}
