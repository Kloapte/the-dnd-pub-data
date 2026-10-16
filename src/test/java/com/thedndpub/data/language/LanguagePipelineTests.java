package com.thedndpub.data.language;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.thedndpub.data.dto.language.LanguageDto;
import com.thedndpub.data.dto.language.LanguageMapper;
import com.thedndpub.data.dto.language.LanguageImportRecord;
import com.thedndpub.data.dto.language.LanguageSummaryDto;
import com.thedndpub.data.dte.language.LanguageDte;
import com.thedndpub.data.dte.language.LanguagesFileDte;
import com.thedndpub.data.services.LanguageCatalog;
import com.thedndpub.data.services.LanguagesService;
import com.thedndpub.data.controllers.LanguageController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.util.List;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LanguagePipelineTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LanguageCatalog catalog;

    @Autowired
    private LanguagesService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(new LanguageController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
    }

    @Test
    void deserializesCompletePackagedCorpusAndMapsEveryLanguage() throws Exception {
        try (InputStream input = new ClassPathResource("data/languages.json").getInputStream()) {
            LanguagesFileDte source = objectMapper.readerFor(LanguagesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
            assertEquals(201, source.language().size());
            assertEquals(6, source.languageScript().size());
            assertEquals(source.language().size(), catalog.size());
            assertEquals(source.language().size(), catalog.sourceRecordCount());
            assertEquals(source.languageScript().size(), catalog.scriptRecordCount());
            assertEquals(201, source.language().stream()
                    .map(language -> language.name() + "|" + language.source())
                    .distinct()
                    .count());
        }
    }

    @Test
    void languageResourceCanBeReadFromJarClasspath() throws Exception {
        Path jarPath = Files.createTempFile("language-resource", ".jar");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/languages.json"));
            jar.write("{\"language\":[],\"languageScript\":[]}".getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            Resource resource = new ClassPathResource("data/languages.json", classLoader);
            try (InputStream input = resource.getInputStream()) {
                LanguagesFileDte packaged = objectMapper.readValue(input, LanguagesFileDte.class);
                assertTrue(packaged.language().isEmpty());
                assertTrue(packaged.languageScript().isEmpty());
            }
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
            assertEquals(1, resolver.getResources("classpath*:data/languages.json").length);
        }
    }

    @Test
    void strictSourceReaderRejectsUnmappedFields() {
        String json = "{\"language\":[],\"languageScript\":[],\"futureField\":true}";

        assertThrows(
                JsonProcessingException.class,
                () -> objectMapper.readerFor(LanguagesFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(json));
    }

    @Test
    void idsAreStableAndIncludeSource() throws Exception {
        LanguageDte phb = language("Abyssal", "PHB");
        LanguageDte xphb = language("Abyssal", "XPHB");
        LanguageMapper mapper = new LanguageMapper();

        assertEquals(mapper.map(phb).id(), mapper.map(phb).id());
        assertNotEquals(mapper.map(phb).id(), mapper.map(xphb).id());
        assertEquals("language:phb:abyssal", mapper.map(phb).id().value());
    }

    @Test
    void catalogRejectsDuplicateIdentitiesAndIds() throws Exception {
        LanguageDte abyssal = language("Abyssal", "PHB");
        LanguagesFileDte duplicateCorpus = new LanguagesFileDte(List.of(abyssal, abyssal), List.of());

        assertThrows(
                IllegalStateException.class,
                () -> new LanguageCatalog(duplicateCorpus, new LanguageMapper()));
    }

    @Test
    void mapsAllSourceFieldsToDefinitionOrInternalProvenance() throws Exception {
        LanguageDte source = language("Alzhedo", "SCAG");
        LanguageImportRecord mapped = new LanguageMapper().mapRecord(source);

        assertEquals(source.name(), mapped.definition().name());
        assertEquals(source.script(), mapped.definition().script());
        assertEquals(source.entries().size(), mapped.provenance().entries().size());
        assertEquals(source.typicalSpeakers(), mapped.provenance().typicalSpeakers());
        assertEquals(source.page(), mapped.provenance().page());
        assertEquals(source.hasFluffImages(), mapped.provenance().hasFluffImages());
        assertEquals(
                source.additionalSources() == null ? List.of() : source.additionalSources().stream()
                        .map(reference -> reference.source() + ":" + reference.page()).toList(),
                mapped.provenance().additionalSources().stream()
                        .map(reference -> reference.identity().code() + ":" + reference.page()).toList());
        assertEquals(
                source.otherSources() == null ? List.of() : source.otherSources().stream()
                        .map(reference -> reference.source() + ":" + reference.page()).toList(),
                mapped.provenance().otherSources().stream()
                        .map(reference -> reference.identity().code() + ":" + reference.page()).toList());
        assertFalse(objectMapper.writeValueAsString(LanguageDto.from(mapped.definition())).contains("entries"));
    }

    @Test
    void serviceSupportsSourceAndNameLookups() {
        List<LanguageSummaryDto> abyssal = service.findLanguages("abyssal", null);
        assertEquals(3, abyssal.size());
        assertEquals("Abyssal", service.findByNameAndSource("abyssal", "phb").name());
        assertEquals("PHB", service.findByNameAndSource("Abyssal", "phb").source());
    }

    @Test
    void publicJsonOmitsEmptyPropertiesAndInternalSourceMetadata() throws Exception {
        LanguageDto dto = new LanguageDto(
                catalog.findByNameAndSource("Abyssal", "XPHB").id(),
                "Abyssal",
                "XPHB",
                "rare",
                null,
                List.of());
        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"id\""));
        assertFalse(json.contains("script"));
        assertFalse(json.contains("dialects"));
        assertFalse(json.contains("fonts"));
        assertFalse(json.contains("origin"));
    }

    @Test
    void restEndpointsFilterAndResolveDefinitions() throws Exception {
        mockMvc.perform(get("/api/languages").param("name", "Abyssal").param("source", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Abyssal"))
                .andExpect(jsonPath("$[0].source").value("PHB"));

        mockMvc.perform(get("/api/languages/by-name/Abyssal").param("source", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.value").value("language:phb:abyssal"));

        mockMvc.perform(get("/api/languages/language:phb:abyssal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Abyssal"));

        mockMvc.perform(get("/api/languages/by-name/Abyssal").param("source", "unknown"))
                .andExpect(status().isNotFound());
    }

    private LanguageDte language(String name, String source) throws Exception {
        try (InputStream input = new ClassPathResource("data/languages.json").getInputStream()) {
            LanguagesFileDte file = objectMapper.readValue(input, LanguagesFileDte.class);
            return file.language().stream()
                    .filter(language -> language.name().equals(name) && language.source().equals(source))
                    .findFirst()
                    .orElseThrow();
        }
    }
}
