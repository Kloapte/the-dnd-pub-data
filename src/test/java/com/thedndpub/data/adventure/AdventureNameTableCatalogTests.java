package com.thedndpub.data.adventure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.AdventuresController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.controllers.NameTablesController;
import com.thedndpub.data.dto.adventure.AdventureMapper;
import com.thedndpub.data.dto.nametable.NameTableMapper;
import com.thedndpub.data.dte.adventure.AdventureDte;
import com.thedndpub.data.dte.adventure.AdventureFileDte;
import com.thedndpub.data.dte.nametable.NameTableDte;
import com.thedndpub.data.dte.nametable.NameTableOptionDte;
import com.thedndpub.data.dte.nametable.NameTableRowDte;
import com.thedndpub.data.dte.nametable.NameTablesFileDte;
import com.thedndpub.data.services.AdventureCatalog;
import com.thedndpub.data.services.NameTableCatalog;
import com.thedndpub.data.services.AdventuresService;
import com.thedndpub.data.services.NameTableCatalog;
import com.thedndpub.data.services.NameTablesService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AdventureNameTableCatalogTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdventureCatalog adventures;

    @Autowired
    private NameTableCatalog nameTables;

    @Autowired
    private AdventureMapper adventureMapper;

    @Autowired
    private NameTableMapper nameTableMapper;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private AdventuresService adventuresService;

    @Autowired
    private NameTablesService nameTablesService;

    @Test
    void loadsAndNormalizesCompleteAdventureAndNameCorpora() throws Exception {
        AdventureFileDte adventureSource = read("data/adventures.json", AdventureFileDte.class);
        NameTablesFileDte nameSource = read("data/names.json", NameTablesFileDte.class);

        assertEquals(101, adventureSource.adventure().size());
        assertEquals(101, adventures.size());
        assertEquals(10, nameSource.name().size());
        assertEquals(10, nameTables.size());
        assertEquals(61, nameTables.definitions().stream().mapToInt(table -> table.tables().size()).sum());
        assertEquals(2822, nameTables.definitions().stream().mapToInt(table -> table.rowCount()).sum());
        assertEquals("adventure:pip", adventures.findBySource("PiP").id().value());
        assertEquals("MOT-NSS", adventures.findBySource("MOT").authoredId());
        assertEquals("FRAiF-TLLoL", adventures.findBySource("FRAiF").authoredId());
        assertTrue(adventures.definitions().stream().allMatch(definition ->
                sourceRegistry.find(definition.source().identity().code()) != null));
        assertTrue(nameTables.definitions().stream().allMatch(definition ->
                sourceRegistry.find(definition.source().identity().code()) != null));
    }

    @Test
    void sourceShapesDeserializeStrictlyAndPreserveTypedAdventureNavigation() throws Exception {
        AdventureFileDte source = read("data/adventures.json", AdventureFileDte.class);
        long textHeaders = adventures.definitions().stream()
                .flatMap(definition -> adventures.importById(definition.id().value()).provenance().contents().stream())
                .flatMap(content -> content.headers().stream())
                .filter(header -> header instanceof com.thedndpub.data.dte.book.TextBookHeaderDte)
                .count();
        long structuredHeaders = adventures.definitions().stream()
                .flatMap(definition -> adventures.importById(definition.id().value()).provenance().contents().stream())
                .flatMap(content -> content.headers().stream())
                .filter(header -> header instanceof com.thedndpub.data.dte.book.StructuredBookHeaderDte)
                .count();

        assertEquals(101, source.adventure().size());
        assertEquals(3244, textHeaders);
        assertEquals(7464, structuredHeaders);
        assertThrows(Exception.class, () -> objectMapper.readerFor(NameTablesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"name\":[],\"unexpected\":true}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(AdventureFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"adventure\":[{\"name\":\"x\",\"unknown\":1}]}"));
    }

    @Test
    void stableIdsAndInternalNameRowsAreImmutableAndPublicProjectionIsSafe() throws Exception {
        var nameDefinition = nameTables.definitions().getFirst();
        assertThrows(UnsupportedOperationException.class, () -> nameDefinition.tables().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> nameDefinition.tables().getFirst().rows().clear());
        assertThrows(UnsupportedOperationException.class, () -> adventures.definitions().clear());

        String publicNameTable = objectMapper.writeValueAsString(
                nameTablesService.findById(nameDefinition.id().value()));
        assertFalse(publicNameTable.contains("\"tables\""));
        assertFalse(publicNameTable.contains("result"));
        assertFalse(publicNameTable.contains("diceExpression"));
        assertFalse(publicNameTable.contains("path"));

        var adventureDefinition = adventures.findBySource("CoS");
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdventuresController(adventuresService)).build();
        String publicAdventure = mvc.perform(get("/api/adventures/" + adventureDefinition.id().value()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertFalse(publicAdventure.contains("contents"));
        assertFalse(publicAdventure.contains("cover"));
        assertFalse(publicAdventure.contains("path"));
        assertTrue(publicAdventure.contains("adventure:cos"));
    }

    @Test
    void controllersSupportStableIdsAndMetadataFilters() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(
                        new AdventuresController(adventuresService),
                        new NameTablesController(nameTablesService))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();

        mvc.perform(get("/api/adventures").param("source", "PiP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("adventure:pip"));
        mvc.perform(get("/api/adventures/by-name/{name}", "Peril in Pinebrook").param("source", "PiP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("adventure:pip"));
        mvc.perform(get("/api/adventures/adventure:pip"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source.source").value("PiP"));
        mvc.perform(get("/api/name-tables").param("source", "XGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source").value("XGE"));
        mvc.perform(get("/api/name-tables/by-name/Elf").param("source", "XGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("name-table:xge:elf"));
        mvc.perform(get("/api/name-tables/name-table:xge:elf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowCount").isNumber());
        mvc.perform(get("/api/adventures/adventure:missing"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/name-tables/name-table:missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void bothSourceResourcesLoadFromPackagedJarClasspaths() throws Exception {
        Path jarPath = temporaryDirectory.resolve("adventure-name-resources.jar");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            addResource(jar, "data/adventures.json");
            addResource(jar, "data/names.json");
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            AdventureFileDte adventureSource = readFrom(classLoader, "data/adventures.json", AdventureFileDte.class);
            NameTablesFileDte nameSource = readFrom(classLoader, "data/names.json", NameTablesFileDte.class);
            assertEquals(101, adventureSource.adventure().size());
            assertEquals(10, nameSource.name().size());
        }
    }

    @Test
    void rejectsDuplicateIdsAndUnknownSources() {
        var source = adventures.importById("adventure:pip");
        assertNotNull(source);
        assertThrows(IllegalStateException.class, () -> new AdventureCatalog(
                new AdventureFileDte(java.util.List.of(
                        readAdventure("PiP"), readAdventure("PiP"))),
                adventureMapper,
                sourceRegistry));
        assertThrows(IllegalStateException.class, () -> new NameTableCatalog(
                new NameTablesFileDte(List.of(
                        new NameTableDte(
                                "Unknown",
                                "NOPE",
                                1,
                                List.of(new NameTableOptionDte(
                                        "first",
                                        "d6",
                                        List.of(new NameTableRowDte(1, 6, "Example"))))))),
                nameTableMapper,
                sourceRegistry));
    }

    @Test
    void preservesTheAuthoredTerminalSentinelAndRejectsOtherReversedRanges() {
        var terminal = new NameTableDte(
                "Test",
                "XGE",
                1,
                List.of(new NameTableOptionDte(
                        "Names",
                        "d100",
                        List.of(new NameTableRowDte(99, 0, "Terminal result")))));
        var normalized = nameTableMapper.map(terminal);
        assertEquals(99, normalized.tables().getFirst().rows().getFirst().min());
        assertEquals(0, normalized.tables().getFirst().rows().getFirst().max());

        var invalid = new NameTableDte(
                "Test",
                "XGE",
                1,
                List.of(new NameTableOptionDte(
                        "Names",
                        "d100",
                        List.of(new NameTableRowDte(4, 2, "Invalid")))));
        assertThrows(IllegalArgumentException.class, () -> nameTableMapper.map(invalid));
    }

    private <T> T read(String resource, Class<T> type) throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input);
            return objectMapper.readerFor(type)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
    }

    private <T> T readFrom(ClassLoader classLoader, String resource, Class<T> type) throws Exception {
        try (InputStream input = new ClassPathResource(resource, classLoader).getInputStream()) {
            return objectMapper.readerFor(type)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
    }

    private void addResource(JarOutputStream jar, String resource) throws Exception {
        try (InputStream input = new ClassPathResource(resource).getInputStream()) {
            jar.putNextEntry(new JarEntry(resource));
            input.transferTo(jar);
            jar.closeEntry();
        }
    }

    private AdventureDte readAdventure(String source) {
        try {
            return read("data/adventures.json", AdventureFileDte.class).adventure().stream()
                    .filter(adventure -> adventure.source().equals(source))
                    .findFirst()
                    .orElseThrow();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
