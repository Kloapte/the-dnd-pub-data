package com.thedndpub.data.bastion;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.BastionFacilitiesController;
import com.thedndpub.data.dto.bastion.FacilityDetailDto;
import com.thedndpub.data.dto.bastion.FacilitySpellcastingFocusOptions;
import com.thedndpub.data.dto.bastion.FacilitySpellcastingFocusRequirement;
import com.thedndpub.data.dto.bastion.FacilityMapper;
import com.thedndpub.data.dte.bastion.FacilityDte;
import com.thedndpub.data.dte.bastion.FacilityFileDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.services.BastionFacilitiesService;
import com.thedndpub.data.services.BastionFacilityCatalog;
import com.thedndpub.data.services.SourceRegistry;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BastionFacilitiesPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BastionFacilityCatalog catalog;

    @Autowired
    private BastionFacilitiesService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    @Test
    void fullCorpusLoadsStrictlyWithUniqueSourceAwareIdentities() throws Exception {
        FacilityFileDte source;
        try (InputStream input = new ClassPathResource("data/bastions.json").getInputStream()) {
            source = objectMapper.readerFor(FacilityFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
        assertEquals(70, source.facility().size());
        assertEquals(70, catalog.size());
        assertEquals(70, new HashSet<>(catalog.definitions().stream().map(value -> value.id()).toList()).size());
        assertTrue(catalog.definitions().stream()
                .allMatch(value -> sourceRegistry.find(value.source().identity().code()) != null));
        assertEquals(6, service.find(null, "basic", null).size());
        assertEquals(64, service.find(null, "special", null).size());
        assertEquals(9, service.find("AU", null, null).size());
        assertEquals(10, service.find("EFA", null, null).size());
        assertEquals(8, service.find("FRHoF", null, null).size());
        assertEquals(8, service.find("RHW", null, null).size());
        assertEquals(35, service.find("XDMG", null, null).size());
        assertEquals(21, service.find(null, null, 5).size());
        assertEquals(22, service.find(null, null, 9).size());
        assertEquals("facility:XDMG:smithy", catalog.findByName("Smithy").get(0).id());
    }

    @Test
    void prerequisitePolymorphismAndAuthoredMechanicsArePreserved() {
        var definitions = catalog.definitions();
        var focusLists = definitions.stream().flatMap(value -> value.prerequisites().stream())
                .map(value -> value.spellcastingFocus())
                .filter(FacilitySpellcastingFocusOptions.class::isInstance)
                .map(FacilitySpellcastingFocusOptions.class::cast)
                .toList();
        var focusFlags = definitions.stream().flatMap(value -> value.prerequisites().stream())
                .map(value -> value.spellcastingFocus())
                .filter(FacilitySpellcastingFocusRequirement.class::isInstance)
                .map(FacilitySpellcastingFocusRequirement.class::cast)
                .toList();
        assertFalse(focusLists.isEmpty());
        assertTrue(focusFlags.stream().anyMatch(value -> value.required()));
        assertTrue(focusFlags.stream().allMatch(value -> value.required()));
        assertTrue(definitions.stream().anyMatch(value -> !value.orders().isEmpty()));
        assertTrue(definitions.stream().anyMatch(value -> !value.hirelings().isEmpty()));
        assertTrue(definitions.stream().anyMatch(value -> !value.prerequisites().isEmpty()));
        assertTrue(definitions.stream().allMatch(value -> !value.entries().isEmpty()));
        assertEquals(6, definitions.stream().filter(value -> value.level() == null).count());
        assertEquals(6, definitions.stream().filter(value -> value.facilityType().equals("basic")).count());
        assertEquals(15, definitions.stream().filter(value -> Boolean.TRUE.equals(value.hasFluffImages())).count());
        assertEquals(1, definitions.stream().filter(value -> Boolean.TRUE.equals(value.hasFluff())).count());
        List<EntryDte> allEntries = definitions.stream().flatMap(value -> value.entries().stream()).toList();
        assertTrue(allEntries.stream().anyMatch(TextEntryDte.class::isInstance));
        assertTrue(allEntries.stream().anyMatch(NamedEntryDte.class::isInstance));
        assertTrue(allEntries.stream().anyMatch(TableEntryDte.class::isInstance));
    }

    @Test
    void strictUnknownFieldsAndInvalidFocusShapesAreRejected() throws Exception {
        assertThrows(Exception.class, () -> objectMapper.readerFor(FacilityFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"facility\":[{\"name\":\"Bad\",\"source\":\"XDMG\",\"unknown\":true}]}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(FacilityFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"facility\":[{\"name\":\"Bad\",\"source\":\"XDMG\","
                        + "\"prerequisite\":[{\"spellcastingFocus\":7}]}]}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(FacilityFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"facility\":[{\"name\":\"Bad\",\"source\":\"XDMG\","
                        + "\"prerequisite\":[{\"unknown\":true}]}]}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(FacilityFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"facility\":[{\"name\":\"Bad\",\"source\":\"XDMG\",\"entries\":["
                        + "{\"type\":\"entries\",\"unknown\":true}]}]}"));
        FacilityFileDte falseFocus = objectMapper.readerFor(FacilityFileDte.class)
                .readValue("{\"facility\":[{\"name\":\"Test\",\"source\":\"XDMG\","
                        + "\"prerequisite\":[{\"spellcastingFocus\":false}]}]}");
        var mappedFocus = new com.thedndpub.data.dto.bastion.FacilityMapper().map(falseFocus.facility().get(0))
                .prerequisites().get(0).spellcastingFocus();
        assertEquals(Boolean.FALSE, ((FacilitySpellcastingFocusRequirement) mappedFocus).required());
    }

    @Test
    void publicProjectionOmitsSourceEntriesMarkupAndOtherSummaryText() throws Exception {
        FacilityDetailDto detail = service.byId("facility:XDMG:smithy");
        String json = objectMapper.writeValueAsString(detail);
        assertTrue(json.contains("\"facilityType\":\"special\""));
        assertTrue(json.contains("\"orders\""));
        assertFalse(objectMapper.readTree(json).has("entries"));
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("Entry summary"));
        assertFalse(json.contains("hasFluff"));
        assertNotNull(detail.source());
        String polymorphicJson = objectMapper.writeValueAsString(service.byId("facility:XDMG:arcane-study"));
        assertTrue(polymorphicJson.contains("\"kind\":\"options\""));
        assertTrue(polymorphicJson.contains("\"types\":[\"arcane\",\"tool\"]"));
        String descriptivePrerequisiteJson = objectMapper.writeValueAsString(
                service.byId("facility:EFA:kundarak-vault"));
        assertFalse(descriptivePrerequisiteJson.contains("House Kundarak Renown 15+"));
    }

    @Test
    void filtersAndNameLookupAreDeterministicAndSourceAware() {
        assertEquals(70, service.find(null, null, null).size());
        assertEquals(35, service.find("XDMG", null, null).size());
        assertEquals(6, service.find(null, "basic", null).size());
        assertEquals(21, service.find(null, null, 5).size());
        assertEquals(service.find(null, null, null), service.find(null, null, null));
        assertEquals(1, service.byName("Smithy", "XDMG").size());
        assertThrows(RuntimeException.class, () -> service.byId("facility:missing:missing"));
        assertThrows(RuntimeException.class, () -> service.byName("Smithy", "AU"));
    }

    @Test
    void nameLookupRetainsMultipleSourceSpecificDefinitions() {
        var source = new FacilityFileDte(List.of(
                new FacilityDte("Example", "XDMG", 1, "basic", null,
                        null, List.of("Room"), null, null, List.of(), null, null),
                new FacilityDte("Example", "AU", 2, "special", null,
                        null, List.of("Room"), null, null, List.of(), null, null)));
        var syntheticCatalog = new BastionFacilityCatalog(source,
                new FacilityMapper(), sourceRegistry);
        assertEquals(2, syntheticCatalog.findByName("Example").size());
        assertEquals("facility:AU:example", syntheticCatalog.findByName("Example").get(0).id());
        assertEquals("facility:XDMG:example", syntheticCatalog.findByName("Example").get(1).id());
    }

    @Test
    void sourceRegistryRejectsUnknownPrimaryAndQualifiedMarkupSources() {
        FacilityDte unknownPrimary = new FacilityDte("Example", "ZZZZ", null, "basic", null,
                null, List.of("Room"), null, null, List.of(), null, null);
        assertThrows(IllegalStateException.class, () -> new BastionFacilityCatalog(
                new FacilityFileDte(List.of(unknownPrimary)), new FacilityMapper(), sourceRegistry));

        FacilityDte unknownMarkup = new FacilityDte("Example", "XDMG", null, "basic", null,
                null, List.of("Room"), null, null, List.of(new TextEntryDte("{@book Example|ZZZZ}")), null, null);
        assertThrows(IllegalStateException.class, () -> new BastionFacilityCatalog(
                new FacilityFileDte(List.of(unknownMarkup)), new FacilityMapper(), sourceRegistry));
    }

    @Test
    void routesExposeOnlyCatalogLookupsAndFilters() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new BastionFacilitiesController(service)).build();
        mvc.perform(get("/api/bastion-facilities").param("source", "XDMG").param("type", "basic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].facilityType").value("basic"));
        mvc.perform(get("/api/bastion-facilities/facility:XDMG:smithy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Smithy"));
        mvc.perform(get("/api/bastion-facilities/by-name/Smithy").param("source", "XDMG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("facility:XDMG:smithy"));
    }

    @Test
    void resolverAddsOnlySupportedFacilityMarkupAndLeavesPriorOutcomesUnchanged() {
        var results = resolutionCoordinator.snapshot().results();
        var facilityReferences = results.stream()
                .filter(result -> result.reference().owner().catalog().equals("facility"))
                .toList();
        var existingResults = results.stream()
                .filter(result -> !result.reference().owner().catalog().equals("facility"))
                .filter(result -> !result.reference().owner().catalog().equals("encounter"))
                .filter(result -> !result.reference().owner().catalog().equals("reward"))
                .filter(result -> !result.reference().owner().catalog().equals("trap"))
                .filter(result -> !result.reference().owner().catalog().equals("hazard"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle-upgrade"))
                .filter(result -> !result.reference().owner().catalog().equals("variant-rule"))
                .filter(result -> !result.reference().owner().catalog().equals("deity"))
                .filter(result -> !result.reference().owner().catalog().equals("treasure-table"))
                .toList();
        assertEquals(110, facilityReferences.size());
        assertEquals(89, facilityReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(0, facilityReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(6, facilityReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(15, facilityReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.DEFERRED).count());
        assertFalse(facilityReferences.isEmpty());
        assertEquals(3_954, existingResults.size());
        assertEquals(1_704, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(1_178, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(30, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1_042, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.DEFERRED).count());
        assertTrue(facilityReferences.stream()
                .filter(result -> result.reference().parsedSource() == null)
                .allMatch(result -> result.resolvedTargetId() == null));
        assertTrue(facilityReferences.stream().allMatch(result -> catalog.findById(
                result.reference().owner().stableId()) != null));
        assertEquals(1_704, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(1_178, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(30, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1_042, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.DEFERRED).count());
        assertThrows(UnsupportedOperationException.class, () -> catalog.definitions().clear());
    }

    @Test
    void facilityResourceLoadsFromPackagedJarClasspath() throws Exception {
        Path jarPath = temporaryDirectory.resolve("bastion-facility-resource.jar");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath));
                InputStream input = new ClassPathResource("data/bastions.json").getInputStream()) {
            jar.putNextEntry(new JarEntry("data/bastions.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }
        try (URLClassLoader loader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null);
                InputStream input = new ClassPathResource("data/bastions.json", loader).getInputStream()) {
            FacilityFileDte packaged = objectMapper.readerFor(FacilityFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
            assertEquals(70, packaged.facility().size());
        }
    }
}
