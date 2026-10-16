package com.thedndpub.data.feat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.FeatsController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.dto.feat.FeatMapper;
import com.thedndpub.data.dte.feat.FeatDte;
import com.thedndpub.data.dte.feat.FeatEntryModificationDte;
import com.thedndpub.data.dte.feat.FeatsFileDte;
import com.thedndpub.data.dte.feat.FeatVersionDte;
import com.thedndpub.data.dte.feat.FeatVersionModDte;
import com.thedndpub.data.dte.feat.FeatVersionExpander;
import com.thedndpub.data.dte.feat.FeatReplaceNameDte;
import com.thedndpub.data.dte.feat.FeatModificationNamedItemsDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.FeatsService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class FeatPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeatCatalog catalog;

    @Autowired
    private FeatMapper mapper;

    @Autowired
    private FeatsService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Test
    void entireFeatCorpusDeserializesAndUsesRegisteredSources() throws Exception {
        FeatsFileDte source;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/feats.json")) {
            assertNotNull(input);
            source = objectMapper.readerFor(FeatsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
        assertEquals(305, source.feat().size());
        assertEquals(305, catalog.rootCount());
        assertEquals(9, catalog.variantCount());
        assertEquals(314, catalog.size());
        assertTrue(List.of("PHB", "XPHB", "BGG", "EFA", "FRHoF")
                .stream().allMatch(code -> sourceRegistry.find(code) != null));
    }

    @Test
    void sourceSpecificIdentityAndReprintRelationshipsStayDistinct() {
        var legacy = catalog.findById("feat:phb:alert");
        var current = catalog.findById("feat:xphb:alert");
        assertNotNull(legacy);
        assertNotNull(current);
        assertEquals("PHB", legacy.source().identity().code());
        assertEquals("XPHB", current.source().identity().code());
        assertEquals("Alert", legacy.reprintedAs().getFirst().name());
        assertEquals("XPHB", legacy.reprintedAs().getFirst().source().code());
    }

    @Test
    void alternativesAbilityChoicesAndClassLevelPrerequisitesRemainStructured() {
        var initiative = catalog.findById("feat:dsotdq:initiate-of-high-sorcery");
        assertEquals(3, initiative.prerequisites().size());
        assertTrue(catalog.findById("feat:xphb:ability-score-improvement").abilityIncreases().size() > 0);

        FeatDte asi = sourceFeat("Ability Score Improvement", "XPHB");
        var ability = asi.ability().getFirst();
        assertEquals(List.of("str", "dex", "con", "int", "wis", "cha"), ability.choose().from());
        assertEquals(2, ability.choose().amount());

        FeatDte classPrerequisite = sourceFeat("Initiate of High Sorcery", "DSotDQ");
        assertTrue(classPrerequisite.prerequisite().stream().anyMatch(group -> group.level() != null));
    }

    @Test
    void versionModificationsAreRetainedAndSourceValidated() {
        assertEquals(3, sourceFeat("Magic Initiate", "XPHB").versions().size());
        assertEquals(6, sourceFeat("Strike of the Giants", "BGG").versions().size());
        assertEquals(3, catalog.importById("feat:xphb:magic-initiate").sourceRecord().versions().size());
        assertEquals("XPHB", sourceRegistry.find("XPHB").identity().code());
    }

    @Test
    void allVariantsAreIndexedWithParentIdentityAndInheritedFields() {
        List<String> ids = List.of(
                "feat:xphb:magic-initiate-cleric", "feat:xphb:magic-initiate-druid",
                "feat:xphb:magic-initiate-wizard", "feat:bgg:strike-of-the-giants-cloud",
                "feat:bgg:strike-of-the-giants-fire", "feat:bgg:strike-of-the-giants-frost",
                "feat:bgg:strike-of-the-giants-hill", "feat:bgg:strike-of-the-giants-stone",
                "feat:bgg:strike-of-the-giants-storm");
        for (String id : ids) {
            var variant = catalog.findById(id);
            assertNotNull(variant, id);
            assertTrue(variant.variant());
            assertNotNull(catalog.findById(variant.parentId().value()));
            assertEquals(id, service.findByName(variant.name(), variant.source().identity().code())
                    .getFirst().id().value());
        }
        var parent = catalog.findById("feat:xphb:magic-initiate");
        var cleric = catalog.findById("feat:xphb:magic-initiate-cleric");
        assertFalse(parent.variant());
        assertEquals(parent.entries(), sourceFeat("Magic Initiate", "XPHB").entries());
        assertEquals(parent.category(), cleric.category());
        assertEquals(parent.prerequisites(), cleric.prerequisites());
        assertEquals("XPHB", cleric.source().identity().code());
        assertEquals(1, cleric.additionalSpells().size());
    }

    @Test
    void sourceEntryOperationsProduceTheNineExpectedVariants() throws Exception {
        for (String strike : List.of("Cloud", "Fire", "Frost", "Hill", "Stone", "Storm")) {
            var variant = catalog.findById("feat:bgg:strike-of-the-giants-" + strike.toLowerCase());
            assertEquals(2, variant.entries().size());
            var chosen = (com.thedndpub.data.dte.common.entry.NamedEntryDte) variant.entries().get(1);
            assertEquals(strike + " Strike", chosen.name());
        }
        for (String className : List.of("Cleric", "Druid", "Wizard")) {
            var variant = catalog.findById("feat:xphb:magic-initiate-" + className.toLowerCase());
            List<String> names = variant.entries().stream()
                    .filter(com.thedndpub.data.dte.common.entry.NamedEntryDte.class::isInstance)
                    .map(com.thedndpub.data.dte.common.entry.NamedEntryDte.class::cast)
                    .map(com.thedndpub.data.dte.common.entry.NamedEntryDte::name)
                    .toList();
            assertTrue(names.contains("Two Cantrips"));
            assertTrue(names.contains("Level 1 Spell"));
            var cantrips = (NamedEntryDte) variant.entries().stream()
                    .filter(NamedEntryDte.class::isInstance)
                    .map(NamedEntryDte.class::cast)
                    .filter(entry -> entry.name().equals("Two Cantrips"))
                    .findFirst().orElseThrow();
            assertTrue(((TextEntryDte) cantrips.entries().getFirst()).text().contains(className));

            String publicJson = objectMapper.writeValueAsString(service.findById(variant.id().value()));
            assertTrue(publicJson.contains("class=" + className));
            assertFalse(publicJson.contains("{@"));
            assertFalse(publicJson.contains("_mod"));
            assertFalse(publicJson.contains("\"entries\""));
        }
    }

    @Test
    void entryModificationInstructionsApplyInSourceOrder() {
        FeatDte parent = withEntries(feat("Fixture", "PHB"), List.of(
                new NamedEntryDte("entries", "Target", null, List.of(new TextEntryDte("original")), null, null)));
        var first = new FeatEntryModificationDte("replaceArr", new FeatReplaceNameDte("Target"), null,
                new FeatModificationNamedItemsDte("Target", List.of(new TextEntryDte("first"))));
        var second = new FeatEntryModificationDte("replaceArr", new FeatReplaceNameDte("Target"), null,
                new FeatModificationNamedItemsDte("Target", List.of(new TextEntryDte("second"))));
        FeatVersionDte version = new FeatVersionDte("Fixture Variant", "PHB",
                new FeatVersionModDte(List.of(first, second)), null);

        var expanded = new FeatVersionExpander().expand(withVersions(parent, List.of(version)));
        var variant = expanded.get(1).definition();
        var result = (NamedEntryDte) variant.entries().getFirst();
        assertEquals("second", ((TextEntryDte) result.entries().getFirst()).text());
    }

    @Test
    void rejectsUnsupportedVersionOperationsAndDuplicateGeneratedIdentities() {
        FeatDte parent = sourceFeat("Magic Initiate", "XPHB");
        FeatVersionDte unsupported = new FeatVersionDte("Unsupported", "XPHB",
                new FeatVersionModDte(List.of(new FeatEntryModificationDte("setProp", null, null, null))), null);
        assertThrows(IllegalArgumentException.class,
                () -> new FeatVersionExpander().expand(withVersions(parent, List.of(unsupported))));

        FeatVersionDte duplicate = new FeatVersionDte(parent.name(), parent.source(), null, null);
        assertThrows(IllegalStateException.class,
                () -> new FeatCatalog(new FeatsFileDte(List.of(withVersions(parent, List.of(duplicate)))),
                        mapper, sourceRegistry));
    }

    @Test
    void filtersSortsAndProjectsWithoutEntriesOrImportFlags() throws Exception {
        assertEquals(2, service.findByName("ALERT", null).size());
        assertEquals(1, service.findByName("Alert", "XPHB").size());
        assertEquals(80, service.findFeats(null, "xphb", null).size());
        assertEquals(95, service.findFeats(null, null, "G").size());
        assertThrows(UnsupportedOperationException.class, () -> catalog.summaries().clear());

        String json = objectMapper.writeValueAsString(service.findById("feat:xphb:ability-score-improvement"));
        assertTrue(json.contains("\"page\""));
        assertTrue(json.contains("prerequisites"));
        assertFalse(json.contains("entries"));
        assertFalse(json.contains("basicRules2024"));
        assertFalse(json.contains("{@"));
    }

    @Test
    void endpointsReturnSummariesDetailsAndAllSourceSpecificNameMatches() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new FeatsController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
        mvc.perform(get("/api/feats").param("name", "alert").param("source", "phb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("feat:phb:alert"));
        mvc.perform(get("/api/feats/feat:xphb:alert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source.page").exists());
        mvc.perform(get("/api/feats/by-name/Alert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
        mvc.perform(get("/api/feats/feat:xphb:magic-initiate-cleric"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.additionalSpells[0].innate.kind").value("object"));
        mvc.perform(get("/api/feats/by-name/{name}", "Magic Initiate; Cleric").param("source", "XPHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("feat:xphb:magic-initiate-cleric"));
        mvc.perform(get("/api/feats/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsUnknownSourcesAndDuplicateSourceSpecificIdentities() {
        FeatDte unknown = feat("Unknown", "NOT_REGISTERED");
        assertThrows(IllegalStateException.class,
                () -> new FeatCatalog(new FeatsFileDte(List.of(unknown)), mapper, sourceRegistry));
        assertThrows(IllegalStateException.class,
                () -> new FeatCatalog(new FeatsFileDte(List.of(
                        feat("Duplicate", "PHB"), feat("duplicate", "phb"))), mapper, sourceRegistry));
    }

    @Test
    void featSourceLoadsFromPackagedJarResource() throws Exception {
        Path jarPath = temporaryDirectory.resolve("feats-resource.jar");
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/feats.json");
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            assertNotNull(input);
            jar.putNextEntry(new JarEntry("data/feats.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null);
             InputStream input = classLoader.getResourceAsStream("data/feats.json")) {
            assertNotNull(input);
            FeatsFileDte packaged = objectMapper.readerFor(FeatsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
            assertEquals(305, packaged.feat().size());
        }
    }

    private FeatDte sourceFeat(String name, String source) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/feats.json")) {
            return objectMapper.readerFor(FeatsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .<FeatsFileDte>readValue(input).feat().stream()
                    .filter(feat -> feat.name().equals(name) && feat.source().equalsIgnoreCase(source))
                    .findFirst()
                    .orElseThrow();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static FeatDte feat(String name, String source) {
        return new FeatDte(name, source, 1, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null);
    }

    private static FeatDte withVersions(FeatDte feat, List<FeatVersionDte> versions) {
        return new FeatDte(feat.name(), feat.source(), feat.page(), feat.category(), feat.prerequisite(), feat.ability(),
                feat.additionalSources(), feat.additionalSpells(), feat.skillProficiencies(), feat.expertise(),
                feat.languageProficiencies(), feat.toolProficiencies(), feat.weaponProficiencies(),
                feat.armorProficiencies(), feat.savingThrowProficiencies(), feat.skillToolLanguageProficiencies(),
                feat.conditionImmune(), feat.immune(), feat.resist(), feat.senses(), feat.bonusSenses(),
                feat.optionalfeatureProgression(), feat.traitTags(), feat.entries(), feat.reprintedAs(),
                feat.repeatable(), feat.repeatableHidden(), feat.srd(), feat.srd52(), feat.basicRules2024(),
                feat.hasFluffImages(), versions);
    }

    private static FeatDte withEntries(FeatDte feat,
                                      List<com.thedndpub.data.dte.common.entry.EntryDte> entries) {
        return new FeatDte(feat.name(), feat.source(), feat.page(), feat.category(), feat.prerequisite(), feat.ability(),
                feat.additionalSources(), feat.additionalSpells(), feat.skillProficiencies(), feat.expertise(),
                feat.languageProficiencies(), feat.toolProficiencies(), feat.weaponProficiencies(),
                feat.armorProficiencies(), feat.savingThrowProficiencies(), feat.skillToolLanguageProficiencies(),
                feat.conditionImmune(), feat.immune(), feat.resist(), feat.senses(), feat.bonusSenses(),
                feat.optionalfeatureProgression(), feat.traitTags(), entries, feat.reprintedAs(), feat.repeatable(),
                feat.repeatableHidden(), feat.srd(), feat.srd52(), feat.basicRules2024(), feat.hasFluffImages(),
                feat.versions());
    }
}
