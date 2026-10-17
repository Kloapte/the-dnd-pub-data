package com.thedndpub.data.variantrule;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.VariantRulesApiExceptionHandler;
import com.thedndpub.data.controllers.VariantRulesController;
import com.thedndpub.data.dto.variantrule.VariantRuleDefinition;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.EntryTableCellDte;
import com.thedndpub.data.dte.common.entry.ImageEntryDte;
import com.thedndpub.data.dte.common.entry.InlineEntryDte;
import com.thedndpub.data.dte.common.entry.LinkEntryDte;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.StructuredTableCellDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.variantrule.VariantRuleDte;
import com.thedndpub.data.dte.variantrule.VariantRulesFileDte;
import com.thedndpub.data.dte.variantrule.VariantRulesFileDte;
import com.thedndpub.data.dto.variantrule.VariantRuleMapper;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.references.ReferenceTargetKind;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.VariantRuleCatalog;
import com.thedndpub.data.services.VariantRulesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class VariantRuleCatalogTests {
    @Autowired
    private VariantRuleCatalog catalog;

    @Autowired
    private VariantRulesService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    @Test
    void loadsCompleteCorpusWithAuthoredClassificationsAndUniqueSourceAwareIds() {
        assertEquals(231, catalog.size());
        assertEquals(231, catalog.summaries().size());
        assertEquals(Map.ofEntries(
                Map.entry("AAG", 4),
                Map.entry("AI", 10),
                Map.entry("CoS", 1),
                Map.entry("DMG", 44),
                Map.entry("ESK", 1),
                Map.entry("GoS", 9),
                Map.entry("MM", 3),
                Map.entry("PHB", 13),
                Map.entry("PSI", 1),
                Map.entry("SCAG", 1),
                Map.entry("TCE", 3),
                Map.entry("VGM", 1),
                Map.entry("VRGR", 3),
                Map.entry("XGE", 22),
                Map.entry("XPHB", 115)), sourceCounts());
        assertEquals(231, catalog.definitions().stream().map(value -> value.id().value()).distinct().count());
        assertEquals(231, catalog.definitions().stream()
                .map(value -> value.source().identity().code() + "\u0000" + value.name())
                .distinct().count());
        assertEquals(Map.of("C", 115L, "O", 43L, "V", 17L, "VO", 16L, "[absent]", 40L),
                catalog.definitions().stream().collect(java.util.stream.Collectors.groupingBy(
                        value -> value.ruleType() == null ? "[absent]" : value.ruleType(),
                        java.util.stream.Collectors.counting())));
        assertEquals(Map.of("[absent]", 186L, "entries", 17L, "section", 28L),
                catalog.definitions().stream().collect(java.util.stream.Collectors.groupingBy(
                        value -> value.rootEntryType() == null ? "[absent]" : value.rootEntryType(),
                        java.util.stream.Collectors.counting())));
        assertEquals(188, catalog.definitions().stream().mapToInt(VariantRuleDefinition::tableCount).sum());
        assertEquals(5, catalog.definitions().stream().mapToInt(VariantRuleDefinition::sectionCount).sum());
        assertEquals(2, catalog.definitions().stream().flatMap(value -> value.reprintedAs().stream())
                .filter(com.thedndpub.data.dte.variantrule.VariantRuleReprintUidDte.class::isInstance)
                .count());
        assertTrue(catalog.definitions().stream().allMatch(value -> sourceRegistry.find(
                value.source().identity().code()) != null));
        assertEquals(13, catalog.definitions().stream().mapToInt(value -> value.reprintedAs().size()).sum());
        assertEquals(5, catalog.definitions().stream().mapToInt(value -> value.additionalSources().size()).sum());
        assertTrue(catalog.definitions().stream().flatMap(value -> value.additionalSources().stream())
                .allMatch(value -> sourceRegistry.find(value.source()) != null));
        assertEquals(catalog.summaries(), catalog.summaries().stream()
                .sorted(java.util.Comparator.comparing(
                        value -> value.name().toLowerCase(java.util.Locale.ROOT))).toList());
        assertThrows(UnsupportedOperationException.class, () -> catalog.summaries().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.definitions().clear());
    }

    @Test
    void retainsAllObservedNestedEntryVariantsAndTables() {
        List<EntryDte> entries = catalog.definitions().stream()
                .flatMap(value -> value.authoredEntries().stream())
                .toList();
        assertEquals(1, count(entries, InlineEntryDte.class));
        assertEquals(1, count(entries, LinkEntryDte.class));
        assertEquals(11, count(entries, ImageEntryDte.class));
        assertEquals(188, count(entries, TableEntryDte.class));
        assertEquals(2, catalog.definitions().stream()
                .flatMap(value -> flatten(value.authoredEntries()).stream())
                .filter(TableEntryDte.class::isInstance)
                .map(TableEntryDte.class::cast)
                .filter(table -> Boolean.TRUE.equals(table.isNameGenerator()))
                .count());
        assertTrue(catalog.definitions().stream().allMatch(value -> !value.authoredEntries().isEmpty()));
        assertTrue(catalog.definitions().stream().anyMatch(value -> value.sectionCount() > 0));
        assertTrue(catalog.definitions().stream().anyMatch(value -> value.tableCount() > 0));
        assertTrue(catalog.definitions().stream().flatMap(value -> flatten(value.authoredEntries()).stream())
                .filter(ImageEntryDte.class::isInstance)
                .map(ImageEntryDte.class::cast)
                .allMatch(value -> value.href() != null && "internal".equals(value.href().type())));
    }

    @Test
    void nameLookupPreservesSameNameDefinitionsAcrossSources() {
        assertEquals(2, catalog.findByName("Familiars").size());
        assertEquals("MM", catalog.findByName("Familiars").getFirst().source().identity().code());
        assertEquals(1, catalog.findByName("Familiars").stream()
                .filter(value -> value.source().identity().code().equals("VGM"))
                .count());
        assertEquals(1, service.byName("Familiars", "VGM").size());
    }

    @Test
    void rejectsUnknownRootNestedAndPolymorphicProperties() throws Exception {
        String unknownRoot = "{\"name\":\"Test Rule\",\"source\":\"PHB\",\"page\":1,"
                + "\"entries\":[],\"unrecognized\":true}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(VariantRuleDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownRoot));

        String unknownImage = "{\"name\":\"Test Rule\",\"source\":\"PHB\",\"page\":1,"
                + "\"entries\":[{\"type\":\"image\",\"href\":{\"type\":\"internal\","
                + "\"path\":\"test.webp\"},\"unexpected\":true}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(VariantRuleDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownImage));

        String unknownTable = "{\"name\":\"Test Rule\",\"source\":\"PHB\",\"page\":1,"
                + "\"entries\":[{\"type\":\"table\",\"rows\":[],\"unrecognized\":1}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(VariantRuleDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownTable));

        String unknownReprint = "{\"name\":\"Test Rule\",\"source\":\"PHB\",\"page\":1,"
                + "\"entries\":[],\"reprintedAs\":[{\"tag\":\"hazard\",\"uid\":\"Falling|XPHB\","
                + "\"extra\":true}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(VariantRuleDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownReprint));

        VariantRuleDte unknownSource = new VariantRuleDte(
                "Test Rule", "UNKNOWN", 1, null, null, null, null, null, List.of(), List.of(), List.of());
        assertThrows(IllegalStateException.class, () -> new VariantRuleCatalog(
                new VariantRulesFileDte(List.of(unknownSource)), new VariantRuleMapper(), sourceRegistry));
    }

    @Test
    void publicProjectionExcludesAuthoredEntriesMarkupAndSourceFlags() throws Exception {
        VariantRuleDefinition definition = catalog.findById("variant-rule:XPHB:ability-check");
        assertNotNull(definition);
        String json = objectMapper.writeValueAsString(service.byId(definition.id().value()));
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("entries"));
        assertFalse(json.contains("rows"));
        assertFalse(json.contains("href"));
        assertFalse(json.contains("credit"));
        assertFalse(json.contains("reprintedAs"));
        assertFalse(json.contains("additionalSources"));
        assertFalse(json.contains("basicRules2024"));
        assertFalse(json.contains("srd52"));
        assertTrue(json.contains("\"name\":\"Ability Check\""));
        assertTrue(json.contains("\"page\":360"));
        assertTrue(json.contains("\"ruleType\":\"C\""));
        assertTrue(json.contains("\"tableCount\":"));
        String authoredProse = flatten(definition.authoredEntries()).stream()
                .filter(TextEntryDte.class::isInstance)
                .map(TextEntryDte.class::cast)
                .map(TextEntryDte::text)
                .findFirst()
                .orElseThrow();
        assertFalse(json.contains(authoredProse));

        VariantRuleDefinition referencedDefinition = catalog.definitions().stream()
                .filter(value -> !service.byId(value.id().value()).crossCatalogReferences().isEmpty())
                .findFirst()
                .orElseThrow();
        String referencedJson = objectMapper.writeValueAsString(service.byId(referencedDefinition.id().value()));
        assertTrue(referencedJson.contains("crossCatalogReferences"));
        assertFalse(referencedJson.contains("candidates"));
    }

    @Test
    void addsOnlySupportedMarkupReferencesAndPreservesHistoricalSnapshot() {
        List<com.thedndpub.data.references.ResolutionResult> results = resolutionCoordinator.snapshot().results();
        List<com.thedndpub.data.references.ResolutionResult> added = results.stream()
                .filter(value -> value.reference().owner().catalog().equals("variant-rule"))
                .toList();
        assertEquals(361, added.size());
        assertEquals(137, added.stream().filter(value -> value.reference().targetKind() == ReferenceTargetKind.ITEM)
                .count());
        assertEquals(144, added.stream().filter(value -> value.reference().targetKind() == ReferenceTargetKind.SKILL)
                .count());
        assertEquals(80, added.stream().filter(value -> value.reference().targetKind() == ReferenceTargetKind.SPELL)
                .count());
        assertEquals(102, added.stream().filter(value -> value.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(214, added.stream().filter(value -> value.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(21, added.stream().filter(value -> value.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(24, added.stream().filter(value -> value.outcome() == ResolutionOutcome.DEFERRED).count());
        assertOutcomeCounts(added, ReferenceTargetKind.ITEM, 83, 10, 21, 23);
        assertOutcomeCounts(added, ReferenceTargetKind.SKILL, 12, 132, 0, 0);
        assertOutcomeCounts(added, ReferenceTargetKind.SPELL, 7, 72, 0, 1);
        assertTrue(added.stream().allMatch(value -> value.reference().fieldLocator().startsWith("entries[")));
        assertTrue(added.stream().filter(value -> value.outcome() == ResolutionOutcome.RESOLVED)
                .allMatch(value -> value.resolvedTargetId() != null));
        assertTrue(added.stream().filter(value -> value.outcome() != ResolutionOutcome.RESOLVED)
                .allMatch(value -> value.resolvedTargetId() == null));

        List<com.thedndpub.data.references.ResolutionResult> previous = results.stream()
                .filter(value -> !value.reference().owner().catalog().equals("variant-rule"))
                .filter(value -> !value.reference().owner().catalog().equals("deity"))
                .filter(value -> !value.reference().owner().catalog().equals("treasure-table"))
                .toList();
        assertEquals(4_520, previous.size());
        assertEquals(1_918, previous.stream().filter(value -> value.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(1_487, previous.stream().filter(value -> value.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(44, previous.stream().filter(value -> value.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1_071, previous.stream().filter(value -> value.outcome() == ResolutionOutcome.DEFERRED).count());
    }

    @Test
    void listAndNameEndpointsSupportSourceAndRuleTypeFilters() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new VariantRulesController(service))
                .setControllerAdvice(new VariantRulesApiExceptionHandler())
                .build();
        mockMvc.perform(get("/api/variant-rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
        mockMvc.perform(get("/api/variant-rules").param("source", "DMG").param("ruleType", "O"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source").value("DMG"))
                .andExpect(jsonPath("$[0].ruleType").value("O"));
        mockMvc.perform(get("/api/variant-rules/by-name/{name}", "Ability Check").param("source", "XPHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.value").value("variant-rule:XPHB:ability-check"));
        mockMvc.perform(get("/api/variant-rules/variant-rule:missing:unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void classpathSourceLoadsFromPackagedJar(@TempDir Path temporaryDirectory) throws Exception {
        Path jarPath = temporaryDirectory.resolve("variant-rules.jar");
        try (OutputStream file = Files.newOutputStream(jarPath);
                JarOutputStream jar = new JarOutputStream(file);
                var input = new ClassPathResource("data/variantrules.json").getInputStream()) {
            jar.putNextEntry(new JarEntry("data/variantrules.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader jarLoader = new URLClassLoader(
                new URL[] { jarPath.toUri().toURL() }, ClassLoader.getPlatformClassLoader())) {
            Thread.currentThread().setContextClassLoader(jarLoader);
            VariantRuleCatalog packaged = new VariantRuleCatalog(
                    objectMapper, new VariantRuleMapper(), sourceRegistry);
            assertEquals(231, packaged.size());
            assertEquals("variant-rule:XPHB:ability-check", packaged.findById(
                    "variant-rule:XPHB:ability-check").id().value());
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    private Map<String, Integer> sourceCounts() {
        Map<String, Integer> result = new java.util.TreeMap<>();
        catalog.definitions().forEach(value -> result.merge(value.source().identity().code(), 1, Integer::sum));
        return result;
    }

    private static void assertOutcomeCounts(
            List<com.thedndpub.data.references.ResolutionResult> results,
            ReferenceTargetKind targetKind,
            long resolved,
            long ambiguous,
            long unresolved,
            long deferred) {
        List<com.thedndpub.data.references.ResolutionResult> matches = results.stream()
                .filter(value -> value.reference().targetKind() == targetKind)
                .toList();
        assertEquals(resolved, matches.stream()
                .filter(value -> value.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(ambiguous, matches.stream()
                .filter(value -> value.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(unresolved, matches.stream()
                .filter(value -> value.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(deferred, matches.stream()
                .filter(value -> value.outcome() == ResolutionOutcome.DEFERRED).count());
    }

    private static long count(List<EntryDte> entries, Class<?> type) {
        return flatten(entries).stream().filter(type::isInstance).count();
    }

    private static List<Object> flatten(List<EntryDte> entries) {
        List<Object> values = new ArrayList<>();
        if (entries == null) {
            return values;
        }
        for (EntryDte entry : entries) {
            values.add(entry);
            if (entry instanceof com.thedndpub.data.dte.common.entry.NamedEntryDte named) {
                values.addAll(flatten(named.entries()));
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.ListEntryDte list) {
                values.addAll(flatten(list.items()));
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.ListItemEntryDte item) {
                values.addAll(flatten(item.entries()));
            } else if (entry instanceof TableEntryDte table && table.rows() != null) {
                for (List<EntryTableCellDte> row : table.rows()) {
                    for (EntryTableCellDte cell : row) {
                        if (cell instanceof StructuredTableCellDte structured) {
                            values.addAll(flatten(structured.entries()));
                        } else if (cell instanceof RollTableCellDte roll && roll.entry() != null) {
                            values.addAll(flatten(List.of(roll.entry())));
                        }
                    }
                }
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.QuoteEntryDte quote) {
                values.addAll(flatten(quote.entries()));
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.InsetEntryDte inset) {
                values.addAll(flatten(inset.entries()));
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.SectionEntryDte section) {
                values.addAll(flatten(section.entries()));
            } else if (entry instanceof InlineEntryDte inline) {
                values.addAll(flatten(inline.entries()));
            }
        }
        return values;
    }
}
