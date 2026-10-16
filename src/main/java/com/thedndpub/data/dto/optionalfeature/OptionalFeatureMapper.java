package com.thedndpub.data.dto.optionalfeature;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.optionalfeature.OptionalFeatureDte;
import com.thedndpub.data.dte.optionalfeature.OptionalFeatureReprintDte;
import com.thedndpub.data.dte.optionalfeature.OptionalFeatureSpellGrantDte;
import com.thedndpub.data.dte.optionalfeature.OptionalFeatureProgressionDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

@Component
public final class OptionalFeatureMapper {
    public OptionalFeatureDefinition map(OptionalFeatureDte source) {
        String sourceCode = source.source();
        return new OptionalFeatureDefinition(
                "optional-feature:" + slug(sourceCode) + ":" + slug(source.name()),
                source.name(),
                new SourceReference(sourceCode, source.page()),
                list(source.featureType()),
                source.reprintedAs() == null ? List.of() : source.reprintedAs().stream()
                        .map(value -> new OptionalFeatureReprintDto(value.uid(), value.tag())).toList(),
                source.otherSources() == null ? List.of() : source.otherSources().stream()
                        .map(value -> new SourceReference(value.source(), value.page())).toList(),
                source.prerequisite() == null ? List.of() : source.prerequisite().stream().map(value ->
                        new OptionalFeaturePrerequisiteDto(
                                value.level() == null ? null : new OptionalFeatureLevelRequirementDto(
                                        value.level().level(), option(value.level().classReference()),
                                        option(value.level().subclass())),
                                list(value.item()), value.pact(), list(value.optionalfeature()),
                                value.spell() == null ? List.of() : value.spell().stream().map(spell ->
                                        new OptionalFeatureSpellRequirementDto(
                                                spell.spell(), spell.choose(), spell.entry(),
                                                spell.entrySummary(), spell.count()))
                                        .toList(),
                                value.otherSummary() == null ? null : new OptionalFeatureOtherRequirementDto(
                                        value.otherSummary().entry(), value.otherSummary().entrySummary())))
                        .toList(),
                source.consumes() == null ? null : new OptionalFeatureConsumptionDto(
                        source.consumes().name(), source.consumes().amount(),
                        source.consumes().amountMin(), source.consumes().amountMax()),
                source.additionalSpells() == null ? List.of() : source.additionalSpells().stream()
                        .map(OptionalFeatureMapper::spellGrant).toList(),
                source.skillProficiencies() == null ? List.of() : source.skillProficiencies().stream()
                        .map(value -> new OptionalFeatureProficiencyGrantDto(
                                value.deception(), value.persuasion())).toList(),
                source.senses() == null ? List.of() : source.senses().stream()
                        .map(value -> new OptionalFeatureSenseGrantDto(
                                value.blindsight(), value.darkvision(), value.truesight())).toList(),
                progressions(source.featProgression()),
                progressions(source.optionalfeatureProgression()));
    }

    private static OptionalFeatureSpellGrantDto spellGrant(OptionalFeatureSpellGrantDte value) {
        return new OptionalFeatureSpellGrantDto(
                value.ability(), choices(value.known()), choices(value.prepared()),
                value.innate() == null ? List.of() : list(value.innate().spells()),
                value.innate() == null ? Map.of() : immutableNestedMap(value.innate().daily()),
                value.innate() == null ? Map.of() : immutableNestedMap(value.innate().resource()),
                value.innate() == null ? List.of() : value.innate().ritual() == null ? List.of()
                        : value.innate().ritual().stream().map(choice ->
                                new OptionalFeatureSpellRequirementDto(
                                        choice.spell(), choice.choose(), choice.entry(),
                                        choice.entrySummary(), choice.count())).toList(),
                value.resourceName());
    }

    private static List<OptionalFeatureSpellChoiceDto> choices(
            com.thedndpub.data.dte.optionalfeature.OptionalFeatureSpellSetDte choices) {
        if (choices == null || choices.values() == null) {
            return List.of();
        }
        return choices.values().stream()
                .map(value -> new OptionalFeatureSpellChoiceDto(value.count(), value.choose()))
                .toList();
    }

    private static OptionalFeatureOptionReferenceDto option(
            com.thedndpub.data.dte.optionalfeature.OptionalFeatureClassReferenceDte value) {
        return value == null ? null : new OptionalFeatureOptionReferenceDto(
                value.name(), value.source(), value.visibleStats());
    }

    private static List<OptionalFeatureProgressionDto> progressions(List<OptionalFeatureProgressionDte> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream().map(value -> new OptionalFeatureProgressionDto(
                value.name(), list(value.featureType()), list(value.category()),
                value.progression() == null ? Map.of() : new TreeMap<>(value.progression()))).toList();
    }

    private static Map<String, List<String>> immutableNestedMap(Map<String, List<String>> values) {
        if (values == null) {
            return Map.of();
        }
        Map<String, List<String>> copy = new TreeMap<>();
        values.forEach((key, value) -> copy.put(key, List.copyOf(value)));
        return Collections.unmodifiableMap(copy);
    }

    private static <T> List<T> list(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Cannot create an optional feature ID from blank text");
        }
        return normalized;
    }
}
