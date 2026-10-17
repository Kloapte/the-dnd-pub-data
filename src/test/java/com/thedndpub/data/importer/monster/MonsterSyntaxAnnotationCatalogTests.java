package com.thedndpub.data.importer.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.monster.syntax.MonsterSyntaxAnnotation;
import com.thedndpub.data.dto.monster.syntax.MonsterSyntaxContextKind;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.LabelSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.ModifierSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.OpaqueSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.OpaqueTagCategory;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.services.LegendaryGroupSupportCatalog;
import com.thedndpub.data.services.MonsterCatalog;
import com.thedndpub.data.services.MonsterSyntaxAnnotationCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
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
class MonsterSyntaxAnnotationCatalogTests {
    @Autowired
    private MonsterSyntaxAnnotationCatalog annotationCatalog;

    @Autowired
    private MonsterCatalog monsterCatalog;

    @Autowired
    private LegendaryGroupSupportCatalog legendaryGroupSupportCatalog;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void indexesEveryAuthoredMonsterRootAgainstTheUnchangedEffectiveCatalog() {
        var index = annotationCatalog.index();

        assertEquals(4_560, index.authoredMonsterRootCount());
        assertEquals(4_851, index.effectiveMonsterCount());
        assertEquals(4_851, monsterCatalog.size());
        assertEquals(4_560, index.sourceOwners().stream()
                .map(owner -> owner.monsterId().value())
                .distinct()
                .count());
        assertEquals(4_560, index.byAuthoredMonsterId().size());
        assertEquals(111, index.sourceFileCount());
        assertFalse(index.effectiveObservationsMaterialized());
        assertEquals(188, legendaryGroupSupportCatalog.authoredInputs().groups().size());
        assertEquals(209, legendaryGroupSupportCatalog.definitionsById().size());
        assertEquals(307, legendaryGroupSupportCatalog.associations().size());
    }

    @Test
    void sourceCopyAndVersionInstructionsRemainSeparateFromEffectiveOwners() throws Exception {
        Map<String, JsonNode> documents = new HashMap<>();
        int copyRoots = 0;
        int versionDefinitions = 0;
        int abstractVersionDefinitions = 0;
        int abstractImplementations = 0;
        int rootsWithTransformInstructions = 0;

        for (var owner : annotationCatalog.index().sourceOwners()) {
            JsonNode document = documents.get(owner.sourceResourcePath());
            if (document == null) {
                try (InputStream input = getClass().getClassLoader()
                        .getResourceAsStream(owner.sourceResourcePath())) {
                    assertNotNull(input, owner.sourceResourcePath());
                    document = objectMapper.readTree(input);
                    documents.put(owner.sourceResourcePath(), document);
                }
            }

            JsonNode sourceMonster = document.at("/monster/" + owner.sourceLocation().arrayIndex());
            boolean hasCopy = sourceMonster.has("_copy");
            JsonNode versions = sourceMonster.path("_versions");
            boolean hasVersions = versions.isArray() && !versions.isEmpty();
            if (hasCopy) {
                copyRoots++;
            }
            if (hasVersions) {
                rootsWithTransformInstructions++;
                versionDefinitions += versions.size();
                for (JsonNode version : versions) {
                    if (version.has("_abstract")) {
                        abstractVersionDefinitions++;
                        abstractImplementations += version.path("_implementations").size();
                    }
                }
            }
            if (hasCopy && !hasVersions) {
                rootsWithTransformInstructions++;
            }
        }

        assertEquals(1_144, copyRoots);
        assertEquals(287, versionDefinitions);
        assertEquals(1, abstractVersionDefinitions);
        assertEquals(5, abstractImplementations);
        assertEquals(1_228, rootsWithTransformInstructions);
        assertEquals(286 + abstractImplementations, annotationCatalog.index().effectiveMonsterCount()
                - annotationCatalog.index().authoredMonsterRootCount());
    }

    @Test
    void reportsTheCorpusSyntaxFamiliesAndActionContextsDeterministically() {
        var index = annotationCatalog.index();
        Map<String, Long> families = new TreeMap<>();
        Map<MonsterSyntaxContextKind, Long> contexts = new TreeMap<>();
        Map<SyntaxOrigin, Long> origins = new TreeMap<>();
        Map<String, Long> kinds = new TreeMap<>();
        Map<OpaqueTagCategory, Long> opaqueCategories = new TreeMap<>();
        Map<Integer, Long> rechargeArgumentCounts = new TreeMap<>();
        long taggedFeatureNames = 0;
        Map<SyntaxOrigin, java.util.Set<String>> ownersByOrigin = new TreeMap<>();
        Map<String, Long> annotationsByTextProperty = new TreeMap<>();

        for (MonsterSyntaxAnnotation annotation : index.annotations()) {
            families.merge(annotation.observation().tag().name().toLowerCase(Locale.ROOT), 1L, Long::sum);
            contexts.merge(annotation.contextKind(), 1L, Long::sum);
            SyntaxOrigin origin = annotation.observation().tag().evidence().location().origin();
            origins.merge(origin, 1L, Long::sum);
            ownersByOrigin.computeIfAbsent(origin, ignored -> new java.util.TreeSet<>())
                    .add(annotation.authoredMonsterId().value());
            String observationKind = observationKind(annotation);
            kinds.merge(observationKind, 1L, Long::sum);
            if (annotation.observation() instanceof OpaqueSyntaxObservation opaque) {
                opaqueCategories.merge(opaque.category(), 1L, Long::sum);
            }
            if (annotation.observation().tag().name().equalsIgnoreCase("recharge")) {
                rechargeArgumentCounts.merge(annotation.observation().tag().arguments().size(), 1L, Long::sum);
            }
            if (annotation.featureName() != null && annotation.featureName().contains("{@")) {
                taggedFeatureNames++;
            }
            String textPointer = annotation.observation().tag().evidence().location().jsonPointer();
            annotationsByTextProperty.merge(textPointer.substring(textPointer.lastIndexOf('/') + 1), 1L, Long::sum);
        }

        assertEquals(57_534, index.annotations().size());
        assertEquals(3_733, index.authoredOwnerCountWithObservations());
        assertEquals(Map.ofEntries(
                Map.entry("action", 437L), Map.entry("actresponse", 76L), Map.entry("actsave", 525L),
                Map.entry("actsavefail", 544L), Map.entry("actsavefailby", 1L),
                Map.entry("actsavesuccess", 248L), Map.entry("actsavesuccessorfail", 84L),
                Map.entry("acttrigger", 76L), Map.entry("adventure", 19L), Map.entry("atk", 4_500L),
                Map.entry("atkr", 982L), Map.entry("b", 3L), Map.entry("book", 7L),
                Map.entry("chance", 41L), Map.entry("condition", 5_767L), Map.entry("creature", 323L),
                Map.entry("damage", 9_574L), Map.entry("dc", 5_085L),
                Map.entry("dcyourspellsave", 1L), Map.entry("dice", 849L), Map.entry("disease", 5L),
                Map.entry("feat", 1L), Map.entry("filter", 11L), Map.entry("footnote", 1L),
                Map.entry("h", 5_499L), Map.entry("hazard", 11L), Map.entry("hit", 5_870L),
                Map.entry("hityourspellattack", 73L), Map.entry("hom", 35L), Map.entry("i", 6L),
                Map.entry("item", 1_077L), Map.entry("note", 66L), Map.entry("quickref", 179L),
                Map.entry("recharge", 907L), Map.entry("sense", 457L), Map.entry("skill", 889L),
                Map.entry("skillcheck", 6L), Map.entry("spell", 11_431L), Map.entry("status", 311L),
                Map.entry("table", 2L), Map.entry("variantrule", 1_555L)), families);
        assertEquals(Map.of(
                MonsterSyntaxContextKind.TRAIT, 4_618L,
                MonsterSyntaxContextKind.ACTION, 33_775L,
                MonsterSyntaxContextKind.BONUS, 1_292L,
                MonsterSyntaxContextKind.REACTION, 609L,
                MonsterSyntaxContextKind.LEGENDARY, 1_356L,
                MonsterSyntaxContextKind.MYTHIC, 45L,
                MonsterSyntaxContextKind.VARIANT, 1_749L,
                MonsterSyntaxContextKind.SPELLCASTING, 11_751L,
                MonsterSyntaxContextKind.OTHER, 2_339L), contexts);
        assertEquals(Map.of(
                SyntaxOrigin.AUTHORED, 55_026L,
                SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION, 2_508L), origins);
        assertEquals(Map.of("formula", 10_423L, "label", 18_636L, "modifier", 5_870L, "opaque", 22_605L), kinds);
        assertEquals(Map.of(
                OpaqueTagCategory.REFERENCE, 20_698L,
                OpaqueTagCategory.FORMATTING, 75L,
                OpaqueTagCategory.UNSUPPORTED, 1_832L), opaqueCategories);
        assertEquals(Map.of(0, 165L, 1, 740L, 2, 2L), rechargeArgumentCounts);
        assertEquals(3_539, ownersByOrigin.get(SyntaxOrigin.AUTHORED).size());
        assertEquals(284, ownersByOrigin.get(SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION).size());
        assertEquals(4_061, taggedFeatureNames);
        assertEquals(897L, annotationsByTextProperty.get("name"));
    }

    @Test
    void preservesFeatureNameActionCategoryAndRechargeSyntaxWithoutInterpretingIt() {
        var observations = annotationCatalog.index().annotations();
        var recharge = observations.stream()
                .filter(value -> value.observation() instanceof LabelSyntaxObservation)
                .filter(value -> value.observation().tag().name().equalsIgnoreCase("recharge"))
                .toList();

        assertFalse(recharge.isEmpty());
        assertTrue(recharge.stream().allMatch(value -> value.contextKind() != null));
        assertTrue(recharge.stream().allMatch(value -> value.featureName() == null
                || value.featurePointer() != null));
        assertEquals(Map.of(0L, 165L, 1L, 740L, 2L, 2L), recharge.stream().collect(
                java.util.stream.Collectors.groupingBy(
                        value -> (long) value.observation().tag().arguments().size(), TreeMap::new,
                        java.util.stream.Collectors.counting())));
        assertTrue(recharge.stream().anyMatch(value -> value.featureName() != null));
        assertTrue(observations.stream().anyMatch(value -> value.featureName() != null));
    }

    @Test
    void syntaxFamiliesRemainSeparateObservationsWithoutGameplayRelationships() {
        var annotations = annotationCatalog.index().annotations();

        assertTrue(annotations.stream().anyMatch(value -> value.observation() instanceof FormulaSyntaxObservation));
        assertTrue(annotations.stream().anyMatch(value -> value.observation() instanceof ModifierSyntaxObservation));
        assertTrue(annotations.stream().anyMatch(value -> value.observation() instanceof LabelSyntaxObservation));
        assertTrue(annotations.stream().anyMatch(value -> value.observation() instanceof OpaqueSyntaxObservation));
        assertTrue(annotations.stream().allMatch(value -> value.observation().tag().evidence() != null));
        assertTrue(annotations.stream().noneMatch(value -> value.getClass().getSimpleName().contains("Effect")));
        assertTrue(annotations.stream().anyMatch(value -> value.observation().tag().name().equalsIgnoreCase("actSaveFailBy")));
        assertTrue(annotations.stream().anyMatch(value -> value.observation().tag().name()
                .equalsIgnoreCase("hitYourSpellAttack")));
    }

    @Test
    void everyTagSpanReconstructsFromTheFingerprintMatchedAuthoredText() throws Exception {
        Map<String, JsonNode> documents = new HashMap<>();
        Map<String, String> hashes = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();

        for (MonsterSyntaxAnnotation annotation : annotationCatalog.index().annotations()) {
            var tag = annotation.observation().tag();
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
            assertEquals(annotation.authoredMonsterId().value(), monsterCatalog.findByNameAndSource(
                    document.at(evidence.location().owner().jsonPointer()).path("name").asText(),
                    document.at(evidence.location().owner().jsonPointer()).path("source").asText())
                    .identity().id().value());
            assertEquals("monster", evidence.location().owner().collection());
            assertTrue(evidence.location().jsonPointer().startsWith(
                    evidence.location().owner().jsonPointer()));
            if (annotation.featurePointer() != null) {
                assertTrue(annotation.featurePointer().startsWith(evidence.location().owner().jsonPointer()));
                assertEquals(annotation.featureName(), document.at(annotation.featurePointer()).path("name").textValue());
            }
        }
    }

    @Test
    void indexesAreImmutableAndOccurrencesAreOrderedPerAuthoredMonster() {
        var index = annotationCatalog.index();
        var firstOwner = index.sourceOwners().getFirst();
        var first = annotationCatalog.findByAuthoredMonsterId(firstOwner.monsterId());

        assertSame(first, index.findByAuthoredMonsterId(firstOwner.monsterId()));
        assertThrows(UnsupportedOperationException.class, () -> index.annotations().clear());
        assertThrows(UnsupportedOperationException.class, () -> index.sourceOwners().clear());
        assertThrows(UnsupportedOperationException.class, () -> index.byAuthoredMonsterId().clear());
        assertThrows(UnsupportedOperationException.class, () -> first.clear());
        for (int occurrence = 0; occurrence < first.size(); occurrence++) {
            assertEquals(occurrence, first.get(occurrence).occurrenceIndex());
        }
        assertEquals(Set.copyOf(index.sourceOwners().stream().map(owner -> owner.monsterId().value()).toList()).size(),
                index.authoredMonsterRootCount());
    }

    @Test
    void sourceOriginDistinguishesAuthoredBodiesFromTransformInstructions() {
        var index = annotationCatalog.index();

        assertEquals(index.annotations().size(),
                index.observationCount(SyntaxOrigin.AUTHORED)
                        + index.observationCount(SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION)
                        + index.observationCount(SyntaxOrigin.AUTHORED_ORIGIN_UNAVAILABLE));
        assertEquals(0, index.observationCount(SyntaxOrigin.AUTHORED_ORIGIN_UNAVAILABLE));
        assertTrue(index.observationCount(SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION) > 0);
    }

    @Test
    void syntaxIndexDoesNotExpandThePublicMonsterDto() {
        var monster = monsterCatalog.findByNameAndSource("Aboleth", "MM");
        JsonNode publicJson = objectMapper.valueToTree(monster);

        assertNotNull(monster);
        assertFalse(publicJson.has("syntaxAnnotations"));
        assertFalse(publicJson.has("syntaxIndex"));
    }

    private String observationKind(MonsterSyntaxAnnotation annotation) {
        if (annotation.observation() instanceof FormulaSyntaxObservation) {
            return "formula";
        }
        if (annotation.observation() instanceof ModifierSyntaxObservation) {
            return "modifier";
        }
        if (annotation.observation() instanceof LabelSyntaxObservation) {
            return "label";
        }
        return "opaque";
    }
}
