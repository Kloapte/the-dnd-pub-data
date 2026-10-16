package com.thedndpub.data.condition;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.ConditionsController;
import com.thedndpub.data.controllers.DiseasesController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.dte.condition.ConditionsDiseasesFileDte;
import com.thedndpub.data.dte.condition.ConditionDte;
import com.thedndpub.data.dte.condition.StatusDte;
import com.thedndpub.data.dto.condition.ConditionMapper;
import com.thedndpub.data.services.ConditionCatalog;
import com.thedndpub.data.services.ConditionNotFoundException;
import com.thedndpub.data.services.ConditionsService;
import com.thedndpub.data.services.DiseaseCatalog;
import com.thedndpub.data.services.DiseaseNotFoundException;
import com.thedndpub.data.services.DiseasesService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
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
class ConditionDiseasePipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConditionCatalog conditionCatalog;

    @Autowired
    private DiseaseCatalog diseaseCatalog;

    @Autowired
    private ConditionsService conditionsService;

    @Autowired
    private DiseasesService diseasesService;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private ConditionMapper conditionMapper;

    @Test
    void strictDeserializationAndCatalogsAccountForEveryRootCollection() throws Exception {
        ConditionsDiseasesFileDte source;
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("data/conditionsdiseases.json")) {
            assertNotNull(input);
            source = objectMapper.readerFor(ConditionsDiseasesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }

        assertEquals(30, source.condition().size());
        assertEquals(29, source.disease().size());
        assertEquals(5, source.status().size());
        assertEquals(30, conditionCatalog.size());
        assertEquals(29, diseaseCatalog.size());
        assertEquals(5, conditionCatalog.statusRecordCount());
        assertTrue(sourceRegistry.find("RMR") != null);
        assertTrue(sourceRegistry.find("HftT") != null);
        assertEquals("condition:phb:blinded", conditionCatalog.findById("condition:phb:blinded").id().value());
        assertEquals("disease:phb:slimy-doom", diseaseCatalog.findById("disease:phb:slimy-doom").id().value());
    }

    @Test
    void sourceSpecificDefinitionsReferencesAndImportOnlyEntriesArePreserved() throws Exception {
        var phbBlinded = conditionCatalog.findById("condition:phb:blinded");
        var xphbBlinded = conditionCatalog.findById("condition:xphb:blinded");
        assertEquals("PHB", phbBlinded.source().identity().code());
        assertEquals(290, phbBlinded.source().page());
        assertEquals("XPHB", phbBlinded.reprintedAs().getFirst().source().code());
        assertEquals("XPHB", xphbBlinded.source().identity().code());
        assertEquals(2, phbBlinded.otherSources().size());
        assertEquals("RMR", phbBlinded.otherSources().getFirst().identity().code());
        assertEquals(1, conditionCatalog.importById(phbBlinded.id().value()).provenance().entries().size());
        assertTrue(conditionCatalog.importById("condition:phb:exhaustion")
                .provenance().entries().stream().anyMatch(entry -> entry.getClass().getSimpleName().contains("Table")));

        var rusting = diseaseCatalog.findById("disease:fraif:the-rusting");
        assertEquals("Magical Contagion", rusting.classification());
        assertTrue(diseaseCatalog.importById(rusting.id().value()).provenance().entries().size() > 1);
        var cackleFever = diseaseCatalog.findById("disease:dmg:cackle-fever");
        assertEquals("XDMG", cackleFever.reprintedAs().getFirst().source().code());

        String conditionJson = objectMapper.writeValueAsString(conditionsService.findById(phbBlinded.id().value()));
        String diseaseJson = objectMapper.writeValueAsString(diseasesService.findById(rusting.id().value()));
        assertFalse(conditionJson.contains("entries"));
        assertFalse(conditionJson.contains("srd"));
        assertFalse(diseaseJson.contains("{@"));
        assertTrue(diseaseJson.contains("Magical Contagion"));
        assertFalse(objectMapper.writeValueAsString(diseasesService.findById("disease:phb:slimy-doom"))
                .contains("classification"));
    }

    @Test
    void nameSourceFiltersAndAmbiguousNameLookupsAreDeterministic() {
        assertEquals(2, conditionsService.findByName("bLiNdEd", null).size());
        assertEquals(1, conditionsService.findByName("Blinded", "xphb").size());
        assertEquals(1, conditionsService.findConditions("BLIND", "PHB").size());
        assertEquals(1, diseasesService.findDiseases("rusting", "fraif").size());
        assertEquals(1, diseasesService.findByName("The Rusting", "FRAiF").size());
        assertThrows(ConditionNotFoundException.class, () -> conditionsService.findById("missing"));
        assertThrows(DiseaseNotFoundException.class, () -> diseasesService.findByName("missing", null));
        assertThrows(UnsupportedOperationException.class, () -> conditionCatalog.summaries().clear());
    }

    @Test
    void catalogRejectsUnknownSourcesAndDuplicateSourceIdentities() {
        ConditionDte unknownSource = new ConditionDte(
                "Test Condition", "NOT_REGISTERED", 1, null, java.util.List.of(),
                null, null, null, null, null, null);
        assertThrows(IllegalStateException.class,
                () -> new ConditionCatalog(
                        new ConditionsDiseasesFileDte(
                                java.util.List.of(unknownSource),
                                java.util.List.of(),
                                java.util.List.of(new StatusDte(
                                        "Surprised", "PHB", 1, null, java.util.List.of(),
                                        null, null, null, null))),
                        conditionMapper,
                        sourceRegistry));

        ConditionDte first = new ConditionDte(
                "Duplicate", "PHB", 1, null, java.util.List.of(), null, null, null, null, null, null);
        ConditionDte duplicate = new ConditionDte(
                "duplicate", "phb", 2, null, java.util.List.of(), null, null, null, null, null, null);
        assertThrows(IllegalStateException.class,
                () -> new ConditionCatalog(
                        new ConditionsDiseasesFileDte(
                                java.util.List.of(first, duplicate),
                                java.util.List.of(),
                                java.util.List.of(new StatusDte(
                                        "Surprised", "PHB", 1, null, java.util.List.of(),
                                        null, null, null, null))),
                        conditionMapper,
                        sourceRegistry));
    }

    @Test
    void endpointsReturnSummariesDetailsAmbiguousMatchesAndExistingErrors() throws Exception {
        MockMvc conditions = MockMvcBuilders.standaloneSetup(new ConditionsController(conditionsService))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
        conditions.perform(get("/api/conditions").param("name", "blind").param("source", "phb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("condition:phb:blinded"));
        conditions.perform(get("/api/conditions/condition:phb:blinded"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source.page").value(290));
        conditions.perform(get("/api/conditions/by-name/Blinded"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
        conditions.perform(get("/api/conditions/nope"))
                .andExpect(status().isNotFound());

        MockMvc diseases = MockMvcBuilders.standaloneSetup(new DiseasesController(diseasesService))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
        diseases.perform(get("/api/diseases/by-name/The Rusting").param("source", "FRAiF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].classification").value("Magical Contagion"));
    }

    @Test
    void completeResourceLoadsFromPackagedClasspathJar() throws Exception {
        Path jarPath = temporaryDirectory.resolve("conditions-diseases-resource.jar");
        try (InputStream input = new ClassPathResource("data/conditionsdiseases.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/conditionsdiseases.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            Resource resource = new ClassPathResource("data/conditionsdiseases.json", classLoader);
            try (InputStream input = resource.getInputStream()) {
                ConditionsDiseasesFileDte file = objectMapper.readerFor(ConditionsDiseasesFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(input);
                assertEquals(30, file.condition().size());
                assertEquals(29, file.disease().size());
            }
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
            assertEquals(1, resolver.getResources("classpath*:data/conditionsdiseases.json").length);
        }
    }
}
