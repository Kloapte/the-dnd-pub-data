package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SubclassDte(
        String name,
        String source,
        Integer page,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024,
        String shortName,
        String className,
        String classSource,
        String edition,
        Boolean isReprinted,
        Boolean hasFluff,
        Boolean hasFluffImages,
        ClassSubclassFluffDte fluff,
        List<ClassSourceReferenceDte> otherSources,
        List<String> reprintedAs,
        @JsonProperty("_copy") ClassCopyDte copy,
        List<String> subclassFeatures,
        List<ClassProgressionTableDte> subclassTableGroups,
        List<ClassOptionalProgressionDte> optionalfeatureProgression,
        List<ClassFeatProgressionDte> featProgression,
        String casterProgression,
        String spellcastingAbility,
        List<Integer> cantripProgression,
        List<Integer> spellsKnownProgression,
        List<Integer> preparedSpellsProgression,
        String preparedSpellsChange,
        List<ClassAdditionalSpellsDte> additionalSpells) implements ClassRootDte, ClassSpellcastingSourceDte {
    public SubclassDte {
        otherSources = ClassDteCollections.list(otherSources);
        reprintedAs = ClassDteCollections.list(reprintedAs);
        subclassFeatures = ClassDteCollections.list(subclassFeatures);
        subclassTableGroups = ClassDteCollections.list(subclassTableGroups);
        optionalfeatureProgression = ClassDteCollections.list(optionalfeatureProgression);
        featProgression = ClassDteCollections.list(featProgression);
        cantripProgression = ClassDteCollections.list(cantripProgression);
        spellsKnownProgression = ClassDteCollections.list(spellsKnownProgression);
        preparedSpellsProgression = ClassDteCollections.list(preparedSpellsProgression);
        additionalSpells = ClassDteCollections.list(additionalSpells);
    }
}
