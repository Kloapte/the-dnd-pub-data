package com.thedndpub.data.importer.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotation;
import com.thedndpub.data.dto.spell.SpellFormulaSection;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.spell.SpellSyntaxAnnotation;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.LabelSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.MarkupTag;
import com.thedndpub.data.mechanics.syntax.ModifierSyntaxObservation;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SpellFormulaAnnotationCatalogTests {
    private static final Map<String, Long> EXPECTED_FAMILY_COUNTS = Map.of(
            "damage", 642L,
            "dice", 126L,
            "scaledamage", 148L,
            "scaledice", 14L);

    @Autowired
    private SpellFormulaAnnotationCatalog annotationCatalog;

    @Autowired
    private SpellCatalog spellCatalog;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullCorpusFormulaObservationsMatchTheAuthoredAssessment() {
        var index = annotationCatalog.index();

        assertEquals(969, index.spellRootCount());
        assertEquals(18, index.sourceFileCount());
        assertEquals(930, index.authoredOccurrenceCount());
        assertEquals(0, index.transformInstructionOccurrenceCount());
        assertEquals(0, index.unavailableOriginOccurrenceCount());
        assertEquals(422, index.formulaOwnerCount());
        assertEquals(EXPECTED_FAMILY_COUNTS, familyCounts(index.annotations()));
        assertEquals(18, index.syntaxAnnotations().size());
        assertEquals(Map.of("dc", 11L, "d20", 2L, "hit", 5L), syntaxFamilyCounts(index.syntaxAnnotations()));
        assertEquals(969, index.bySpellId().size());
        assertEquals(969, spellCatalog.size());
        assertEquals(930, index.annotations().size());

        long scalingSpellCount = spellCatalog.findAll().stream()
                .filter(spell -> !spell.scaling().isEmpty())
                .count();
        long nonPointNumericRangeCount = spellCatalog.findAll().stream()
                .filter(spell -> spell.range() != null)
                .filter(spell -> !"point".equals(spell.range().type()))
                .filter(spell -> spell.range().distance() != null)
                .filter(spell -> spell.range().distance().amount() != null)
                .count();
        assertEquals(41, scalingSpellCount);
        assertEquals(78, nonPointNumericRangeCount);
    }

    @Test
    void sourceLocationsAndUtf16SpansResolveAgainstTheOriginalSpellResources() throws Exception {
        Map<String, JsonNode> documents = new HashMap<>();
        Map<String, String> hashes = new HashMap<>();
        Set<String> formulaResources = new HashSet<>();
        ClassLoader classLoader = getClass().getClassLoader();

        for (SpellFormulaAnnotation annotation : annotationCatalog.index().annotations()) {
            MarkupTag tag = annotation.observation().tag();
            verifySourceLocation(annotation.spellId(), tag, documents, hashes, classLoader);
            formulaResources.add(tag.evidence().location().owner().resourcePath());
        }
        for (SpellSyntaxAnnotation annotation : annotationCatalog.index().syntaxAnnotations()) {
            verifySourceLocation(annotation.spellId(), annotation.observation().tag(),
                    documents, hashes, classLoader);
        }

        assertEquals(12, formulaResources.size());
        assertTrue(documents.size() >= formulaResources.size());
    }

    @Test
    void sourceEntrySectionsPreserveMainHigherLevelAndTableContext() {
        var annotations = annotationCatalog.index().annotations();

        assertTrue(annotations.stream().anyMatch(value -> value.section() == SpellFormulaSection.ENTRIES));
        assertTrue(annotations.stream().anyMatch(
                value -> value.section() == SpellFormulaSection.ENTRIES_HIGHER_LEVEL));
        assertFalse(annotations.stream().anyMatch(
                value -> value.section() == SpellFormulaSection.OTHER_SOURCE_FIELD));
        assertTrue(annotations.stream().anyMatch(value -> value.observation().tag().evidence().location()
                .hierarchy().stream().anyMatch(context -> "table".equals(context.authoredType()))));
        assertTrue(annotations.stream().noneMatch(value -> !value.observation().tag().children().isEmpty()));

        Map<String, Long> higherLevelFamilies = new TreeMap<>();
        annotations.stream()
                .filter(value -> value.section() == SpellFormulaSection.ENTRIES_HIGHER_LEVEL)
                .forEach(value -> higherLevelFamilies.merge(
                        value.observation().tag().name().toLowerCase(), 1L, Long::sum));
        assertEquals(Map.of("damage", 82L, "dice", 6L, "scaledamage", 148L, "scaledice", 14L),
                higherLevelFamilies);
    }

    @Test
    void requiredSpellExamplesRemainDistinctSourceLocatedSyntaxObservations() {
        assertSpellHasFormula("spell:phb:fireball");
        assertSpellHasFormula("spell:xphb:fireball");
        assertSpellHasFormula("spell:phb:healing-word");
        assertSpellHasFormula("spell:xphb:healing-word");
        assertSpellHasFormula("spell:phb:false-life");
        assertSpellHasFormula("spell:xphb:false-life");
        assertSpellHasFormula("spell:phb:magic-missile");
        assertSpellHasFormula("spell:xphb:magic-missile");
        assertSpellHasFormula("spell:phb:scorching-ray");
        assertSpellHasFormula("spell:tce:booming-blade");

        List<SpellFormulaAnnotation> healingWord = annotations("spell:phb:healing-word");
        List<String> healingWordMainExpressions = healingWord.stream()
                .filter(value -> value.section() == SpellFormulaSection.ENTRIES)
                .map(SpellFormulaAnnotation::observation)
                .filter(FormulaSyntaxObservation.class::isInstance)
                .map(FormulaSyntaxObservation.class::cast)
                .map(value -> value.expression().authoredToken())
                .toList();
        assertEquals(List.of("1d4"), healingWordMainExpressions);
        assertFalse(healingWordMainExpressions.stream().anyMatch(value -> value.contains("mod")));

        List<SpellFormulaAnnotation> magicMissile = annotations("spell:xphb:magic-missile");
        assertTrue(magicMissile.stream().anyMatch(value ->
                value.observation() instanceof FormulaSyntaxObservation formula
                        && formula.expression().authoredToken().equals("1d4 + 1")));

        List<SpellFormulaAnnotation> boomingBlade = annotations("spell:tce:booming-blade");
        assertTrue(boomingBlade.size() > 1);
        assertTrue(boomingBlade.stream()
                .map(value -> value.observation().tag().evidence().location().jsonPointer())
                .distinct()
                .count() > 1);

        Map<String, Set<String>> locationsByExactTag = new HashMap<>();
        annotationCatalog.index().annotations().forEach(value -> locationsByExactTag
                .computeIfAbsent(value.observation().tag().originalText(), ignored -> new HashSet<>())
                .add(value.observation().tag().evidence().location().owner().resourcePath() + "#"
                        + value.observation().tag().evidence().location().jsonPointer() + "#"
                        + value.observation().tag().evidence().span()));
        assertTrue(locationsByExactTag.values().stream().anyMatch(locations -> locations.size() > 1));
    }

    @Test
    void formulasAreNotPromotedToEffectsAndNoFormulaSpellsRemainIndexed() {
        var index = annotationCatalog.index();
        SpellId fireball = new SpellId("spell:phb:fireball");
        SpellId shield = new SpellId("spell:phb:shield");

        assertFalse(index.findBySpellId(fireball).isEmpty());
        assertTrue(index.findBySpellId(shield).isEmpty());
        assertTrue(index.annotations().stream()
                .allMatch(value -> value.observation() instanceof FormulaSyntaxObservation));
        assertTrue(index.syntaxAnnotations().stream().allMatch(value ->
                value.observation() instanceof LabelSyntaxObservation
                        || value.observation() instanceof ModifierSyntaxObservation));
        assertEquals(0, index.unavailableOriginOccurrenceCount());
    }

    @Test
    void indexAndPerSpellObservationCollectionsAreImmutableAndDeterministic() {
        var index = annotationCatalog.index();
        SpellId spellId = new SpellId("spell:phb:fireball");
        List<SpellFormulaAnnotation> first = annotationCatalog.findBySpellId(spellId);
        List<SpellFormulaAnnotation> second = annotationCatalog.findBySpellId(spellId);

        assertSame(first, second);
        assertEquals(first, index.findBySpellId(spellId));
        assertThrows(UnsupportedOperationException.class, () -> index.annotations().clear());
        assertThrows(UnsupportedOperationException.class, () -> index.bySpellId().clear());
        assertThrows(UnsupportedOperationException.class, () -> index.syntaxAnnotations().clear());
        assertThrows(UnsupportedOperationException.class, () -> index.syntaxBySpellId().clear());
        assertThrows(UnsupportedOperationException.class, () -> first.clear());
        for (int occurrence = 0; occurrence < first.size(); occurrence++) {
            assertEquals(occurrence, first.get(occurrence).occurrenceIndex());
        }
    }

    private void assertSpellHasFormula(String id) {
        assertFalse(annotations(id).isEmpty(), id);
    }

    private List<SpellFormulaAnnotation> annotations(String id) {
        return annotationCatalog.findBySpellId(new SpellId(id));
    }

    private SpellId spellIdAtOwner(JsonNode document, String ownerPointer) {
        String[] segments = ownerPointer.split("/");
        int spellIndex = Integer.parseInt(segments[2]);
        JsonNode spell = document.path("spell").get(spellIndex);
        return SpellIdentityFactory.id(spell.path("name").asText(), spell.path("source").asText());
    }

    private void verifySourceLocation(
            SpellId spellId,
            MarkupTag tag,
            Map<String, JsonNode> documents,
            Map<String, String> hashes,
            ClassLoader classLoader) throws Exception {
        var evidence = tag.evidence();
        String resourcePath = evidence.location().owner().resourcePath();
        JsonNode document = documents.get(resourcePath);
        if (document == null) {
            byte[] sourceBytes;
            try (InputStream input = classLoader.getResourceAsStream(resourcePath)) {
                assertNotNull(input, resourcePath);
                sourceBytes = input.readAllBytes();
            }
            String hash = HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(sourceBytes));
            hashes.put(resourcePath, hash);
            document = objectMapper.readTree(sourceBytes);
            documents.put(resourcePath, document);
        }

        assertEquals(hashes.get(resourcePath), evidence.location().owner().resourceSha256());
        assertEquals(tag.originalText(), evidence.span().extract(
                document.at(evidence.location().jsonPointer()).textValue()));
        assertEquals(spellId, spellIdAtOwner(document, evidence.location().owner().jsonPointer()));
        assertFalse(evidence.location().hierarchy().isEmpty());
    }

    private Map<String, Long> familyCounts(List<SpellFormulaAnnotation> annotations) {
        Map<String, Long> counts = new TreeMap<>();
        annotations.forEach(annotation -> counts.merge(
                annotation.observation().tag().name().toLowerCase(Locale.ROOT), 1L, Long::sum));
        return counts;
    }

    private Map<String, Long> syntaxFamilyCounts(List<SpellSyntaxAnnotation> annotations) {
        Map<String, Long> counts = new TreeMap<>();
        annotations.forEach(annotation -> counts.merge(
                annotation.observation().tag().name().toLowerCase(Locale.ROOT), 1L, Long::sum));
        return counts;
    }
}
