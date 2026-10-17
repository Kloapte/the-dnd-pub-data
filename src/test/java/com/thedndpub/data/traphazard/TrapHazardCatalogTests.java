package com.thedndpub.data.traphazard;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.HazardsController;
import com.thedndpub.data.controllers.TrapsController;
import com.thedndpub.data.dto.traphazard.HazardDefinition;
import com.thedndpub.data.dto.traphazard.TrapDefinition;
import com.thedndpub.data.dte.traphazard.TrapHazardsFileDte;
import com.thedndpub.data.services.HazardCatalog;
import com.thedndpub.data.services.HazardsService;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.TrapCatalog;
import com.thedndpub.data.services.TrapsService;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionResult;
import com.thedndpub.data.references.ResolutionOutcome;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TrapHazardCatalogTests {
    @TempDir
    private Path temporaryDirectory;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TrapCatalog traps;
    @Autowired
    private HazardCatalog hazards;
    @Autowired
    private SourceRegistry sourceRegistry;
    @Autowired
    private TrapsService trapsService;
    @Autowired
    private HazardsService hazardsService;
    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    @Test
    void loadsTheCompleteTypedCorpusAndValidatesSourcesAndStableIds() throws Exception {
        TrapHazardsFileDte source;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/trapshazards.json")) {
            assertNotNull(input);
            source = objectMapper.readerFor(TrapHazardsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        }

        assertEquals(37, source.trap().size());
        assertEquals(73, source.hazard().size());
        assertEquals(37, traps.size());
        assertEquals(73, hazards.size());
        assertEquals(37, new HashSet<>(traps.definitions().stream()
                .map(value -> value.id().value()).toList()).size());
        assertEquals(73, new HashSet<>(hazards.definitions().stream()
                .map(value -> value.id().value()).toList()).size());
        assertTrue(traps.definitions().stream()
                .allMatch(value -> sourceRegistry.find(value.source().identity().code()) != null));
        assertTrue(hazards.definitions().stream()
                .allMatch(value -> sourceRegistry.find(value.source().identity().code()) != null));
        assertEquals(Map.of("AZfyT", 1L, "BMT", 3L, "DMG", 8L, "RtG", 1L, "VRGR", 4L,
                        "XDMG", 8L, "XGE", 12L), sourceCounts(traps.definitions().stream()
                        .map(value -> value.source().identity().code()).toList()));
        assertEquals(Map.of("BGDIA", 1L, "DMG", 16L, "FRAiF", 10L, "IDRotF", 2L,
                        "NF", 5L, "TCE", 7L, "VGM", 2L, "XDMG", 25L, "XPHB", 5L),
                sourceCounts(hazards.definitions().stream()
                        .map(value -> value.source().identity().code()).toList()));
        assertEquals(java.util.Set.of("CMPX", "HAUNT", "MAG", "MECH", "SMPL", "TRP"),
                traps.definitions().stream().map(TrapDefinition::type).collect(java.util.stream.Collectors.toSet()));
        assertEquals(java.util.Set.of("ENV", "EST", "GEN", "WLD", "WTH"), hazards.definitions().stream()
                .map(HazardDefinition::type).filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet()));
        assertEquals(8, traps.definitions().stream().mapToInt(value -> value.reprintedAs().size()).sum());
        assertEquals(15, hazards.definitions().stream().mapToInt(value -> value.reprintedAs().size()).sum());
        assertEquals("trap:DMG:collapsing-roof", traps.findByName("Collapsing Roof").getFirst().id().value());
        assertEquals(2, traps.findByName("Collapsing Roof").size());
        assertEquals(2, hazards.findByName("Avalanche").size());
        assertThrows(UnsupportedOperationException.class, () -> traps.definitions().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> traps.definitions().getFirst().entries().clear());
    }

    @Test
    void rejectsUnknownRootAndNestedStageFieldsAndPreservesObservedStageVariants() throws Exception {
        assertThrows(Exception.class, () -> objectMapper.readerFor(TrapHazardsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"trap\":[{\"name\":\"x\",\"unknown\":1}]}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(TrapHazardsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"trap\":[{\"name\":\"x\",\"eActive\":[{\"type\":\"entries\","
                        + "\"style\":\"list-hang-notitle\",\"items\":[],\"unknown\":true}]}]}"));
        assertTrue(traps.definitions().stream().anyMatch(value -> !value.activeStages().isEmpty()));
        assertTrue(traps.definitions().stream().anyMatch(value -> !value.countermeasures().isEmpty()));
        assertTrue(traps.definitions().stream().anyMatch(value -> !value.entries().isEmpty()));
        assertTrue(hazards.definitions().stream().anyMatch(value -> !value.entries().isEmpty()));
    }

    @Test
    void preservesAuthoredMetadataWithoutPublishingDescriptiveText() throws Exception {
        TrapDefinition trap = traps.definitions().stream()
                .filter(value -> value.initiative() != null).findFirst().orElseThrow();
        HazardDefinition hazard = hazards.definitions().stream()
                .filter(value -> !value.otherSources().isEmpty()).findFirst().orElseThrow();
        assertFalse(trap.activeStages().isEmpty());
        assertFalse(hazard.otherSources().isEmpty());
        assertTrue(trap.rating().stream().allMatch(value -> value.tier() != null && value.threat() != null));
        assertTrue(hazards.definitions().stream().anyMatch(value -> value.type() == null));

        String trapJson = objectMapper.writeValueAsString(trapsService.findById(trap.id().value()));
        String hazardJson = objectMapper.writeValueAsString(hazardsService.findById(hazard.id().value()));
        assertFalse(trapJson.contains("entries"));
        assertFalse(trapJson.contains("trigger"));
        assertFalse(trapJson.contains("{@"));
        assertFalse(hazardJson.contains("entries"));
        assertFalse(hazardJson.contains("{@"));
        assertTrue(trapJson.contains("initiative"));
    }

    @Test
    void extractsSupportedAuthoredMarkupReferencesWithoutInferringTargets() {
        var references = resolutionCoordinator.snapshot().results().stream()
                .filter(result -> result.reference().owner().catalog().equals("trap")
                        || result.reference().owner().catalog().equals("hazard"))
                .toList();
        assertEquals(146, references.size());
        var trapReferences = references.stream()
                .filter(result -> result.reference().owner().catalog().equals("trap")).toList();
        var hazardReferences = references.stream()
                .filter(result -> result.reference().owner().catalog().equals("hazard")).toList();
        assertEquals(95, trapReferences.size());
        assertEquals(51, hazardReferences.size());
        assertEquals(35, count(trapReferences, ResolutionOutcome.RESOLVED));
        assertEquals(57, count(trapReferences, ResolutionOutcome.AMBIGUOUS));
        assertEquals(3, count(trapReferences, ResolutionOutcome.DEFERRED));
        assertEquals(25, count(hazardReferences, ResolutionOutcome.RESOLVED));
        assertEquals(24, count(hazardReferences, ResolutionOutcome.AMBIGUOUS));
        assertEquals(2, count(hazardReferences, ResolutionOutcome.DEFERRED));
        assertTrue(references.stream().allMatch(result -> !result.reference().fieldLocator().isBlank()));
        assertTrue(references.stream().filter(result -> result.outcome() == ResolutionOutcome.RESOLVED)
                .allMatch(result -> result.resolvedTargetId() != null
                        && result.reference().parsedSource() != null));
        assertTrue(references.stream().filter(result -> result.outcome() != ResolutionOutcome.RESOLVED)
                .allMatch(result -> result.resolvedTargetId() == null));
        for (String catalogName : java.util.List.of("trap", "hazard")) {
            var ownerReference = references.stream()
                    .filter(result -> result.reference().owner().catalog().equals(catalogName)).findFirst().orElseThrow();
            String definitionId = ownerReference.reference().owner().stableId();
            var projection = catalogName.equals("trap")
                    ? trapsService.findById(definitionId).crossCatalogReferences()
                    : hazardsService.findById(definitionId).crossCatalogReferences();
            assertFalse(projection.isEmpty());
            assertTrue(projection.stream().allMatch(reference -> reference.targetId() == null
                    || reference.authoredSource() != null));
        }
    }

    private static long count(java.util.List<ResolutionResult> results, ResolutionOutcome outcome) {
        return results.stream().filter(result -> result.outcome() == outcome).count();
    }

    private static Map<String, Long> sourceCounts(java.util.List<String> sources) {
        return sources.stream().collect(Collectors.groupingBy(source -> source, java.util.TreeMap::new,
                Collectors.counting()));
    }

    @Test
    void exposesSourceAndTypeFiltersNameLookupAndReadOnlyDetailEndpoints() throws Exception {
        MockMvc trapMvc = MockMvcBuilders.standaloneSetup(new TrapsController(trapsService)).build();
        MockMvc hazardMvc = MockMvcBuilders.standaloneSetup(new HazardsController(hazardsService)).build();
        trapMvc.perform(get("/api/traps").param("source", "DMG").param("type", "MECH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("DMG"));
        trapMvc.perform(get("/api/traps/by-name/Collapsing Roof").param("source", "XDMG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.value").value("trap:XDMG:collapsing-roof"));
        trapMvc.perform(get("/api/traps/trap:DMG:collapsing-roof"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries").doesNotExist());
        trapMvc.perform(get("/api/traps/not-found"))
                .andExpect(status().isNotFound());
        hazardMvc.perform(get("/api/hazards").param("source", "XDMG").param("type", "ENV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("XDMG"));
        hazardMvc.perform(get("/api/hazards/by-name/Avalanche").param("source", "TCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.value").value("hazard:TCE:avalanche"));
        hazardMvc.perform(get("/api/hazards/hazard:TCE:avalanche"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries").doesNotExist());
        hazardMvc.perform(get("/api/hazards/not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void readsTheCompleteCatalogFromPackagedJarClasspathResources() throws Exception {
        Path jarPath = temporaryDirectory.resolve("trapshazards.jar");
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/trapshazards.json");
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            assertNotNull(input);
            jar.putNextEntry(new JarEntry("data/trapshazards.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new java.net.URL[]{jarPath.toUri().toURL()}, null);
             InputStream input = new org.springframework.core.io.ClassPathResource(
                     "data/trapshazards.json", classLoader).getInputStream()) {
            TrapHazardsFileDte source = objectMapper.readerFor(TrapHazardsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
            assertEquals(37, source.trap().size());
            assertEquals(73, source.hazard().size());
        }
    }
}
