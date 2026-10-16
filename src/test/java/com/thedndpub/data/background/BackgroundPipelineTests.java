package com.thedndpub.data.background;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.BackgroundsController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.dto.background.BackgroundCopyResolver;
import com.thedndpub.data.dto.background.BackgroundMapper;
import com.thedndpub.data.dto.background.BackgroundSummaryDto;
import com.thedndpub.data.dte.background.BackgroundCopyDte;
import com.thedndpub.data.dte.background.BackgroundDte;
import com.thedndpub.data.dte.background.BackgroundsFileDte;
import com.thedndpub.data.services.BackgroundCatalog;
import com.thedndpub.data.services.BackgroundsService;
import com.thedndpub.data.services.ItemCatalog;
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
class BackgroundPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BackgroundCatalog catalog;

    @Autowired
    private BackgroundMapper mapper;

    @Autowired
    private BackgroundsService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private ItemCatalog itemCatalog;

    @Test
    void strictlyDeserializesFullCorpusAndAccountsForAllRootRecords() throws Exception {
        BackgroundsFileDte source;
        try (InputStream input = new ClassPathResource("data/backgrounds.json").getInputStream()) {
            source = strictMapper().readValue(input, BackgroundsFileDte.class);
        }
        assertEquals(171, source.background().size());
        assertEquals(List.of("background"), source._meta().internalCopies());
        assertEquals(26, source.background().stream().filter(background -> background.copy() != null).count());
        assertEquals(0, source.background().stream().filter(background -> background.entries() != null
                && background.entries().isEmpty()).count());
        assertEquals(70, source.background().stream().filter(background -> "one".equals(background.edition())).count());
    }

    @Test
    void catalogExpandsCopiesWithoutMergingSourceSpecificBackgrounds() {
        assertEquals(171, catalog.rootCount());
        assertEquals(171, catalog.size());
        assertEquals(26, catalog.copyCount());
        assertEquals(171L, catalog.definitions().stream().map(value -> value.id().value()).distinct().count());
        assertEquals(171L, catalog.definitions().stream()
                .map(value -> value.name() + "|" + value.source().identity().code()).distinct().count());
        assertEquals(2742, itemCatalog.size());
        assertEquals("background:phb:acolyte", catalog.findById("background:phb:acolyte").id().value());
        assertEquals("background:xphb:acolyte", catalog.findById("background:xphb:acolyte").id().value());
        assertNotNull(catalog.findImportProvenance("background:bgdia:baldur-s-gate-acolyte"));
        var copied = catalog.definitions().stream().filter(background -> background.parentId() != null).toList();
        assertEquals(26, copied.size());
        assertEquals(21, copied.stream().filter(background -> background.sourceRecord().copy().modification() != null)
                .count());
        assertEquals(23, copied.stream().flatMap(background -> background.sourceRecord().copy().modification()
                        == null ? java.util.stream.Stream.empty()
                        : background.sourceRecord().copy().modification().operations().stream())
                .count());
        assertEquals(13, copied.stream().flatMap(background -> background.sourceRecord().copy().modification()
                        == null ? java.util.stream.Stream.empty()
                        : background.sourceRecord().copy().modification().operations().stream())
                .filter(operation -> operation.mode().equals("insertArr")).count());
        assertEquals(10, copied.stream().flatMap(background -> background.sourceRecord().copy().modification()
                        == null ? java.util.stream.Stream.empty()
                        : background.sourceRecord().copy().modification().operations().stream())
                .filter(operation -> operation.mode().equals("replaceArr")).count());
    }

    @Test
    void preservesWeightedAbilityChoicesAndFeatReferences() {
        var acolyte = mapper.toDto(catalog.findById("background:xphb:acolyte"));
        assertEquals(List.of("int", "wis", "cha"), acolyte.abilityChoices().getFirst().abilities());
        assertEquals(List.of(2, 1), acolyte.abilityChoices().getFirst().weights());
        assertEquals(List.of(1, 1, 1), acolyte.abilityChoices().getLast().weights());
        assertTrue(acolyte.feats().stream().anyMatch(feat -> feat.uid().equals("magic initiate; cleric|xphb")
                && feat.resolution().equals("RESOLVED")
                && feat.selection().equals("cleric")));
        assertTrue(mapper.toDto(catalog.findById("background:phb:acolyte")).reprintedAs().stream()
                .anyMatch(reprint -> reprint.name().equals("Acolyte") && reprint.source().equals("XPHB")));
        assertTrue(catalog.findById("background:phb:acolyte").ability().isEmpty());
    }

    @Test
    void preservesProficiencyChoicesAndSourceEditionLanguageDifferences() {
        var xphb = mapper.toDto(catalog.findById("background:xphb:acolyte"));
        var phb = mapper.toDto(catalog.findById("background:phb:acolyte"));
        assertTrue(xphb.toolProficiencies().stream()
                .anyMatch(group -> group.grants().containsKey("calligrapher's supplies")));
        assertTrue(phb.languageProficiencies().stream()
                .anyMatch(group -> group.grants().containsKey("anyStandard")));
        assertTrue(xphb.languageProficiencies().isEmpty());
        var hauntedSkill = mapper.toDto(catalog.findById("background:vrgr:haunted-one"));
        assertTrue(hauntedSkill.skillProficiencies().stream().flatMap(group -> group.grants().values().stream())
                .anyMatch(grant -> grant.choice() != null && grant.choice().count() == 2
                        && grant.choice().from().contains("survival")));
        var hauntedFeat = mapper.toDto(catalog.findById("background:rhw:haunted-one"));
        assertTrue(hauntedFeat.featGrants().stream()
                .anyMatch(group -> group.grants().containsKey("anyFromCategory")));
    }

    @Test
    void startingEquipmentKeepsPackagesAlternativesQuantitiesAndCurrency() {
        var acolyte = mapper.toDto(catalog.findById("background:xphb:acolyte"));
        var equipment = acolyte.startingEquipment().getFirst();
        assertTrue(equipment.options().containsKey("A"));
        assertTrue(equipment.options().containsKey("B"));
        assertTrue(equipment.options().get("A").stream().anyMatch(entry -> Integer.valueOf(10).equals(entry.quantity())));
        assertEquals(800, equipment.options().get("A").stream()
                .filter(entry -> "CURRENCY".equals(entry.kind())).findFirst().orElseThrow().valueCopper());
        assertEquals(5000, equipment.options().get("B").getFirst().valueCopper());
        var legacy = mapper.toDto(catalog.findById("background:phb:acolyte"));
        assertTrue(legacy.startingEquipment().getFirst().options().containsKey("_"));
        assertTrue(legacy.startingEquipment().get(1).options().containsKey("a"));
        assertTrue(legacy.startingEquipment().get(1).options().containsKey("b"));
        assertTrue(itemCatalog.size() > 0);
    }

    @Test
    void reportsResolvedAndUnresolvedItemReferencesAndKeepsFreeTextSeparate() {
        assertEquals(718, catalog.referenceStatistics().resolvedItems());
        assertEquals(8, catalog.referenceStatistics().unresolvedItems());
        assertEquals(0, catalog.referenceStatistics().ambiguousItems());
        assertEquals(204, catalog.referenceStatistics().textEquipment());
        var backgrounds = catalog.definitions().stream().filter(value -> !value.startingEquipment().isEmpty()).toList();
        assertTrue(backgrounds.stream().flatMap(value -> mapper.toDto(value).startingEquipment().stream())
                .flatMap(value -> value.options().values().stream()).flatMap(List::stream)
                .anyMatch(entry -> entry.item() != null
                        && entry.item().resolution().name().equals("UNRESOLVED")));
    }

    @Test
    void featureMetadataAndCharacteristicTableMetadataExcludeSourceProse() throws Exception {
        var definition = catalog.findById("background:phb:acolyte");
        var dto = mapper.toDto(definition);
        assertTrue(dto.features().stream().anyMatch(feature -> feature.name().contains("Shelter of the Faithful")));
        assertTrue(dto.characteristicTables().stream().anyMatch(table -> "Personality Trait".equals(
                table.columns().getLast())));
        JsonNode json = objectMapper.valueToTree(dto);
        assertFalse(json.toString().contains("You can find common ground"));
        assertFalse(json.toString().contains("{@"));
        assertFalse(json.toString().contains("copyLineage"));
        var phb = objectMapper.valueToTree(mapper.toDto(catalog.findById("background:phb:acolyte")));
        assertFalse(phb.has("abilityChoices"));
        assertFalse(phb.has("prerequisites"));
    }

    @Test
    void preservesStructuredSpellSchedulesAndCampaignPrerequisites() {
        var spellBackground = catalog.definitions().stream()
                .filter(background -> background.effectiveRecord().additionalSpells() != null)
                .findFirst().orElseThrow();
        var spellDto = mapper.toDto(spellBackground);
        assertTrue(spellDto.additionalSpells().stream().anyMatch(spells -> spells.expanded().containsKey("s0")));
        assertTrue(spellDto.additionalSpells().stream().flatMap(spells -> spells.expanded().values().stream())
                .flatMap(List::stream).anyMatch(spell -> spell.contains("#c") || spell.contains("|")));

        var prerequisite = catalog.definitions().stream()
                .filter(background -> background.effectiveRecord().prerequisite() != null)
                .findFirst().orElseThrow();
        assertFalse(mapper.toDto(prerequisite).prerequisites().isEmpty());
    }

    @Test
    void copyResolverAppliesEntryOperationsAndRejectsMissingParentsAndCycles() throws Exception {
        var bgdia = catalog.findById("background:bgdia:baldur-s-gate-acolyte");
        assertTrue(bgdia.entries().size() > 3);
        assertNotNull(bgdia.parentId());
        assertEquals("background:phb:acolyte", bgdia.parentId().value());
        BackgroundsFileDte source;
        try (InputStream input = new ClassPathResource("data/backgrounds.json").getInputStream()) {
            source = strictMapper().readValue(input, BackgroundsFileDte.class);
        }
        var first = new BackgroundDte("First", "AU", null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                new BackgroundCopyDte("Second", "AU", null));
        var second = new BackgroundDte("Second", "AU", null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                new BackgroundCopyDte("First", "AU", null));
        var invalid = new BackgroundsFileDte(source._meta(), List.of(first, second));
        assertThrows(IllegalStateException.class, () -> new BackgroundCopyResolver().resolve(invalid));
        var duplicate = new BackgroundsFileDte(source._meta(), List.of(first, first));
        assertThrows(IllegalStateException.class, () -> new BackgroundCopyResolver().resolve(duplicate));
    }

    @Test
    void sourcesAreValidatedAndOrderingFilteringAndLookupsAreDeterministic() {
        for (var definition : catalog.definitions()) {
            assertNotNull(sourceRegistry.find(definition.source().identity().code()));
            definition.additionalSources().forEach(reference -> assertNotNull(
                    sourceRegistry.find(reference.identity().code())));
        }
        assertEquals(2, service.byName("Acolyte", null).size());
        assertEquals(1, service.byName("Acolyte", "xphb").size());
        assertTrue(service.find("acolyte", "XPHB").stream().allMatch(item -> item.source()
                .identity().code().equals("XPHB")));
        var comparator = java.util.Comparator.comparing(BackgroundSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER);
        assertEquals(catalog.summaries().stream().sorted(comparator).toList(), catalog.summaries());
    }

    @Test
    void restEndpointsReturnSummariesDetailsAndSourceSpecificMatches() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new BackgroundsController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
        mvc.perform(get("/api/backgrounds").param("source", "XPHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
        mvc.perform(get("/api/backgrounds/background:xphb:acolyte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acolyte"));
        mvc.perform(get("/api/backgrounds/by-name/{name}", "Acolyte").param("source", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("PHB"));
        mvc.perform(get("/api/backgrounds/not-a-background"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsUnknownSourceFieldsAndUnsupportedCopyOperations() throws Exception {
        String unknown = "{\"background\":[{\"name\":\"x\",\"source\":\"PHB\",\"other\":true}]}";
        assertThrows(Exception.class, () -> strictMapper().readValue(unknown, BackgroundsFileDte.class));
        String unsupported = "{\"name\":\"x\",\"source\":\"PHB\",\"_copy\":{\"name\":\"y\","
                + "\"source\":\"PHB\",\"_mod\":{\"ability\":{\"mode\":\"replace\"}}}}";
        assertThrows(Exception.class, () -> strictMapper().readValue(unsupported, BackgroundDte.class));
        String malformedEquipment = "{\"name\":\"x\",\"source\":\"PHB\",\"startingEquipment\":["
                + "{\"_\":[{\"item\":\"book|phb\",\"unexpected\":true}]}]}";
        assertThrows(Exception.class, () -> strictMapper().readValue(malformedEquipment, BackgroundDte.class));

        BackgroundDte invalidSource = new BackgroundDte("Invalid", "ZZZ", null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null);
        assertThrows(IllegalStateException.class, () -> new BackgroundCatalog(
                new BackgroundsFileDte(sourceMetadata(), List.of(invalidSource)), mapper, sourceRegistry));
    }

    @Test
    void packagedJarContainsBackgroundSource() throws Exception {
        Path jarPath = temporaryDirectory.resolve("backgrounds.jar");
        try (InputStream input = new ClassPathResource("data/backgrounds.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/backgrounds.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }
        try (URLClassLoader loader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            try (InputStream input = new ClassPathResource("data/backgrounds.json", loader).getInputStream()) {
                assertEquals(171, strictMapper().readValue(input, BackgroundsFileDte.class).background().size());
            }
        }
    }

    private ObjectMapper strictMapper() {
        return objectMapper.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    private com.thedndpub.data.dte.background.BackgroundMetadataDte sourceMetadata() {
        return new com.thedndpub.data.dte.background.BackgroundMetadataDte(List.of("background"));
    }
}
