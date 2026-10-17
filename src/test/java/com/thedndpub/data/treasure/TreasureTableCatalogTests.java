package com.thedndpub.data.treasure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.TreasureTablesController;
import com.thedndpub.data.dto.treasure.IndividualTreasureDefinition;
import com.thedndpub.data.dto.treasure.MagicItemTableDefinition;
import com.thedndpub.data.dto.treasure.TreasureTableDefinition;
import com.thedndpub.data.dto.treasure.TreasureTableKind;
import com.thedndpub.data.dto.treasure.TreasureTableMapper;
import com.thedndpub.data.dte.treasure.FromGenericChoiceDte;
import com.thedndpub.data.dte.treasure.FromGroupChoiceDte;
import com.thedndpub.data.dte.treasure.FromItemsChoiceDte;
import com.thedndpub.data.dte.treasure.FromMatchingChoiceDte;
import com.thedndpub.data.dte.treasure.LootFileDte;
import com.thedndpub.data.dte.treasure.MagicItemChooseDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ReferenceSemanticForm;
import com.thedndpub.data.references.ReferenceTargetKind;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.TreasureTableCatalog;
import com.thedndpub.data.services.TreasureTablesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.OutputStream;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TreasureTableCatalogTests {
    @Autowired
    private TreasureTableCatalog catalog;

    @Autowired
    private TreasureTablesService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TreasureTablesController(service)).build();
    }

    @Test
    void loadsAllSevenSourceCollectionsAndSeventyFiveNamedDefinitions() {
        assertEquals(75, catalog.size());
        assertEquals(Map.of(
                "individual", 8,
                "hoard", 8,
                "dragon", 4,
                "gems", 12,
                "artObjects", 10,
                "magicItems", 33), catalog.definitions().stream().collect(Collectors.groupingBy(
                        definition -> definition.kind().sourceCollection(),
                        Collectors.summingInt(ignored -> 1))));
        assertEquals(25, catalog.dragonMundaneItemRows().size());
        assertEquals(86, catalog.definitions().stream().mapToInt(TreasureTableDefinition::tableCount).sum() + 1);
        assertEquals(1_354, catalog.definitions().stream().mapToInt(TreasureTableDefinition::rowCount).sum()
                + catalog.dragonMundaneItemRows().size());

        assertEquals(Map.of("DMG", 28, "FTD", 4, "XDMG", 43), catalog.definitions().stream()
                .collect(Collectors.groupingBy(definition -> definition.source().identity().code(),
                        Collectors.summingInt(ignored -> 1))));
        assertEquals(Map.of(
                "individual", Map.of("DMG", 4, "XDMG", 4),
                "hoard", Map.of("DMG", 4, "XDMG", 4),
                "dragon", Map.of("FTD", 4),
                "gems", Map.of("DMG", 6, "XDMG", 6),
                "artObjects", Map.of("DMG", 5, "XDMG", 5),
                "magicItems", Map.of("DMG", 9, "XDMG", 24)),
                catalog.definitions().stream().collect(Collectors.groupingBy(
                        definition -> definition.kind().sourceCollection(),
                        Collectors.groupingBy(definition -> definition.source().identity().code(),
                                Collectors.summingInt(ignored -> 1)))));
        assertTrue(catalog.definitions().stream().allMatch(definition ->
                sourceRegistry.find(definition.source().identity().code()) != null));
        assertTrue(catalog.definitions().stream().map(definition -> definition.id().value()).distinct().count()
                == catalog.size());
        assertTrue(catalog.summaries().stream().allMatch(summary ->
                summary.id().value().startsWith("treasure-table:")));
        assertThrows(UnsupportedOperationException.class, () -> catalog.definitions().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.dragonMundaneItemRows().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.sourceData().magicItems().clear());
    }

    @Test
    void sourceDteRoundTripsEveryObservedFieldAndJsonType() throws Exception {
        JsonNode source = objectMapper.readTree(new ClassPathResource("data/loot.json").getInputStream());
        ObjectMapper sourceShapeMapper = objectMapper.copy().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JsonNode normalizedSourceShape = sourceShapeMapper.valueToTree(catalog.sourceData());

        assertEquals(source, normalizedSourceShape);
        assertEquals(7, source.size());
        assertEquals(131, fieldPaths(source).size());
    }

    @Test
    void sourceAwareIdentitySeparatesRepeatedChallengeBandsAndReprints() {
        List<TreasureTableDefinition> challengeTables = catalog.findByName("Challenge 0-4");
        assertEquals(4, challengeTables.size());
        assertEquals(2, challengeTables.stream().filter(value -> value.source().identity().code().equals("DMG"))
                .count());
        assertEquals(1, challengeTables.stream().filter(value -> value.kind() == TreasureTableKind.INDIVIDUAL)
                .filter(value -> value.source().identity().code().equals("DMG")).count());
        assertEquals(1, challengeTables.stream().filter(value -> value.kind() == TreasureTableKind.HOARD)
                .filter(value -> value.source().identity().code().equals("DMG")).count());
        assertEquals("treasure-table:DMG:individual:challenge-0-4",
                challengeTables.stream().filter(value -> value.kind() == TreasureTableKind.INDIVIDUAL)
                        .filter(value -> value.source().identity().code().equals("DMG"))
                        .findFirst().orElseThrow().id().value());

        long reprintTargets = catalog.sourceData().gems().stream().mapToLong(value ->
                value.reprintedAs() == null ? 0 : value.reprintedAs().size()).sum()
                + catalog.sourceData().artObjects().stream().mapToLong(value ->
                value.reprintedAs() == null ? 0 : value.reprintedAs().size()).sum();
        assertEquals(11, reprintTargets);
        assertEquals(1, service.findAll("XDMG", "gems", "10").size());
    }

    @Test
    void preservesAllChoiceVariantsAndRejectsUnknownChoiceShapes() {
        List<MagicItemTableDefinition> definitions = catalog.definitions().stream()
                .filter(MagicItemTableDefinition.class::isInstance)
                .map(MagicItemTableDefinition.class::cast)
                .toList();
        List<MagicItemChooseDte> choices = definitions.stream()
                .flatMap(value -> value.authoredData().table().stream())
                .map(value -> value.choose()).filter(java.util.Objects::nonNull).toList();

        assertEquals(172, choices.size());
        assertEquals(51, choices.stream().filter(FromItemsChoiceDte.class::isInstance).count());
        assertEquals(100, choices.stream().filter(FromGenericChoiceDte.class::isInstance).count());
        assertEquals(6, choices.stream().filter(FromGroupChoiceDte.class::isInstance).count());
        assertEquals(15, choices.stream().filter(FromMatchingChoiceDte.class::isInstance).count());
        assertEquals(190, choices.stream().filter(FromItemsChoiceDte.class::isInstance)
                .map(FromItemsChoiceDte.class::cast).mapToInt(value -> value.fromItems().size()).sum());

        assertThrows(JsonMappingException.class, () -> objectMapper.readerFor(LootFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("""
                        {"individual":[{"name":"T","source":"DMG","page":1,"crMin":0,"crMax":4,
                          "table":[],"unexpected":true}]}
                        """));
        assertThrows(JsonMappingException.class, () -> objectMapper.readerFor(LootFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("""
                        {"individual":[{"name":"T","source":"DMG","page":1,"crMin":0,"crMax":4,
                          "table":[{"min":1,"max":100,"coins":{"gp":"1d4","unexpected":"1d4"}}]}]}
                        """));
        assertThrows(JsonMappingException.class, () -> objectMapper.readerFor(LootFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("""
                        {"magicItems":[{"name":"T","source":"DMG","page":1,"type":"A","table":[
                          {"min":1,"max":2,"choose":{"unsupported":[]}}
                        ]}]}
                        """));
        assertThrows(JsonMappingException.class, () -> objectMapper.readerFor(LootFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("""
                        {"magicItems":[{"name":"T","source":"DMG","page":1,"type":"A","table":[
                          {"min":1,"max":2,"choose":{"fromItems":[],"fromGroup":[]}}
                        ]}]}
                        """));
    }

    @Test
    void preservesRollRowsAmountsRangesAndMeaningfulZeroValues() {
        var individual = catalog.definitions().stream()
                .filter(value -> value.id().value().equals("treasure-table:DMG:individual:challenge-0-4"))
                .map(IndividualTreasureDefinition.class::cast)
                .findFirst().orElseThrow();
        assertEquals(0, individual.authoredData().crMin());
        assertEquals(21, catalog.sourceData().individual().stream()
                .mapToInt(value -> value.table().size()).sum());
        assertEquals("5d6", catalog.sourceData().individual().getFirst().table().getFirst().coins().cp());
        assertEquals("6d6*100", catalog.sourceData().hoard().getFirst().coins().cp());
        assertEquals("2d8", catalog.sourceData().dragon().getFirst().gems().amount());

        long nestedRowsMissingMaximum = catalog.sourceData().magicItems().stream()
                .flatMap(value -> value.table().stream())
                .filter(value -> value.table() != null)
                .flatMap(value -> value.table().stream())
                .filter(value -> value.max() == null)
                .count();
        assertEquals(6, nestedRowsMissingMaximum);
        assertTrue(catalog.sourceData().magicItems().stream().flatMap(value -> value.table().stream())
                .flatMap(value -> value.table() == null ? java.util.stream.Stream.empty() : value.table().stream())
                .anyMatch(value -> value.min() != null && value.max() == null));
    }

    @Test
    void resolvesOnlyAuthoredItemLinksAndPreservesHistoricalResolverOutcomes() {
        List<com.thedndpub.data.references.ResolutionResult> newReferences =
                resolutionCoordinator.snapshot().results().stream()
                        .filter(value -> value.reference().owner().catalog().equals("treasure-table"))
                        .toList();
        assertEquals(1_332, newReferences.size());
        assertEquals(1_127, newReferences.stream()
                .filter(value -> value.reference().semanticForm() == ReferenceSemanticForm.DEFINITION
                        || value.reference().semanticForm() == ReferenceSemanticForm.UNSUPPORTED).count());
        assertEquals(977, newReferences.stream()
                .filter(value -> value.reference().semanticForm() == ReferenceSemanticForm.DEFINITION).count());
        assertEquals(150, newReferences.stream()
                .filter(value -> value.reference().semanticForm() == ReferenceSemanticForm.UNSUPPORTED).count());
        assertEquals(190, newReferences.stream()
                .filter(value -> value.reference().semanticForm() == ReferenceSemanticForm.CHOICE).count());
        assertEquals(15, newReferences.stream()
                .filter(value -> value.reference().semanticForm() == ReferenceSemanticForm.FILTER).count());
        assertEquals(Map.of(
                "artObjects", Map.of(ResolutionOutcome.RESOLVED, 50L,
                        ResolutionOutcome.AMBIGUOUS, 32L, ResolutionOutcome.DEFERRED, 16L),
                "gems", Map.of(ResolutionOutcome.RESOLVED, 52L, ResolutionOutcome.AMBIGUOUS, 52L),
                "magicItems", Map.of(ResolutionOutcome.RESOLVED, 376L,
                        ResolutionOutcome.AMBIGUOUS, 269L, ResolutionOutcome.UNRESOLVED, 108L,
                        ResolutionOutcome.DEFERRED, 377L)),
                newReferences.stream().collect(Collectors.groupingBy(
                        value -> value.reference().owner().stableId().split(":")[2],
                        Collectors.groupingBy(
                                com.thedndpub.data.references.ResolutionResult::outcome,
                                Collectors.counting()))));
        assertTrue(newReferences.stream().allMatch(value -> value.reference().targetKind() == ReferenceTargetKind.ITEM));
        assertTrue(newReferences.stream().filter(value -> value.outcome() == ResolutionOutcome.RESOLVED)
                .allMatch(value -> value.reference().parsedSource() != null));
        assertTrue(newReferences.stream().filter(value -> value.reference().semanticForm() != ReferenceSemanticForm.DEFINITION)
                .allMatch(value -> value.outcome() == ResolutionOutcome.DEFERRED
                        && value.resolvedTargetId() == null));

        Map<ResolutionOutcome, Long> historicalOutcomes = resolutionCoordinator.snapshot().results().stream()
                .filter(value -> !value.reference().owner().catalog().equals("treasure-table"))
                .collect(Collectors.groupingBy(
                        com.thedndpub.data.references.ResolutionResult::outcome,
                        Collectors.counting()));
        assertEquals(Map.of(
                ResolutionOutcome.RESOLVED, 2_039L,
                ResolutionOutcome.AMBIGUOUS, 1_701L,
                ResolutionOutcome.UNRESOLVED, 73L,
                ResolutionOutcome.DEFERRED, 1_101L), historicalOutcomes);
        assertEquals(1_127, newReferences.stream().filter(value ->
                value.reference().fieldLocator().startsWith("table[")
                        && (value.reference().semanticForm() == ReferenceSemanticForm.DEFINITION
                        || value.reference().semanticForm() == ReferenceSemanticForm.UNSUPPORTED)).count());
        assertEquals(190, newReferences.stream().filter(value ->
                value.reference().fieldLocator().contains("choose.fromItems[")).count());
    }

    @Test
    void publicProjectionExposesMetadataAndRangesWithoutPublishingTableText() throws Exception {
        String gemId = "treasure-table:DMG:gems:10-gp-gemstones";
        String gemJson = objectMapper.writeValueAsString(service.findById(gemId));
        assertTrue(gemJson.contains("10 gp Gemstones"));
        assertTrue(gemJson.contains("gems"));
        assertFalse(gemJson.contains("opaque mottled deep blue"));
        assertFalse(gemJson.contains("{@item"));

        String dragonId = "treasure-table:FTD:dragon:wyrmling";
        String dragonJson = objectMapper.writeValueAsString(service.findById(dragonId));
        assertTrue(dragonJson.contains("dragonMundaneItems"));
        assertTrue(dragonJson.contains("rollRanges"));
        assertFalse(dragonJson.contains("A painting by an artist"));
        assertFalse(dragonJson.contains("{@"));

        var details = service.findById("treasure-table:DMG:magicItems:magic-item-table-a");
        assertEquals(8, details.tables().getFirst().rollRanges().size());
        assertEquals(1, details.tables().getFirst().rollRanges().getFirst().min());
        assertEquals(100, details.tables().getFirst().rollRanges().getLast().max());
    }

    @Test
    void filtersAndDuplicateNameLookupRemainSourceAndCollectionAware() throws Exception {
        assertEquals(1, service.findAll("DMG", "individual", "0-4").size());
        assertEquals(1, service.findByName("Challenge 0-4", "DMG", "hoard").size());
        assertEquals(2, service.findByName("Challenge 0-4", "DMG", null).size());
        assertEquals(0, service.findByName("not a table", null, null).size());

        mockMvc.perform(get("/api/treasure-tables")
                        .param("source", "DMG")
                        .param("type", "individual")
                        .param("category", "0-4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("treasure-table:DMG:individual:challenge-0-4"));
        mockMvc.perform(get("/api/treasure-tables/by-name/{name}", "Challenge 0-4")
                        .param("source", "DMG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/treasure-tables/treasure-table:DMG:individual:challenge-0-4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collectionType").value("individual"));
        mockMvc.perform(get("/api/treasure-tables/not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void packagedJarClasspathLoadsTheEntireSourceCollection(@TempDir Path temporaryDirectory) throws Exception {
        Path jarPath = temporaryDirectory.resolve("loot.jar");
        try (OutputStream output = Files.newOutputStream(jarPath);
                JarOutputStream jar = new JarOutputStream(output);
                var input = new ClassPathResource("data/loot.json").getInputStream()) {
            jar.putNextEntry(new JarEntry("data/loot.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader jarLoader = new URLClassLoader(
                new java.net.URL[] {jarPath.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            Thread.currentThread().setContextClassLoader(jarLoader);
            TreasureTableCatalog packaged = new TreasureTableCatalog(
                    objectMapper, new TreasureTableMapper(), sourceRegistry);
            assertEquals(75, packaged.size());
            assertEquals(25, packaged.dragonMundaneItemRows().size());
            assertNotNull(packaged.findById("treasure-table:FTD:dragon:wyrmling"));
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    private static java.util.Set<String> fieldPaths(JsonNode value) {
        java.util.Set<String> paths = new HashSet<>();
        collectPaths("", value, paths);
        return paths;
    }

    private static void collectPaths(String path, JsonNode value, java.util.Set<String> paths) {
        if (!path.isEmpty()) {
            paths.add(path);
        }
        if (value.isArray()) {
            for (JsonNode member : value) {
                collectPaths(path + "[]", member, paths);
            }
        } else if (value.isObject()) {
            value.fields().forEachRemaining(field -> collectPaths(
                    path.isEmpty() ? field.getKey() : path + "." + field.getKey(), field.getValue(), paths));
        }
    }
}
