package com.thedndpub.data.skill;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.controllers.SkillsController;
import com.thedndpub.data.dto.skill.SkillAbility;
import com.thedndpub.data.dto.skill.SkillDefinition;
import com.thedndpub.data.dto.skill.SkillDto;
import com.thedndpub.data.dto.skill.SkillMapper;
import com.thedndpub.data.dto.skill.SkillSummaryDto;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.skill.SkillDte;
import com.thedndpub.data.dte.skill.SkillsFileDte;
import com.thedndpub.data.services.SkillCatalog;
import com.thedndpub.data.services.SkillsService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SkillPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkillCatalog catalog;

    @Autowired
    private SkillsService service;

    @Autowired
    private SkillMapper mapper;

    @Autowired
    private SourceRegistry sourceRegistry;

    private MockMvc mockMvc;

    @BeforeEach
    void configureController() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SkillsController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
    }

    @Test
    void deserializesAndNormalizesTheCompleteSkillCorpus() throws Exception {
        try (InputStream input = new ClassPathResource("data/skills.json").getInputStream()) {
            SkillsFileDte source = strictReader().readValue(input);
            assertEquals(36, source.skill().size());
            assertEquals(36, catalog.size());
            assertEquals(36, source.skill().stream()
                    .map(skill -> skill.name() + "|" + skill.source())
                    .distinct()
                    .count());
            assertEquals(List.of("PHB", "XPHB"), catalog.sourceCodes().stream().sorted().toList());
            assertNotNull(sourceRegistry.find("PHB"));
            assertNotNull(sourceRegistry.find("XPHB"));
        }
    }

    @Test
    void strictReaderRejectsUnexpectedFields() {
        String unexpectedField = """
                {"skill":[{"name":"Test","source":"PHB","page":1,"ability":"str",
                  "entries":["text"],"unexpected":true}]}
                """;

        assertThrows(Exception.class, () -> strictReader().readValue(unexpectedField));
    }

    @Test
    void mapsEveryFieldAndTheStructuredAthleticsEntry() {
        SkillDefinition athletics = catalog.findByNameAndSource("athletics", "phb");
        assertNotNull(athletics);
        assertEquals("skill:phb:athletics", athletics.id().value());
        assertEquals("PHB", athletics.source().identity().code());
        assertEquals(175, athletics.source().page());
        assertEquals(SkillAbility.STRENGTH, athletics.ability());

        var provenance = catalog.provenanceById(athletics.id().value());
        assertEquals("data/skills.json", provenance.sourceFile());
        assertEquals(2, provenance.entries().size());
        assertTrue(provenance.entries().get(1) instanceof ListEntryDte);
        assertEquals(List.of("Athletics|XPHB"), provenance.reprintedAs());
        assertEquals(Boolean.TRUE, provenance.srd());
        assertEquals(Boolean.TRUE, provenance.basicRules());
        assertNull(provenance.srd52());

        var xphbAthletics = catalog.findByNameAndSource("Athletics", "XPHB");
        assertEquals(14, xphbAthletics.source().page());
        assertEquals(Boolean.TRUE, catalog.provenanceById(xphbAthletics.id().value()).srd52());
    }

    @Test
    void mapsExplicitAbilityValuesWithoutInferringFromSkillNames() {
        assertEquals(6, catalog.summaries().stream()
                .filter(skill -> skill.ability() == SkillAbility.DEXTERITY)
                .count());
        assertEquals(10, catalog.summaries().stream()
                .filter(skill -> skill.ability() == SkillAbility.WISDOM)
                .count());
        assertEquals(10, catalog.summaries().stream()
                .filter(skill -> skill.ability() == SkillAbility.INTELLIGENCE)
                .count());
        assertEquals(2, catalog.summaries().stream()
                .filter(skill -> skill.ability() == SkillAbility.STRENGTH)
                .count());
        assertEquals(8, catalog.summaries().stream()
                .filter(skill -> skill.ability() == SkillAbility.CHARISMA)
                .count());
        assertThrows(IllegalArgumentException.class, () -> SkillAbility.fromCode("con"));
    }

    @Test
    void createsStableSourceAwareIdsAndKeepsSameNameDefinitionsDistinct() {
        SkillDefinition phb = catalog.findByNameAndSource("Acrobatics", "PHB");
        SkillDefinition xphb = catalog.findByNameAndSource("Acrobatics", "XPHB");

        assertEquals("skill:phb:acrobatics", phb.id().value());
        assertEquals("skill:xphb:acrobatics", xphb.id().value());
        assertFalse(phb.id().equals(xphb.id()));
        assertEquals(2, catalog.findByName("Acrobatics").size());
    }

    @Test
    void rejectsDuplicateNameSourceDefinitionsAndUnregisteredSources() throws Exception {
        SkillsFileDte source = loadSkills();
        SkillDte skill = source.skill().getFirst();
        assertThrows(IllegalStateException.class,
                () -> new SkillCatalog(new SkillsFileDte(List.of(skill, skill)), mapper, sourceRegistry));

        SkillDte unknownSource = new SkillDte(
                skill.name(), "UNKNOWN-SOURCE", skill.page(), skill.srd(), skill.basicRules(), skill.srd52(),
                skill.reprintedAs(), skill.ability(), skill.entries());
        assertThrows(IllegalStateException.class,
                () -> new SkillCatalog(new SkillsFileDte(List.of(unknownSource)), mapper, sourceRegistry));
    }

    @Test
    void sortsAndFiltersByNameAndSourceDeterministically() {
        List<SkillSummaryDto> summaries = catalog.summaries();
        List<SkillSummaryDto> sorted = summaries.stream()
                .sorted(java.util.Comparator
                        .comparing(SkillSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(summary -> summary.source().identity().code(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(summary -> summary.id().value()))
                .toList();
        assertEquals(sorted, summaries);

        assertEquals(2, service.findSkills("acrobatics", null).size());
        assertEquals(1, service.findSkills("acrobatics", "phb").size());
        assertEquals("PHB", service.findByName("Acrobatics", "phb").getFirst().source().identity().code());
        assertEquals(2, service.findByName("Acrobatics", null).size());
    }

    @Test
    void publicJsonOmitsDescriptionsAndImportMetadata() throws Exception {
        SkillDto detail = service.findById("skill:phb:acrobatics");
        JsonNode json = objectMapper.valueToTree(detail);

        assertEquals("Acrobatics", json.path("name").asText());
        assertEquals("PHB", json.path("source").path("source").asText());
        assertEquals(176, json.path("source").path("page").asInt());
        assertEquals("dex", json.path("ability").asText());
        assertFalse(json.has("entries"));
        assertFalse(json.has("reprintedAs"));
        assertFalse(json.has("srd"));
        assertFalse(json.has("basicRules"));
        assertFalse(json.has("srd52"));
        assertTrue(json.has("id"));
    }

    @Test
    void skillResourceLoadsFromJarClasspath() throws Exception {
        Path jarPath = temporaryDirectory.resolve("skills-resource.jar");
        try (InputStream input = new ClassPathResource("data/skills.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/skills.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            Resource resource = new ClassPathResource("data/skills.json", classLoader);
            try (InputStream input = resource.getInputStream()) {
                assertEquals(36, objectMapper.readValue(input, SkillsFileDte.class).skill().size());
            }
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
            assertEquals(1, resolver.getResources("classpath*:data/skills.json").length);
        }
    }

    @Test
    void restEndpointsSupportFilteringAmbiguityAndMissingSkills() throws Exception {
        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(36)));
        mockMvc.perform(get("/api/skills").param("name", "acrobatics").param("source", "phb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].source.source").value("PHB"));
        mockMvc.perform(get("/api/skills/skill:phb:acrobatics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acrobatics"));
        mockMvc.perform(get("/api/skills/by-name/Acrobatics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
        mockMvc.perform(get("/api/skills/by-name/Acrobatics").param("source", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));
        mockMvc.perform(get("/api/skills/unknown"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/skills/by-name/unknown"))
                .andExpect(status().isNotFound());
    }

    private SkillsFileDte loadSkills() throws Exception {
        try (InputStream input = new ClassPathResource("data/skills.json").getInputStream()) {
            return strictReader().readValue(input);
        }
    }

    private ObjectMapper readerMapper() {
        return objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    private com.fasterxml.jackson.databind.ObjectReader strictReader() {
        return readerMapper().readerFor(SkillsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
