package com.thedndpub.data.dto.spell;

import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Immutable internal index of source-authored Spell formula syntax. */
public final class SpellFormulaAnnotationIndex {
    private final int sourceFileCount;
    private final List<SpellFormulaAnnotation> annotations;
    private final Map<SpellId, List<SpellFormulaAnnotation>> bySpellId;
    private final List<SpellSyntaxAnnotation> syntaxAnnotations;
    private final Map<SpellId, List<SpellSyntaxAnnotation>> syntaxBySpellId;

    public SpellFormulaAnnotationIndex(
            List<SpellDefinition> spellDefinitions,
            int sourceFileCount,
            List<SpellFormulaAnnotation> annotations,
            List<SpellSyntaxAnnotation> syntaxAnnotations) {
        if (sourceFileCount < 0) {
            throw new IllegalArgumentException("Source file count must not be negative");
        }
        this.sourceFileCount = sourceFileCount;
        this.annotations = List.copyOf(annotations);
        this.syntaxAnnotations = List.copyOf(syntaxAnnotations);

        Map<SpellId, List<SpellFormulaAnnotation>> formulaIndex = emptySpellIndex(spellDefinitions);
        Map<SpellId, List<SpellSyntaxAnnotation>> syntaxIndex = emptySpellIndex(spellDefinitions);
        validateFormulaAnnotations(formulaIndex, this.annotations);
        validateSyntaxAnnotations(syntaxIndex, this.syntaxAnnotations);
        bySpellId = immutableIndex(formulaIndex);
        syntaxBySpellId = immutableIndex(syntaxIndex);
    }

    private <T> Map<SpellId, List<T>> emptySpellIndex(List<SpellDefinition> spellDefinitions) {
        Map<SpellId, List<T>> index = new LinkedHashMap<>();
        for (SpellDefinition definition : spellDefinitions) {
            Objects.requireNonNull(definition, "spell definition");
            if (index.putIfAbsent(definition.id(), new ArrayList<>()) != null) {
                throw new IllegalArgumentException("Duplicate Spell ID in syntax index: " + definition.id().value());
            }
        }
        return index;
    }

    private void validateFormulaAnnotations(
            Map<SpellId, List<SpellFormulaAnnotation>> index,
            List<SpellFormulaAnnotation> values) {
        Map<SpellId, Integer> expectedOccurrenceIndex = new LinkedHashMap<>();
        for (SpellFormulaAnnotation annotation : values) {
            List<SpellFormulaAnnotation> spellAnnotations = index.get(annotation.spellId());
            if (spellAnnotations == null) {
                throw new IllegalArgumentException("Formula annotation has unknown Spell ID: "
                        + annotation.spellId().value());
            }
            int expectedIndex = expectedOccurrenceIndex.getOrDefault(annotation.spellId(), 0);
            if (annotation.occurrenceIndex() != expectedIndex) {
                throw new IllegalArgumentException("Non-sequential formula occurrence index for Spell "
                        + annotation.spellId().value());
            }
            spellAnnotations.add(annotation);
            expectedOccurrenceIndex.put(annotation.spellId(), expectedIndex + 1);
        }
    }

    private void validateSyntaxAnnotations(
            Map<SpellId, List<SpellSyntaxAnnotation>> index,
            List<SpellSyntaxAnnotation> values) {
        Map<SpellId, Integer> expectedOccurrenceIndex = new LinkedHashMap<>();
        for (SpellSyntaxAnnotation annotation : values) {
            List<SpellSyntaxAnnotation> spellAnnotations = index.get(annotation.spellId());
            if (spellAnnotations == null) {
                throw new IllegalArgumentException("Syntax annotation has unknown Spell ID: "
                        + annotation.spellId().value());
            }
            int expectedIndex = expectedOccurrenceIndex.getOrDefault(annotation.spellId(), 0);
            if (annotation.occurrenceIndex() != expectedIndex) {
                throw new IllegalArgumentException("Non-sequential syntax occurrence index for Spell "
                        + annotation.spellId().value());
            }
            spellAnnotations.add(annotation);
            expectedOccurrenceIndex.put(annotation.spellId(), expectedIndex + 1);
        }
    }

    private <T> Map<SpellId, List<T>> immutableIndex(Map<SpellId, List<T>> source) {
        Map<SpellId, List<T>> immutableIndex = new LinkedHashMap<>();
        source.forEach((spellId, values) -> immutableIndex.put(spellId, List.copyOf(values)));
        return Collections.unmodifiableMap(immutableIndex);
    }

    public int sourceFileCount() {
        return sourceFileCount;
    }

    public int spellRootCount() {
        return bySpellId.size();
    }

    public List<SpellFormulaAnnotation> annotations() {
        return annotations;
    }

    public Map<SpellId, List<SpellFormulaAnnotation>> bySpellId() {
        return bySpellId;
    }

    public List<SpellFormulaAnnotation> findBySpellId(SpellId spellId) {
        return bySpellId.getOrDefault(spellId, List.of());
    }

    public List<SpellSyntaxAnnotation> syntaxAnnotations() {
        return syntaxAnnotations;
    }

    public Map<SpellId, List<SpellSyntaxAnnotation>> syntaxBySpellId() {
        return syntaxBySpellId;
    }

    public List<SpellSyntaxAnnotation> findSyntaxBySpellId(SpellId spellId) {
        return syntaxBySpellId.getOrDefault(spellId, List.of());
    }

    public long formulaOwnerCount() {
        return bySpellId.values().stream().filter(values -> !values.isEmpty()).count();
    }

    public long authoredOccurrenceCount() {
        return countFormulaByOrigin(SyntaxOrigin.AUTHORED);
    }

    public long transformInstructionOccurrenceCount() {
        return countFormulaByOrigin(SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION);
    }

    public long unavailableOriginOccurrenceCount() {
        return countFormulaByOrigin(SyntaxOrigin.AUTHORED_ORIGIN_UNAVAILABLE);
    }

    private long countFormulaByOrigin(SyntaxOrigin origin) {
        return annotations.stream()
                .filter(annotation -> annotation.observation().tag().evidence().location().origin() == origin)
                .count();
    }
}
