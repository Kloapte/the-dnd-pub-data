package com.thedndpub.data.sense;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.controllers.SensesController;
import com.thedndpub.data.dto.sense.SenseMapper;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.sense.SenseDte;
import com.thedndpub.data.dte.sense.SensesFileDte;
import com.thedndpub.data.services.SenseCatalog;
import com.thedndpub.data.services.SenseNotFoundException;
import com.thedndpub.data.services.SensesService;
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
import java.nio.file.Path;
import java.util.List;
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
class SensePipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SenseCatalog catalog;

    @Autowired
    private SenseMapper mapper;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private SensesService service;

    @Test
    void completeCorpusNormalizesAndUsesRegisteredPublications() throws Exception {
        SensesFileDte source;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/senses.json")) {
            assertNotNull(input);
            source = objectMapper.readerFor(SensesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }

        assertEquals(8, source.sense().size());
        assertEquals(8, catalog.size());
        assertEquals(List.of("MM", "PHB", "XPHB"), catalog.sourceCodes().stream().sorted().toList());
        assertEquals(3, catalog.sourceCodes().stream()
                .filter(code -> sourceRegistry.find(code).publication() != null).count());
        assertEquals("sense:phb:blindsight", mapper.mapRecord(source.sense().getFirst()).definition().id().value());
        assertEquals(1, catalog.provenanceById("sense:phb:blindsight").reprintedAs().size());
        assertEquals(2, catalog.provenanceById("sense:xphb:truesight").entries().size());
    }

    @Test
    void catalogValidatesUnknownSourcesAndDuplicateDefinitions() {
        SenseDte sourceRecord = new SenseDte(
                "New Sense", "NOT_REGISTERED", 1, null, null, null, null, List.of());
        assertThrows(IllegalArgumentException.class, () -> mapper.mapRecord(sourceRecord));

        SenseDte first = new SenseDte("Blindsight", "PHB", 183, null, null, null, null,
                List.of(new TextEntryDte("test")));
        SenseDte duplicate = new SenseDte("blindsight", "phb", 184, null, null, null, null,
                List.of(new TextEntryDte("test")));
        assertThrows(IllegalStateException.class,
                () -> new SenseCatalog(new SensesFileDte(List.of(first, duplicate)), mapper, sourceRegistry));
    }

    @Test
    void catalogRejectsUnknownSourceIdentity() {
        SenseDte unknownSource = new SenseDte("Unlisted", "NOPE", 1, null, null, null, null,
                List.of(new TextEntryDte("test")));
        assertThrows(IllegalStateException.class,
                () -> new SenseCatalog(new SensesFileDte(List.of(unknownSource)), mapper, sourceRegistry));
    }

    @Test
    void publicSerializationOmitsEntriesAndAvailabilityFlags() throws Exception {
        String json = objectMapper.writeValueAsString(service.findById("sense:xphb:truesight"));
        assertTrue(json.contains("\"page\":377"));
        assertFalse(json.contains("entries"));
        assertFalse(json.contains("srd52"));
    }

    @Test
    void packagedJarClasspathResourceLoadsCompleteCorpus() throws Exception {
        Path jarPath = temporaryDirectory.resolve("senses-resource.jar");
        try (InputStream input = new ClassPathResource("data/senses.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(java.nio.file.Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/senses.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            Resource resource = new ClassPathResource("data/senses.json", classLoader);
            try (InputStream input = resource.getInputStream()) {
                assertEquals(8, objectMapper.readValue(input, SensesFileDte.class).sense().size());
            }
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
            assertEquals(1, resolver.getResources("classpath*:data/senses.json").length);
        }
    }

    @Test
    void filtersAndEndpointsAreCaseInsensitiveAndNotFoundUsesExistingHandler() throws Exception {
        assertEquals(2, service.findByName("dArKvIsIoN", null).size());
        assertEquals(1, service.findByName("Darkvision", "xphb").size());
        assertEquals(1, service.findSenses("dark", "PHB").size());
        assertThrows(SenseNotFoundException.class, () -> service.findById("missing"));

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new SensesController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
        mvc.perform(get("/api/senses").param("source", "xphb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(4)));
        mvc.perform(get("/api/senses/by-name/Darkvision"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
        mvc.perform(get("/api/senses/sense:xphb:darkvision"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Darkvision"));
        mvc.perform(get("/api/senses/missing"))
                .andExpect(status().isNotFound());
    }
}
