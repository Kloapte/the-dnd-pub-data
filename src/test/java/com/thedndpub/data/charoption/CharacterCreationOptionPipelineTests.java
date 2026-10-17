package com.thedndpub.data.charoption;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.CharacterCreationOptionsController;
import com.thedndpub.data.controllers.CharacterCreationOptionApiExceptionHandler;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.dto.charoption.CharacterOptionMapper;
import com.thedndpub.data.dte.charoption.CharacterCreationOptionDte;
import com.thedndpub.data.dte.charoption.CharacterCreationOptionsFileDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.services.CharacterCreationOptionCatalog;
import com.thedndpub.data.services.CharacterCreationOptionNotFoundException;
import com.thedndpub.data.services.CharacterCreationOptionsService;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
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
class CharacterCreationOptionPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CharacterCreationOptionCatalog catalog;

    @Autowired
    private CharacterOptionMapper mapper;

    @Autowired
    private CharacterCreationOptionsService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    @Test
    void completeCorpusDeserializesAndNormalizesAllDefinitions() throws Exception {
        CharacterCreationOptionsFileDte source = readFromClasspath();

        assertEquals(44, source.charoption().size());
        assertEquals(44, catalog.size());
        assertEquals(44, catalog.definitions().stream().map(definition -> definition.id().value()).distinct().count());
        assertEquals(17, catalog.summaries().stream().filter(option -> option.optionTypes().contains("CS")).count());
        assertEquals(8, catalog.summaries().stream().filter(option -> option.optionTypes().contains("DG")).count());
        assertEquals(9, catalog.summaries().stream().filter(option -> option.optionTypes().contains("RF:B")).count());
        assertEquals(10, catalog.summaries().stream().filter(option -> option.optionTypes().contains("SG")).count());
        assertTrue(catalog.definitions().stream().allMatch(definition ->
                sourceRegistry.find(definition.source().identity().code()) != null));
        assertTrue(catalog.definitions().stream().allMatch(definition ->
                definition.id().value().startsWith("char-option:")));
    }

    @Test
    void observedIdentityIncludesSourceAndAuthoredOptionTypeWithoutCollisions() {
        var alagondar = catalog.findByName("Alagondar Scion").getFirst();
        var darkGift = catalog.findByName("Echoing Soul").getFirst();

        assertEquals("char-option:idrotf:cs:alagondar-scion", alagondar.id().value());
        assertEquals("IDRotF", alagondar.source().identity().code());
        assertEquals(List.of("CS"), alagondar.optionTypes().stream()
                .map(type -> type.code()).toList());
        assertEquals("char-option:vrgr:dg:echoing-soul", darkGift.id().value());
        assertEquals(44, catalog.definitions().stream()
                .map(definition -> definition.source().identity().code().toLowerCase()
                        + "|" + definition.optionTypes().getFirst().code()
                        + "|" + definition.name().toLowerCase())
                .distinct()
                .count());
    }

    @Test
    void preservesPrerequisiteGroupsAndReprintIdentitiesWithoutInventedTargets() {
        var alagondar = catalog.findByName("Alagondar Scion").getFirst();
        assertEquals(1, alagondar.prerequisites().size());
        assertEquals(List.of("human", "half-elf", "half-orc"), alagondar.prerequisites().getFirst().races()
                .stream().map(race -> race.name()).toList());
        assertTrue(alagondar.prerequisites().getFirst().note().startsWith("If you don't meet"));

        var reprinted = catalog.findByName("Mist Wanderer").getFirst().reprintedAs().getFirst();
        assertEquals("Mist Wanderer", reprinted.name());
        assertEquals("RHW", reprinted.source().code());
        assertEquals("background", reprinted.targetKind());
        assertEquals("Mist Wanderer|RHW", reprinted.authoredUid());
        assertNotNull(sourceRegistry.find(reprinted.source().code()));
        assertEquals("ToD", catalog.findByName("Dragon Scholar").getFirst().otherSources().getFirst()
                .identity().code());
    }

    @Test
    void descriptiveEntryPolymorphismAndInternalProvenanceArePreserved() {
        long entriesCount = catalog.definitions().stream()
                .mapToInt(definition -> catalog.importById(definition.id().value()).provenance().entries().size())
                .sum();
        long optionsWithTables = catalog.definitions().stream()
                .filter(definition -> catalog.importById(definition.id().value()).provenance().entries().stream()
                        .anyMatch(entry -> entry instanceof com.thedndpub.data.dte.common.entry.TableEntryDte))
                .count();
        assertEquals(102, entriesCount);
        assertEquals(12, optionsWithTables);
        assertTrue(catalog.definitions().stream().anyMatch(definition ->
                catalog.importById(definition.id().value()).provenance().hasFluffImages() != null));
        assertTrue(catalog.importById("char-option:mot:sg:heroic-destiny").provenance()
                .hasFluffImages());
    }

    @Test
    void rejectsUnknownFieldsAndUnsupportedOptionTypes() throws Exception {
        assertThrows(Exception.class, () -> objectMapper.readerFor(CharacterCreationOptionsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"charoption\":[],\"unexpected\":true}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(CharacterCreationOptionsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"charoption\":[{\"name\":\"x\",\"source\":\"PHB\",\"page\":1,"
                        + "\"optionType\":[\"CS\"],\"entries\":[{\"type\":\"entries\","
                        + "\"unexpected\":true}]}]}"));

        CharacterCreationOptionDte source = catalog.findByName("Anvilwrought").stream()
                .findFirst()
                .map(definition -> catalog.importById(definition.id().value()))
                .map(record -> new CharacterCreationOptionDte(
                        record.definition().name(),
                        record.definition().source().identity().code(),
                        record.definition().source().page(),
                        null,
                        List.of("UNKNOWN"),
                        record.provenance().entries(),
                        null,
                        null,
                        null))
                .orElseThrow();
        assertThrows(IllegalArgumentException.class, () -> mapper.mapRecord(source));
    }

    @Test
    void validatesSourcesAndRejectsDuplicateAuthoredIdentities() throws Exception {
        CharacterCreationOptionsFileDte all = readFromClasspath();
        CharacterCreationOptionDte first = all.charoption().getFirst();
        assertThrows(IllegalStateException.class, () -> new CharacterCreationOptionCatalog(
                new CharacterCreationOptionsFileDte(List.of(first, first)), mapper, sourceRegistry));

        CharacterCreationOptionDte unknownSource = new CharacterCreationOptionDte(
                "Unknown",
                "UNREGISTERED",
                1,
                null,
                List.of("CS"),
                List.of(),
                null,
                null,
                null);
        assertThrows(IllegalStateException.class, () -> new CharacterCreationOptionCatalog(
                new CharacterCreationOptionsFileDte(List.of(unknownSource)), mapper, sourceRegistry));

        CharacterCreationOptionDte invalidReprintSource = new CharacterCreationOptionDte(
                "Unknown reprint",
                "IDRotF",
                1,
                null,
                List.of("CS"),
                List.of(),
                null,
                null,
                List.of(new com.thedndpub.data.dte.charoption.CharacterOptionReprintDte(
                        "Missing|UNKNOWN", "background")));
        assertThrows(IllegalStateException.class, () -> new CharacterCreationOptionCatalog(
                new CharacterCreationOptionsFileDte(List.of(invalidReprintSource)), mapper, sourceRegistry));

        CharacterCreationOptionDte invalidMarkupSource = new CharacterCreationOptionDte(
                "Unknown markup source",
                "IDRotF",
                1,
                null,
                List.of("CS"),
                List.of(new TextEntryDte("See {@book a chapter|UNKNOWN|1|Title}.")),
                null,
                null,
                null);
        assertThrows(IllegalStateException.class, () -> new CharacterCreationOptionCatalog(
                new CharacterCreationOptionsFileDte(List.of(invalidMarkupSource)), mapper, sourceRegistry));
    }

    @Test
    void serviceFiltersAreCaseInsensitiveAndAmbiguityIsPreserved() {
        assertEquals(17, service.find(null, "idrotf", "cs").size());
        assertEquals(1, service.find("alagondar", "IDROTF", "CS").size());
        assertEquals(44, service.find(null, null, null).size());
        assertEquals(1, service.findByName("anvilwrought", "mot").size());
        assertThrows(CharacterCreationOptionNotFoundException.class, () -> service.findById("char-option:missing"));
    }

    @Test
    void publicDtoSerializationExcludesEntriesAndImporterMetadata() throws Exception {
        String detail = objectMapper.writeValueAsString(service.findById("char-option:vrgr:dg:echoing-soul"));
        assertFalse(detail.contains("entries"));
        assertFalse(detail.contains("hasFluffImages"));
        assertFalse(detail.contains("sourceAvailability"));
        assertFalse(detail.contains("{@"));
        assertFalse(detail.contains("Mists whisper"));

        String alagondar = objectMapper.writeValueAsString(service.findById(
                "char-option:idrotf:cs:alagondar-scion"));
        assertTrue(alagondar.contains("\"raceNames\":[\"human\",\"half-elf\",\"half-orc\"]"));
        assertFalse(alagondar.contains("If you don't meet this prerequisite"));
        assertFalse(alagondar.contains("targetId"));
        assertFalse(alagondar.contains("candidateIds"));
        assertFalse(alagondar.contains("resolutionOutcome"));
        assertThrows(UnsupportedOperationException.class,
                () -> service.findByName("Alagondar Scion", null).getFirst().optionTypes().clear());
    }

    @Test
    void restRoutesSupportFilteringLookupAndSourceAwareIDs() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new CharacterCreationOptionsController(service))
                .setControllerAdvice(
                        new CharacterCreationOptionApiExceptionHandler(),
                        new MonsterApiExceptionHandler())
                .build();

        mvc.perform(get("/api/character-creation-options")
                        .param("source", "IDROTF")
                        .param("optionType", "cs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("IDRotF"));
        mvc.perform(get("/api/character-creation-options/char-option:idrotf:cs:alagondar-scion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionTypes[0]").value("CS"));
        mvc.perform(get("/api/character-creation-options/by-name/{name}", "Alagondar Scion")
                        .param("source", "IDRotF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("char-option:idrotf:cs:alagondar-scion"));
        mvc.perform(get("/api/character-creation-options/char-option:missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crossCatalogResolverSnapshotIsUnchangedAndUnsupportedLinksStayUnresolved() {
        var snapshot = resolutionCoordinator.snapshot();
        var existingResults = snapshot.results().stream()
                .filter(result -> !result.reference().owner().catalog().equals("cult"))
                .filter(result -> !result.reference().owner().catalog().equals("boon"))
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
        assertEquals(4370, snapshot.results().size() - snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("trap")
                        || result.reference().owner().catalog().equals("hazard")
                        || result.reference().owner().catalog().equals("vehicle")
                        || result.reference().owner().catalog().equals("vehicle-upgrade")
                        || result.reference().owner().catalog().equals("variant-rule")
                        || result.reference().owner().catalog().equals("deity")
                        || result.reference().owner().catalog().equals("treasure-table"))
                .count());
        assertEquals(3875, existingResults.size());
        assertEquals(1697, existingResults.stream().filter(result ->
                result.outcome().name().equals("RESOLVED")).count());
        assertEquals(1108, existingResults.stream().filter(result ->
                result.outcome().name().equals("AMBIGUOUS")).count());
        assertEquals(30, existingResults.stream().filter(result ->
                result.outcome().name().equals("UNRESOLVED")).count());
        assertEquals(1040, existingResults.stream().filter(result ->
                result.outcome().name().equals("DEFERRED")).count());
        assertEquals(4, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("object")).count());
        assertEquals(110, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("facility")).count());
        assertEquals(5, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("cult")
                        || result.reference().owner().catalog().equals("boon"))
                .filter(result -> result.outcome().name().equals("RESOLVED"))
                .count());
        assertEquals(8, catalog.definitions().stream()
                .mapToLong(definition -> definition.prerequisites().stream()
                        .mapToLong(prerequisite -> prerequisite.races().size()).sum()
                        + definition.reprintedAs().size())
                .sum());
    }

    @Test
    void packagedJarResourcesDeserializeTheCompleteCorpus() throws Exception {
        Path jarPath = temporaryDirectory.resolve("character-options.jar");
        try (InputStream input = new ClassPathResource("data/charcreationoptions.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/charcreationoptions.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null);
             InputStream input = new ClassPathResource("data/charcreationoptions.json", classLoader).getInputStream()) {
            CharacterCreationOptionsFileDte source = objectMapper.readerFor(CharacterCreationOptionsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
            assertEquals(44, source.charoption().size());
        }
    }

    private CharacterCreationOptionsFileDte readFromClasspath() throws Exception {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("data/charcreationoptions.json")) {
            assertNotNull(input);
            return objectMapper.readerFor(CharacterCreationOptionsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
    }
}
