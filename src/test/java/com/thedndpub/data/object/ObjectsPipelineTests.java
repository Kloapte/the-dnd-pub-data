package com.thedndpub.data.object;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.object.ObjectDetailDto;
import com.thedndpub.data.dto.object.ObjectStatisticDto;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.controllers.ObjectsController;
import com.thedndpub.data.dte.object.ObjectFileDte;
import com.thedndpub.data.services.ObjectCatalog;
import com.thedndpub.data.services.ObjectsService;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.dte.object.ObjectNumericSpeedDte;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ObjectsPipelineTests {
    @TempDir
    private Path temporaryDirectory;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ObjectCatalog catalog;
    @Autowired private ObjectsService service;
    @Autowired private SourceRegistry sourceRegistry;
    @Autowired private CrossCatalogResolutionCoordinator resolutionCoordinator;

    @Test
    void fullCorpusLoadsStrictlyWithUniqueSourceAwareIdentities() throws Exception {
        ObjectFileDte source;
        try (InputStream input = new ClassPathResource("data/objects.json").getInputStream()) {
            source = objectMapper.readerFor(ObjectFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        }
        assertEquals(37, source.object().size());
        assertEquals(37, catalog.size());
        assertEquals(37, new HashSet<>(catalog.definitions().stream().map(value -> value.id()).toList()).size());
        assertTrue(catalog.definitions().stream()
                .allMatch(value -> sourceRegistry.find(value.source().identity().code()) != null));
        assertEquals("object:DMG:ballista", catalog.findByName("Ballista").get(0).id());
    }

    @Test
    void corpusPreservesOptionalAndPolymorphicMechanicalValues() {
        var cannon = catalog.definitions().stream().filter(value -> value.name().equals("Cannon")
                && value.source().identity().code().equals("DMG")).findFirst().orElseThrow();
        var generic = catalog.definitions().stream().filter(value -> value.name().equals("Generic Object"))
                .findFirst().orElseThrow();
        var movement = catalog.definitions().stream().filter(value -> value.name().equals("Unborn Palanquin"))
                .findFirst().orElseThrow();

        assertNotNull(cannon.actions());
        assertNotNull(generic.armorClass());
        assertNotNull(generic.hitPoints());
        assertNotNull(movement.speed());
        assertEquals(37, catalog.summaries().size());
        assertTrue(catalog.summaries().stream().allMatch(value -> value.name() != null));
    }

    @Test
    void publicProjectionOmitsAuthoredProseAndSourceMediaDetails() throws Exception {
        ObjectDetailDto detail = service.byId("object:DMG:cannon");
        var json = objectMapper.valueToTree(detail);
        assertFalse(json.has("actions"));
        assertFalse(json.has("entries"));
        assertFalse(json.has("token"));
        assertFalse(json.has("alternateArt"));
        assertTrue(json.has("armorClass"));
        ObjectDetailDto cannonSpecial = service.byId("object:EFA:eldritch-cannon");
        assertTrue(objectMapper.writeValueAsString(cannonSpecial).contains("specialValuePresent"));
        String specialJson = objectMapper.writeValueAsString(cannonSpecial);
        assertFalse(specialJson.contains("{@spell"));
        assertFalse(specialJson.contains("restores"));

        ObjectDetailDto explicitDefaults = new ObjectDetailDto(
                "object:test:defaults", "Test Object", new SourceReference("DMG", null), "U", false,
                java.util.List.of(), new ObjectStatisticDto(0, null), null, null,
                null, null, null, null, null, null,
                java.util.List.of(), java.util.List.of(), java.util.List.of(), java.util.List.of(),
                false, java.util.List.of());
        var defaultsJson = objectMapper.valueToTree(explicitDefaults);
        assertTrue(defaultsJson.has("npc"));
        assertFalse(defaultsJson.get("npc").asBoolean());
        assertEquals(0, defaultsJson.get("armorClass").get("value").asInt());
        assertTrue(defaultsJson.has("srd"));
        assertFalse(defaultsJson.get("srd").asBoolean());
    }

    @Test
    void lookupFiltersAreDeterministicAndSourceAware() {
        assertEquals(2, service.byName("Ballista", null).size());
        assertEquals(1, service.byName("Ballista", "DMG").size());
        assertEquals(6, service.find(null, "HBTD", "SW").size());
        assertThrows(RuntimeException.class, () -> service.byId("object:missing:missing"));
    }

    @Test
    void rejectsUnknownSourceFieldsAndServesReadOnlyRoutes() throws Exception {
        assertThrows(Exception.class, () -> objectMapper.readerFor(ObjectFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"object\":[{\"name\":\"Bad\",\"source\":\"DMG\",\"unknown\":true}]}"));

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ObjectsController(service)).build();
        mvc.perform(get("/api/objects").param("source", "DMG").param("type", "SW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("DMG"));
        mvc.perform(get("/api/objects/object:DMG:cannon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cannon"));
    }

    @Test
    void supportedMarkupReferencesAreAddedWithoutInventingTargets() {
        var references = resolutionCoordinator.snapshot().results().stream()
                .filter(result -> result.reference().owner().catalog().equals("object"))
                .toList();
        assertEquals(4, references.size());
        assertEquals(2, references.stream().filter(result -> result.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(1, references.stream().filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(0, references.stream().filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1, references.stream().filter(result -> result.outcome() == ResolutionOutcome.DEFERRED).count());
        assertTrue(references.stream().filter(result -> result.reference().parsedSource() == null)
                .allMatch(result -> result.resolvedTargetId() == null));
        assertTrue(references.stream().allMatch(result -> catalog.findById(
                result.reference().owner().stableId()) != null));
        var existingResults = resolutionCoordinator.snapshot().results().stream()
                .filter(result -> !result.reference().owner().catalog().equals("object"))
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
        assertEquals(3_950, existingResults.size());
        assertEquals(1_702, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(1_177, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(30, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1_041, existingResults.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.DEFERRED).count());
    }

    @Test
    void immutableIndexesAndMeaningfulZeroValuesArePreserved() {
        assertThrows(UnsupportedOperationException.class, () -> catalog.definitions().clear());
        var gh = service.byId("object:RMBRE:captain-a-hole");
        assertEquals(new ObjectNumericSpeedDte(0), gh.speed());
    }

    @Test
    void objectResourceLoadsFromPackagedJarClasspath() throws Exception {
        Path jarPath = temporaryDirectory.resolve("object-resource.jar");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath));
                InputStream input = new ClassPathResource("data/objects.json").getInputStream()) {
            jar.putNextEntry(new JarEntry("data/objects.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }
        try (URLClassLoader loader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null);
                InputStream input = new ClassPathResource("data/objects.json", loader).getInputStream()) {
            ObjectFileDte packaged = objectMapper.readerFor(ObjectFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
            assertEquals(37, packaged.object().size());
        }
    }
}
