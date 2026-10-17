package com.thedndpub.data.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.controllers.SpellsController;
import com.thedndpub.data.dto.spell.SpellMaterialDetails;
import com.thedndpub.data.dto.spell.SpellSchool;
import com.thedndpub.data.dto.spell.api.SpellComponentsDto;
import com.thedndpub.data.dto.spell.api.SpellMaterialDto;
import com.thedndpub.data.dto.spell.api.SpellPublicMapper;
import com.thedndpub.data.dto.spell.api.SpellSchoolDto;
import com.thedndpub.data.dto.spell.api.SpellSummaryDto;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellNotFoundException;
import com.thedndpub.data.services.SpellsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
class SpellPublicApiTests {
    @Autowired
    private SpellCatalog catalog;

    @Autowired
    private SpellsService service;

    @Autowired
    private SpellPublicMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SourceRegistry sourceRegistry;

    @TempDir
    private Path temporaryDirectory;

    private MockMvc mvc;

    @BeforeEach
    void configureRoutes() {
        mvc = MockMvcBuilders.standaloneSetup(new SpellsController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
    }

    @Test
    void catalogAndPublicApiExposeAllDefinitionsAndResolveAllReprintIds() {
        assertEquals(969, catalog.size());
        assertEquals(969, service.findSpells(null, null, null, null, null, null, null).size());

        Set<String> ids = new HashSet<>();
        int reprintCount = 0;
        for (var definition : catalog.findAll()) {
            assertTrue(ids.add(definition.id().value()));
            assertEquals(definition.id().value(), service.findById(definition.id().value()).id());
            reprintCount += definition.sourceRelationships().reprintedAs().size();
            for (var reprint : definition.sourceRelationships().reprintedAs()) {
                var target = catalog.findById(reprint.targetId().value());
                assertTrue(target != null);
                assertEquals(reprint.target(), target.identity());
            }
        }
        assertEquals(969, ids.size());
        assertEquals(389, reprintCount);
        assertThrows(UnsupportedOperationException.class, () -> catalog.findAll().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> catalog.findByName("shield").clear());
    }

    @Test
    void keepsEditionIdentityAndAmbiguousNameMatches() {
        var phbShield = catalog.findById("spell:phb:shield");
        var xphbShield = catalog.findById("spell:xphb:shield");

        assertTrue(phbShield != null);
        assertTrue(xphbShield != null);
        assertNotEquals(phbShield.id(), xphbShield.id());
        assertEquals(2, service.findByName("sHiElD", null).size());
        assertEquals(List.of("spell:phb:shield"), service.findByName("SHIELD", "pHb").stream()
                .map(value -> value.id())
                .toList());
    }

    @Test
    void projectsEveryCorpusSchoolToItsAuthoritativeCodeAndName() {
        Map<String, Long> expectedCounts = Map.of(
                "A", 118L,
                "C", 176L,
                "D", 74L,
                "E", 99L,
                "I", 67L,
                "N", 83L,
                "T", 171L,
                "V", 181L);
        Map<String, String> expectedNames = Map.of(
                "A", "Abjuration",
                "C", "Conjuration",
                "D", "Divination",
                "E", "Enchantment",
                "I", "Illusion",
                "N", "Necromancy",
                "T", "Transmutation",
                "V", "Evocation");
        Map<String, Long> actualCounts = catalog.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        definition -> definition.school(), java.util.stream.Collectors.counting()));

        assertEquals(expectedCounts, actualCounts);
        for (var definition : catalog.findAll()) {
            SpellSchool school = SpellSchool.fromCode(definition.school());
            assertEquals(expectedNames.get(definition.school()), school.displayName());
            var summary = mapper.summary(definition);
            var detail = mapper.detail(definition);
            assertEquals(new SpellSchoolDto(school.code(), school.displayName()), summary.school());
            assertEquals(summary.school(), detail.school());
        }
        assertEquals("Evocation", SpellSchool.fromCode("V").displayName());
        assertThrows(IllegalArgumentException.class, () -> SpellSchool.fromCode("Z"));
    }

    @Test
    void appliesCombinedCaseInsensitiveFiltersAndRejectsInvalidLevels() {
        var fireball = catalog.findByName("fireball").stream()
                .filter(value -> value.source().identity().code().equalsIgnoreCase("PHB"))
                .findFirst()
                .orElseThrow();
        String damageType = fireball.mechanics().damageInflicted().getFirst();

        List<SpellSummaryDto> matched = service.findSpells(
                "FIREBALL",
                "phb",
                fireball.level(),
                fireball.school().toLowerCase(Locale.ROOT),
                fireball.ritual(),
                null,
                damageType.toUpperCase(Locale.ROOT));

        assertEquals(List.of(fireball.id().value()), matched.stream().map(SpellSummaryDto::id).toList());
        assertThrows(IllegalArgumentException.class,
                () -> service.findSpells(null, null, -1, null, null, null, null));
    }

    @Test
    void searchesByRitualConcentrationSchoolAndDamageClassification() {
        assertFalse(service.findSpells(null, null, null, null, true, null, null).isEmpty());
        assertFalse(service.findSpells(null, null, null, null, null, true, null).isEmpty());
        assertFalse(service.findSpells(null, null, null, "V", null, null, null).isEmpty());
        assertFalse(service.findSpells(null, null, null, "eVoCaTiOn", null, null, null).isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> service.findSpells(null, null, null, "Unknown School", null, null, null));
        assertFalse(service.findSpells(null, null, null, null, null, null, "fire").isEmpty());
        assertEquals(service.findSpells(null, null, null, null, null, null, null).stream()
                        .map(SpellSummaryDto::id).toList(),
                service.findSpells(null, null, null, null, null, null, null).stream()
                        .map(SpellSummaryDto::id).toList());
    }

    @Test
    void detailsPreserveStructuredCastingScalingAndMaterialValuesWithoutProse() throws Exception {
        JsonNode reactionSpell = objectMapper.readTree(objectMapper.writeValueAsString(
                service.findById("spell:xphb:banishing-smite")));
        assertEquals("bonus", reactionSpell.path("castingTimes").get(0).path("unit").asText());
        assertTrue(reactionSpell.path("castingTimes").get(0).path("hasCondition").booleanValue());
        assertFalse(reactionSpell.path("castingTimes").get(0).has("condition"));

        var scaledDefinition = catalog.findAll().stream()
                .filter(value -> !value.scaling().isEmpty())
                .findFirst()
                .orElseThrow();
        JsonNode scaledSpell = objectMapper.readTree(objectMapper.writeValueAsString(
                service.findById(scaledDefinition.id().value())));
        assertTrue(scaledSpell.path("scaling").get(0).path("values").isArray());
        assertTrue(scaledSpell.path("scaling").get(0).path("values").get(0).has("levelKey"));
        assertTrue(scaledSpell.path("scaling").get(0).path("values").get(0).has("formula"));

        SpellMaterialDetails costlyMaterial = catalog.findAll().stream()
                .map(value -> value.components() == null ? null : value.components().material())
                .filter(SpellMaterialDetails.class::isInstance)
                .map(SpellMaterialDetails.class::cast)
                .filter(value -> value.cost() != null)
                .findFirst()
                .orElseThrow();
        var materialOwner = catalog.findAll().stream()
                .filter(value -> value.components() != null && value.components().material() == costlyMaterial)
                .findFirst()
                .orElseThrow();
        JsonNode materialSpell = objectMapper.readTree(objectMapper.writeValueAsString(
                service.findById(materialOwner.id().value())));
        assertEquals(costlyMaterial.cost(), materialSpell.path("components").path("material").path("cost").intValue());
        assertFalse(materialSpell.path("components").path("material").has("text"));
    }

    @Test
    void serializedDetailsForEntireCorpusExcludeProseAndImportInternals() throws Exception {
        for (var definition : catalog.findAll()) {
            String json = objectMapper.writeValueAsString(mapper.detail(definition));
            String normalizedJson = json.toLowerCase(Locale.ROOT);

            assertFalse(normalizedJson.contains("\"entries\""));
            assertFalse(normalizedJson.contains("\"entrieshigherlevel\""));
            assertFalse(normalizedJson.contains("\"importmetadata\""));
            assertFalse(normalizedJson.contains("\"hasfluff\""));
            assertFalse(normalizedJson.contains("\"hasfluffimages\""));
            assertFalse(json.contains("{@"), "Markup in public spell " + definition.id().value());
            assertNoEmptyObjects(objectMapper.readTree(json));
        }
    }

    @Test
    void publicSerializationOmitsEmptyValuesButRetainsFalseAndZero() throws Exception {
        SpellSummaryDto dto = new SpellSummaryDto(
                "spell:phb:empty-safe",
                "Cantrip",
                new com.thedndpub.data.dto.source.SourceReference("PHB", 0),
                0,
                new SpellSchoolDto("V", "Evocation"),
                false,
                false,
                List.of(),
                null,
                List.of());

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(dto));
        assertEquals(0, json.path("level").intValue());
        assertEquals(0, json.path("source").path("page").intValue());
        assertFalse(json.path("ritual").booleanValue());
        assertFalse(json.path("concentration").booleanValue());
        assertFalse(json.has("castingTimes"));
        assertFalse(json.has("range"));
        assertFalse(json.has("damageTypes"));

        SpellComponentsDto components = new SpellComponentsDto(false, false, false,
                new SpellMaterialDto(true, 0, false));
        JsonNode componentJson = objectMapper.readTree(objectMapper.writeValueAsString(components));
        assertFalse(componentJson.path("verbal").booleanValue());
        assertEquals(0, componentJson.path("material").path("cost").intValue());
        assertFalse(componentJson.path("material").path("consumed").booleanValue());
    }

    @Test
    void restEndpointsReturnSummaryDetailAmbiguityAndExpectedErrors() throws Exception {
        mvc.perform(get("/api/spells").param("source", "phb").param("level", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        mvc.perform(get("/api/spells").param("school", "Evocation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].school.code").value("V"))
                .andExpect(jsonPath("$[0].school.name").value("Evocation"));

        mvc.perform(get("/api/spells").param("school", "unknown"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/spells/spell:phb:shield"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("spell:phb:shield"))
                .andExpect(jsonPath("$.school.code").value("A"))
                .andExpect(jsonPath("$.school.name").value("Abjuration"))
                .andExpect(jsonPath("$.entries").doesNotExist());

        mvc.perform(get("/api/spells/by-name/Shield"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());

        mvc.perform(get("/api/spells/no-such-spell"))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/spells").param("level", "-1"))
                .andExpect(status().isBadRequest());

        assertThrows(SpellNotFoundException.class, () -> service.findById("spell:phb:missing"));
    }

    @Test
    void catalogLoadsTheCompleteCorpusFromAPackagedJar() throws Exception {
        Path jarPath = temporaryDirectory.resolve("spell-catalog.jar");
        writeSpellResourcesJar(jarPath);

        boolean originalJarCacheSetting = URLConnection.getDefaultUseCaches("jar");
        URLConnection.setDefaultUseCaches("jar", false);
        try {
            try (URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{jarPath.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
                var resolver = new PathMatchingResourcePatternResolver(classLoader);
                SpellCatalog jarCatalog = new SpellCatalog(objectMapper, sourceRegistry, resolver);
                assertEquals(969, jarCatalog.size());
                assertTrue(jarCatalog.findById("spell:phb:shield") != null);
            }
        } finally {
            URLConnection.setDefaultUseCaches("jar", originalJarCacheSetting);
        }
    }

    private void writeSpellResourcesJar(Path jarPath) throws Exception {
        Path sourceDirectory = Path.of("src/main/resources/data/spells");
        try (OutputStream fileOutput = Files.newOutputStream(jarPath);
             JarOutputStream jarOutput = new JarOutputStream(fileOutput);
             var files = Files.list(sourceDirectory)) {
            jarOutput.putNextEntry(new JarEntry("data/"));
            jarOutput.closeEntry();
            jarOutput.putNextEntry(new JarEntry("data/spells/"));
            jarOutput.closeEntry();

            for (Path sourceFile : files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .sorted()
                    .toList()) {
                jarOutput.putNextEntry(new JarEntry("data/spells/" + sourceFile.getFileName()));
                Files.copy(sourceFile, jarOutput);
                jarOutput.closeEntry();
            }
        }
    }

    private void assertNoEmptyObjects(JsonNode node) {
        if (node.isObject()) {
            assertFalse(node.isEmpty(), "Empty public object: " + node);
            node.elements().forEachRemaining(this::assertNoEmptyObjects);
        } else if (node.isArray()) {
            node.elements().forEachRemaining(this::assertNoEmptyObjects);
        }
    }
}
