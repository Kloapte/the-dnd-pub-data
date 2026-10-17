package com.thedndpub.data.deity;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.DeitiesApiExceptionHandler;
import com.thedndpub.data.controllers.DeitiesController;
import com.thedndpub.data.dto.deity.DeityCopyResolver;
import com.thedndpub.data.dto.deity.DeityDefinition;
import com.thedndpub.data.dto.deity.DeityMapper;
import com.thedndpub.data.dto.deity.ResolvedDeityDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.ImageEntryDte;
import com.thedndpub.data.dte.common.entry.InlineEntryDte;
import com.thedndpub.data.dte.common.entry.InsetEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.QuoteEntryDte;
import com.thedndpub.data.dte.common.entry.ReadaloudEntryDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.deity.DeitiesFileDte;
import com.thedndpub.data.dte.deity.DeityDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ReferenceTargetKind;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.services.DeitiesService;
import com.thedndpub.data.services.DeityCatalog;
import com.thedndpub.data.services.SourceRegistry;
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
class DeityCatalogTests {
    @Autowired
    private DeityCatalog catalog;

    @Autowired
    private DeitiesService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    @Test
    void loadsAllDefinitionsWithRegisteredSourcesAndUniquePantheonAwareIds() {
        assertEquals(563, catalog.size());
        assertEquals(Map.ofEntries(
                Map.entry("DMG", 20), Map.entry("DSotDQ", 21), Map.entry("EGW", 32),
                Map.entry("ERLW", 21), Map.entry("FRHoF", 42), Map.entry("GoS", 1),
                Map.entry("MTF", 80), Map.entry("MOT", 15), Map.entry("PHB", 195),
                Map.entry("SCAG", 99), Map.entry("VGM", 9), Map.entry("VRGR", 1), Map.entry("XDMG", 27)),
                sourceCounts());
        assertEquals(Map.ofEntries(
                Map.entry("Celtic", 14), Map.entry("Dawn War", 20), Map.entry("Drow", 14),
                Map.entry("Dragonlance", 42), Map.entry("Dwarven", 33), Map.entry("Duergar", 2),
                Map.entry("Eberron", 42), Map.entry("Elven", 41), Map.entry("Egyptian", 14),
                Map.entry("Exandria", 32), Map.entry("Faerûnian", 90), Map.entry("Forgotten Realms", 37),
                Map.entry("Gnome", 13), Map.entry("Gnomish", 9), Map.entry("Greek", 19),
                Map.entry("Greyhawk", 52), Map.entry("Halfling", 13), Map.entry("Nonhuman", 24),
                Map.entry("Norse", 20), Map.entry("Orc", 12), Map.entry("Theros", 15),
                Map.entry("Unknown", 2), Map.entry("Yuan-ti", 3)), pantheonCounts());
        assertEquals(563, catalog.definitions().stream().map(value -> value.id().value()).distinct().count());
        assertEquals(563, catalog.definitions().stream()
                .map(value -> value.source().identity().code() + "\u0000" + value.pantheon() + "\u0000" + value.name())
                .distinct().count());
        assertTrue(catalog.definitions().stream().allMatch(value -> value.pantheon() != null
                && !value.pantheon().isBlank()
                && sourceRegistry.find(value.source().identity().code()) != null));
        assertEquals(49, catalog.definitions().stream().filter(value -> Boolean.TRUE.equals(value.srd())).count());
        assertEquals(37, catalog.definitions().stream().filter(value -> Boolean.TRUE.equals(value.basicRules())).count());
        assertEquals(15, catalog.definitions().stream().filter(value -> Boolean.TRUE.equals(value.piety())).count());
        assertTrue(catalog.definitions().stream().allMatch(value -> value.srd() != Boolean.FALSE
                && value.basicRules() != Boolean.FALSE && value.piety() != Boolean.FALSE));
        assertTrue(catalog.summaries().stream().allMatch(value -> value.id().value().startsWith("deity:")));
        assertEquals(catalog.summaries(), catalog.summaries().stream()
                .sorted(java.util.Comparator.comparing(value -> value.name().toLowerCase(java.util.Locale.ROOT)))
                .toList());
        assertThrows(UnsupportedOperationException.class, () -> catalog.definitions().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.summaries().clear());
    }

    @Test
    void preservesTheFivePhbNameCollisionsAndAllowsPantheonDisambiguation() {
        Map<String, String> pantheonPairs = Map.of(
                "Oghma", "Celtic|Forgotten Realms",
                "Silvanus", "Celtic|Forgotten Realms",
                "Surtur", "Nonhuman|Norse",
                "Thrym", "Nonhuman|Norse",
                "Tyr", "Forgotten Realms|Norse");

        pantheonPairs.forEach((name, expectedPantheons) -> {
            List<DeityDefinition> matches = catalog.findByName(name).stream()
                    .filter(value -> value.source().identity().code().equals("PHB"))
                    .toList();
            assertEquals(2, matches.size(), name);
            assertEquals(expectedPantheons,
                    matches.get(0).pantheon() + "|" + matches.get(1).pantheon(), name);
            assertEquals(2, service.byName(name, "PHB", null).size(), name);
            assertEquals(1, service.byName(name, "PHB", matches.getFirst().pantheon()).size(), name);
        });

        assertEquals("deity:PHB:forgotten-realms:tyr", DeityMapper.idFor(
                "Tyr", "PHB", "Forgotten Realms").value());
        assertEquals("deity:PHB:norse:tyr", DeityMapper.idFor("Tyr", "PHB", "Norse").value());
    }

    @Test
    void resolvesSixAuthoredCopiesWithoutMergingDescriptiveEntries() {
        List<DeityDefinition> copied = catalog.definitions().stream()
                .filter(value -> value.copy() != null)
                .toList();
        assertEquals(6, copied.size());
        assertTrue(copied.stream().allMatch(value -> value.copyLineage().size() == 1));
        assertTrue(copied.stream().allMatch(value -> value.copyLineage().getFirst().value()
                .startsWith("deity:SCAG:orc:")));
        assertTrue(copied.stream().allMatch(value -> value.inheritedFields().containsAll(
                List.of("alignment", "domains", "symbol"))));
        assertTrue(copied.stream().allMatch(value -> value.authoredData().alignment() == null
                && value.authoredData().domains() == null && value.authoredData().symbol() == null));
        assertTrue(copied.stream().allMatch(value -> !value.authoredEntries().isEmpty()));
        assertTrue(copied.stream().allMatch(value -> value.customExtensionOf() != null));
    }

    @Test
    void retainsAuthoredFieldsAndAllObservedEntryVariants() {
        assertEquals(563, catalog.definitions().stream().filter(value -> value.authoredData().page() != null).count());
        assertEquals(463, catalog.definitions().stream().filter(value -> value.authoredData().alignment() != null).count());
        assertEquals(464, catalog.definitions().stream().filter(value -> value.authoredData().domains() != null).count());
        assertEquals(496, catalog.definitions().stream().filter(value -> value.authoredData().symbol() != null).count());
        assertEquals(158, catalog.definitions().stream().filter(value -> value.symbolImage() != null).count());
        assertEquals(224, catalog.definitions().stream().mapToInt(value -> value.alternateNames().size()).sum());
        assertEquals(65, catalog.definitions().stream().mapToInt(value -> value.additionalSources().size()).sum());
        assertTrue(catalog.definitions().stream().flatMap(value -> value.additionalSources().stream())
                .allMatch(value -> sourceRegistry.find(value.identity().code()) != null));
        assertEquals(15, countEntry(SectionEntryDte.class));
        assertEquals(15, countEntry(ImageEntryDte.class));
        assertEquals(15, countEntry(ReadaloudEntryDte.class));
        assertEquals(85, countEntry(ListEntryDte.class));
        assertEquals(63, countEntry(ListItemEntryDte.class));
        assertEquals(354, countEntry(NamedEntryDte.class));
        assertEquals(30, countEntry(TableEntryDte.class));
        assertEquals(7, countEntry(InsetEntryDte.class));
        assertEquals(1, countEntry(QuoteEntryDte.class));
        assertTrue(catalog.definitions().stream().flatMap(value -> value.additionalSources().stream())
                .allMatch(value -> sourceRegistry.find(value.identity().code()) != null));
    }

    @Test
    void rejectsUnknownFieldsAndInvalidCopyTargets() throws Exception {
        String unknownRoot = "{\"_meta\":{\"internalCopies\":[\"deity\"]},\"deity\":[{"
                + "\"name\":\"Test\",\"source\":\"PHB\",\"page\":1,\"pantheon\":\"Test\","
                + "\"unexpected\":true}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(DeitiesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownRoot));

        String unknownImage = "{\"name\":\"Test\",\"source\":\"PHB\",\"page\":1,"
                + "\"pantheon\":\"Test\",\"symbolImg\":{\"type\":\"image\",\"href\":{"
                + "\"type\":\"internal\",\"path\":\"test.webp\"},\"unexpected\":true}}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(DeityDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownImage));

        String unknownReadaloud = "{\"name\":\"Test\",\"source\":\"PHB\",\"page\":1,"
                + "\"pantheon\":\"Test\",\"entries\":[{\"type\":\"insetReadaloud\","
                + "\"entries\":[],\"unexpected\":true}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(DeityDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownReadaloud));

        DeityDte missingParent = new DeityDte("Test", "PHB", 1,
                new com.thedndpub.data.dte.deity.DeityCopyDte("Missing", "PHB", "Test"), "Test",
                null, null, null, null, null, null, null, null, "Missing|Test|PHB",
                List.of(), List.of(), null, null, null, null, null, null);
        DeitiesFileDte synthetic = new DeitiesFileDte(
                new com.thedndpub.data.dte.deity.DeitiesMetadataDte(List.of("deity")), List.of(missingParent));
        assertThrows(IllegalStateException.class, () -> new DeityCopyResolver().resolve(synthetic));
    }

    @Test
    void preservesFalseAndZeroValuesAndRejectsUnregisteredSources() throws Exception {
        String authoredJson = "{\"name\":\"Synthetic Deity\",\"source\":\"PHB\",\"page\":0,"
                + "\"pantheon\":\"Synthetic\",\"srd\":false,\"basicRules\":false,\"piety\":false}";
        DeityDte authored = objectMapper.readerFor(DeityDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(authoredJson);
        assertEquals(0, authored.page());
        assertEquals(Boolean.FALSE, authored.srd());
        assertEquals(Boolean.FALSE, authored.basicRules());
        assertEquals(Boolean.FALSE, authored.piety());
        DeityDefinition normalized = new DeityMapper().map(new ResolvedDeityDte(
                authored, List.of(), List.of(), null, List.of(), List.of()));
        assertEquals(0, normalized.source().page());
        assertEquals(Boolean.FALSE, normalized.srd());
        assertEquals(Boolean.FALSE, normalized.basicRules());
        assertEquals(Boolean.FALSE, normalized.piety());

        DeityDte unknownSource = new DeityDte(
                "Synthetic Deity", "UNKNOWN", 1, null, "Synthetic", null, null, null,
                null, null, null, null, null, null, List.of(), List.of(), null, null, null, null, null, null);
        DeitiesFileDte source = new DeitiesFileDte(
                new com.thedndpub.data.dte.deity.DeitiesMetadataDte(List.of("deity")), List.of(unknownSource));
        assertThrows(IllegalStateException.class, () -> new DeityCatalog(
                source, new DeityMapper(), sourceRegistry));
    }

    @Test
    void preservesResolverOutcomesWithoutInferringUnsupportedTargets() {
        List<com.thedndpub.data.references.ResolutionResult> deityReferences =
                resolutionCoordinator.snapshot().results().stream()
                        .filter(value -> value.reference().owner().catalog().equals("deity"))
                        .toList();
        assertEquals(33, deityReferences.size());
        assertEquals(17, deityReferences.stream()
                .filter(value -> value.reference().targetKind() == ReferenceTargetKind.SPELL).count());
        assertEquals(15, deityReferences.stream()
                .filter(value -> value.reference().targetKind() == ReferenceTargetKind.ITEM).count());
        assertEquals(1, deityReferences.stream()
                .filter(value -> value.reference().targetKind() == ReferenceTargetKind.SKILL).count());
        assertTrue(deityReferences.stream().allMatch(value -> value.reference().fieldLocator().startsWith("entries[")));
        assertTrue(deityReferences.stream().filter(value -> value.outcome() == ResolutionOutcome.RESOLVED)
                .allMatch(value -> value.resolvedTargetId() != null));
        assertTrue(deityReferences.stream().filter(value -> value.outcome() != ResolutionOutcome.RESOLVED)
                .allMatch(value -> value.resolvedTargetId() == null));
        assertEquals(19, deityReferences.stream()
                .filter(value -> value.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(0, deityReferences.stream()
                .filter(value -> value.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(8, deityReferences.stream()
                .filter(value -> value.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(6, deityReferences.stream()
                .filter(value -> value.outcome() == ResolutionOutcome.DEFERRED).count());

        List<com.thedndpub.data.references.ResolutionResult> previous =
                resolutionCoordinator.snapshot().results().stream()
                        .filter(value -> !value.reference().owner().catalog().equals("deity"))
                        .filter(value -> !value.reference().owner().catalog().equals("treasure-table"))
                        .toList();
        assertEquals(4_881, previous.size());
        assertEquals(2_020, previous.stream().filter(value -> value.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(1_701, previous.stream().filter(value -> value.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(65, previous.stream().filter(value -> value.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1_095, previous.stream().filter(value -> value.outcome() == ResolutionOutcome.DEFERRED).count());
    }

    @Test
    void publicDtosOmitLoreMarkupMediaAndImporterMetadata() throws Exception {
        DeityDefinition deity = catalog.findById("deity:PHB:forgotten-realms:tyr");
        assertNotNull(deity);
        String json = objectMapper.writeValueAsString(service.byId(deity.id().value()));
        assertTrue(json.contains("\"pantheon\":\"Forgotten Realms\""));
        assertTrue(json.contains("\"alignment\":[\"L\",\"G\"]"));
        assertTrue(json.contains("\"domains\":[\"War\",\"Order\"]"));
        assertFalse(json.contains("title"));
        assertFalse(json.contains("symbol"));
        assertFalse(json.contains("entries"));
        assertFalse(json.contains("worshipers"));
        assertFalse(json.contains("symbolImg"));
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("customExtensionOf"));
        assertFalse(json.contains("copyLineage"));
        assertFalse(json.contains("srd"));
    }

    @Test
    void endpointsFilterBySourcePantheonAlignmentAndDomainAndPreserveAmbiguity() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new DeitiesController(service))
                .setControllerAdvice(new DeitiesApiExceptionHandler())
                .build();
        mockMvc.perform(get("/api/deities").param("source", "PHB").param("pantheon", "Norse")
                        .param("alignment", "N").param("domain", "Knowledge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].pantheon").value(org.hamcrest.Matchers.everyItem(
                        org.hamcrest.Matchers.equalTo("Norse"))));
        mockMvc.perform(get("/api/deities/by-name/Tyr").param("source", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());
        mockMvc.perform(get("/api/deities/by-name/Tyr").param("source", "PHB")
                        .param("pantheon", "Forgotten Realms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/deities/deity:missing:test:test"))
                .andExpect(status().isNotFound());
    }

    @Test
    void packagedJarClasspathLoadsTheCompleteCorpus(@TempDir Path temporaryDirectory) throws Exception {
        Path jarPath = temporaryDirectory.resolve("deities.jar");
        try (OutputStream output = Files.newOutputStream(jarPath);
                JarOutputStream jar = new JarOutputStream(output);
                var input = new ClassPathResource("data/deities.json").getInputStream()) {
            jar.putNextEntry(new JarEntry("data/deities.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader jarLoader = new URLClassLoader(
                new URL[] {jarPath.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            Thread.currentThread().setContextClassLoader(jarLoader);
            DeityCatalog packaged = new DeityCatalog(objectMapper, new DeityMapper(), sourceRegistry);
            assertEquals(563, packaged.size());
            assertNotNull(packaged.findById("deity:PHB:forgotten-realms:tyr"));
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    private Map<String, Integer> sourceCounts() {
        return catalog.definitions().stream().collect(Collectors.toMap(
                value -> value.source().identity().code(),
                value -> 1,
                Integer::sum));
    }

    private Map<String, Integer> pantheonCounts() {
        return catalog.definitions().stream().collect(Collectors.toMap(
                DeityDefinition::pantheon,
                value -> 1,
                Integer::sum));
    }

    private long countEntry(Class<?> entryType) {
        return catalog.definitions().stream()
                .flatMap(value -> flatten(value.authoredEntries()).stream())
                .filter(entryType::isInstance)
                .count();
    }

    private static List<EntryDte> flatten(List<EntryDte> entries) {
        if (entries == null || entries.isEmpty()) {
            return List.of();
        }
        java.util.ArrayList<EntryDte> flattened = new java.util.ArrayList<>();
        for (EntryDte entry : entries) {
            flattened.add(entry);
            if (entry instanceof NamedEntryDte named) {
                flattened.addAll(flatten(named.entries()));
            } else if (entry instanceof ListEntryDte list) {
                flattened.addAll(flatten(list.items()));
            } else if (entry instanceof ListItemEntryDte item) {
                flattened.addAll(flatten(item.entries()));
            } else if (entry instanceof SectionEntryDte section) {
                flattened.addAll(flatten(section.entries()));
            } else if (entry instanceof InsetEntryDte inset) {
                flattened.addAll(flatten(inset.entries()));
            } else if (entry instanceof ReadaloudEntryDte readaloud) {
                flattened.addAll(flatten(readaloud.entries()));
            } else if (entry instanceof InlineEntryDte inline) {
                flattened.addAll(flatten(inline.entries()));
            } else if (entry instanceof QuoteEntryDte quote) {
                flattened.addAll(flatten(quote.entries()));
            } else if (entry instanceof TableEntryDte table && table.rows() != null) {
                table.rows().stream().flatMap(List::stream)
                        .filter(com.thedndpub.data.dte.common.entry.StructuredTableCellDte.class::isInstance)
                        .map(com.thedndpub.data.dte.common.entry.StructuredTableCellDte.class::cast)
                        .forEach(cell -> flattened.addAll(flatten(cell.entries())));
            }
        }
        return List.copyOf(flattened);
    }
}
