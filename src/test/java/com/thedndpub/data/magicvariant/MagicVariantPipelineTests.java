package com.thedndpub.data.magicvariant;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.MagicVariantsController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.dto.magicvariant.MagicVariantKind;
import com.thedndpub.data.dto.magicvariant.MagicVariantCopyResolver;
import com.thedndpub.data.dte.magicvariant.MagicVariantCopyDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantCopyOperationKind;
import com.thedndpub.data.dte.magicvariant.MagicVariantDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantsFileDte;
import com.thedndpub.data.services.MagicVariantCatalog;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.MagicVariantsService;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class MagicVariantPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MagicVariantCatalog catalog;

    @Autowired
    private MagicVariantsService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private ItemCatalog itemCatalog;

    @Autowired
    private MagicVariantCopyResolver copyResolver;

    @Test
    void completeSourceDeserializesWithEveryRootCollectionAndSourceShape() throws Exception {
        MagicVariantsFileDte source;
        try (InputStream input = new ClassPathResource("data/magicvariants.json").getInputStream()) {
            source = strictMapper().readValue(input, MagicVariantsFileDte.class);
        }

        assertEquals(230, source.magicvariant().size());
        assertEquals(List.of("magicvariant"), source._meta().internalCopies());
        assertEquals(127, source.linkedLootTables().get("DMG").size());
        assertEquals(5, source.magicvariant().stream().filter(variant -> variant.type() == null).count());
        assertEquals(5, source.magicvariant().stream().filter(variant -> variant.copy() != null).count());
        assertTrue(source.magicvariant().stream().anyMatch(variant -> variant.excludes() != null
                && variant.excludes().name() != null && variant.excludes().name().values().size() > 1));
    }

    @Test
    void catalogAccountsForUniqueSourceAwareDefinitionsAndClassifiesRules() {
        assertEquals(230, catalog.size());
        assertEquals(225, catalog.statistics().genericRuleCount());
        assertEquals(5, catalog.statistics().itemSpecificTransformationCount());
        assertEquals(127, catalog.linkedLootTableEntryCount());
        assertEquals(230L, catalog.definitions().stream().map(variant -> variant.id()).distinct().count());
        assertEquals(230L, catalog.definitions().stream()
                .map(variant -> variant.name() + "|" + variant.source().identity().code())
                .distinct().count());
        assertEquals(2742, itemCatalog.size(), "Magic Variants does not add or overwrite Item Catalog definitions");
        for (var source : sourceRegistry.records()) {
            assertNotNull(sourceRegistry.find(source.identity().code()));
        }
    }

    @Test
    void requirementsKeepAlternativeGroupsAndExclusionsStructured() {
        var ammunitionRules = catalog.findByName("+1 Ammunition");
        assertEquals(2, ammunitionRules.size());
        assertEquals("DMG", ammunitionRules.getFirst().source().identity().code());
        assertEquals("XDMG", ammunitionRules.getLast().source().identity().code());
        assertEquals(4, ammunitionRules.getFirst().requires().size());
        assertTrue(ammunitionRules.getFirst().requires().stream()
                .allMatch(requirement -> requirement.type() != null));

        var weaponRule = catalog.findById("magic-variant:dmg:1-weapon");
        assertNotNull(weaponRule);
        assertEquals(MagicVariantKind.GENERIC_RULE, weaponRule.kind());
        assertEquals(Boolean.TRUE, weaponRule.excludes().net());
        assertTrue(weaponRule.eligibleItemCount() > 0);

        var woodRule = catalog.findByName("Imbued Wood (Fernian Ash)").getFirst();
        assertEquals(List.of("Crystal", "Orb"), woodRule.excludes().names());
        assertEquals(2, woodRule.requires().size());
        assertEquals("arcane", woodRule.requires().getFirst().spellcastingFocusType());
    }

    @Test
    void transformationsExposeStructuredBenefitsAndOmitDescriptionEntries() throws Exception {
        var armor = catalog.findByName("+1 Armor").getFirst();
        assertEquals("rare", armor.transformation().rarity());
        assertEquals("+1", armor.transformation().bonusArmorClass());
        assertEquals("+1 ", armor.transformation().namePrefix());
        assertTrue(armor.eligibleItemCount() > 0);

        var spellVariant = catalog.findByName("Luck Blade").getFirst();
        assertNotNull(spellVariant.transformation().attachedSpells());

        var mithral = catalog.findByName("Mithral Armor").getFirst();
        assertEquals(Boolean.FALSE, mithral.transformation().stealthDisadvantage());
        JsonNode mithralJson = objectMapper.valueToTree(mithral);
        assertEquals(false, mithralJson.get("transformation").get("stealthDisadvantage").asBoolean());

        JsonNode json = objectMapper.valueToTree(armor);
        assertFalse(json.toString().contains("entries"));
        assertFalse(json.toString().contains("hasFluffImages"));
        assertFalse(json.toString().contains("_copy"));
        assertTrue(json.has("transformation"));
    }

    @Test
    void copyChainsPreserveTheirBaseReferencesAndSupportedOperationsInternally() {
        var specificRules = catalog.definitions().stream()
                .filter(variant -> variant.kind() == MagicVariantKind.ITEM_SPECIFIC_TRANSFORMATION)
                .toList();
        assertEquals(5, specificRules.size());
        assertTrue(specificRules.stream().allMatch(variant -> variant.baseItem() != null));
        assertTrue(specificRules.stream().allMatch(variant -> variant.baseItem().itemId() == null));
        assertEquals(5, catalog.statistics().unresolvedBaseItemReferences());

        var studied = specificRules.stream().filter(variant -> variant.name().startsWith("Studious"))
                .findFirst().orElseThrow();
        var provenance = catalog.findImportProvenance(studied.id());
        assertNotNull(provenance);
        assertTrue(provenance.resolvedRecord().copyChainResolved());
        assertFalse(provenance.resolvedRecord().baseItemResolved());
        assertEquals("Weapon of Warning", studied.baseItem().name());
        assertEquals("XDMG", studied.baseItem().source());
        assertEquals("rare", studied.transformation().rarity());

        long appendEntries = specificRules.stream()
                .flatMap(variant -> catalog.findImportProvenance(variant.id()).sourceRecord().copy()
                        .operations().stream())
                .filter(operation -> operation.operation() == MagicVariantCopyOperationKind.APPEND_ENTRY)
                .count();
        long setProperties = specificRules.stream()
                .flatMap(variant -> catalog.findImportProvenance(variant.id()).sourceRecord().copy()
                        .operations().stream())
                .filter(operation -> operation.operation() == MagicVariantCopyOperationKind.SET_PROPERTY)
                .count();
        long removals = specificRules.stream()
                .flatMap(variant -> catalog.findImportProvenance(variant.id()).sourceRecord().copy()
                        .operations().stream())
                .filter(operation -> operation.operation() == MagicVariantCopyOperationKind.REMOVE_PROPERTY)
                .count();
        long appendedValues = specificRules.stream()
                .flatMap(variant -> catalog.findImportProvenance(variant.id()).sourceRecord().copy()
                        .operations().stream())
                .filter(operation -> operation.operation()
                        == MagicVariantCopyOperationKind.APPEND_IF_ABSENT_STRING)
                .count();
        assertEquals(5, appendEntries);
        assertEquals(16, setProperties);
        assertEquals(5, removals);
        assertEquals(3, appendedValues);
    }

    @Test
    void filtersAndLookupsAreCaseInsensitiveAndDeterministic() {
        assertEquals(2, service.byName("+1 ammunition", null).size());
        assertEquals(1, service.byName("+1 ammunition", "xdmg").size());
        assertEquals(0, service.find(null, "unknown-source", null, null).size());
        assertTrue(service.find(null, null, "gv|dmg", MagicVariantKind.GENERIC_RULE).stream()
                .allMatch(summary -> summary.type().equals("GV|DMG")));
        assertTrue(catalog.summaries().stream().map(summary -> summary.name().toLowerCase())
                .toList().equals(catalog.summaries().stream().map(summary -> summary.name().toLowerCase())
                        .sorted().toList()));
    }

    @Test
    void publicSerializationOmitsRawMarkupAndImportProvenanceButKeepsFalseAndZero() throws Exception {
        var definition = catalog.findByName("+1 Ammunition").getFirst();
        JsonNode json = objectMapper.valueToTree(definition);
        assertFalse(json.toString().contains("{@"));
        assertFalse(json.toString().contains("_mod"));
        assertFalse(json.toString().contains("copyLineage"));
        assertNotNull(json.get("ammunition"));

        var wand = catalog.findByName("Luck Blade").getFirst();
        JsonNode wandJson = objectMapper.valueToTree(wand);
        assertFalse(wandJson.toString().contains("{@"));
    }

    @Test
    void strictDeserializationRejectsUnknownFieldsAndUnsupportedCopyOperations() throws Exception {
        String unknownField = "{\"magicvariant\":[{\"name\":\"Example\",\"source\":\"DMG\","
                + "\"type\":\"GV|DMG\",\"unexpected\":true}]}";
        assertThrows(Exception.class, () -> strictMapper().readValue(unknownField, MagicVariantsFileDte.class));

        String unsupportedOperation = "{\"name\":\"Child\",\"source\":\"AU\",\"_mod\":{"
                + "\"inherits.rarity\":{\"mode\":\"replaceValue\",\"value\":\"rare\"}}}";
        assertThrows(Exception.class, () -> strictMapper().readValue(
                unsupportedOperation, com.thedndpub.data.dte.magicvariant.MagicVariantCopyDte.class));
    }

    @Test
    void copyResolverRejectsDuplicateIdentitiesAndCycles() {
        MagicVariantDte duplicate = variant("Duplicate", "DMG", "GV|DMG", null);
        assertThrows(IllegalStateException.class,
                () -> copyResolver.resolve(List.of(duplicate, duplicate), itemCatalog));

        MagicVariantDte first = variant("First", "AU", null,
                new MagicVariantCopyDte("Second", "AU", null, List.of()));
        MagicVariantDte second = variant("Second", "AU", null,
                new MagicVariantCopyDte("First", "AU", null, List.of()));
        assertThrows(IllegalStateException.class,
                () -> copyResolver.resolve(List.of(first, second), itemCatalog));
    }

    @Test
    void restEndpointsReturnSummariesDetailsAndSourceSpecificMatches() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new MagicVariantsController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
        mvc.perform(get("/api/magic-variants").param("type", "GV|XDMG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].eligibleItemCount").exists());
        mvc.perform(get("/api/magic-variants/magic-variant:dmg:1-weapon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("+1 Weapon"));
        mvc.perform(get("/api/magic-variants/by-name/{name}", "Luck Blade").param("source", "XDMG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("XDMG"));
        mvc.perform(get("/api/magic-variants/not-a-variant"))
                .andExpect(status().isNotFound());
    }

    @Test
    void packagedJarContainsTheCompleteMagicVariantResource() throws Exception {
        Path jarPath = temporaryDirectory.resolve("magic-variants.jar");
        try (InputStream input = new ClassPathResource("data/magicvariants.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/magicvariants.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            try (InputStream input = new ClassPathResource("data/magicvariants.json", classLoader).getInputStream()) {
                MagicVariantsFileDte source = strictMapper().readValue(input, MagicVariantsFileDte.class);
                assertEquals(230, source.magicvariant().size());
            }
        }
    }

    @Test
    void catalogStatisticsDistinguishGenerationCandidatesFromRuleOverlaps() {
        var statistics = catalog.statistics();
        assertEquals(230, statistics.rootRecordCount());
        assertTrue(statistics.potentialGenericItemCombinations() > 0);
        assertEquals(0, statistics.genericRulesWithoutEligibleItems());
        assertTrue(statistics.overlappingRuleApplicationPairs() > 0);
        assertEquals(3_361, statistics.duplicateGeneratedIdentityGroups());
        assertEquals(3_361, statistics.duplicateGeneratedIdentityOccurrences());
        assertEquals(0, statistics.generatedIdentityGroupsCollidingWithItems());
        assertEquals(0, statistics.generatedIdentityCollisionsWithItems());
        assertEquals(0, statistics.conflictingRuleApplicationPairs());
    }

    private ObjectMapper strictMapper() {
        return objectMapper.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    private static MagicVariantDte variant(
            String name,
            String source,
            String type,
            MagicVariantCopyDte copy) {
        return new MagicVariantDte(name, source, type, null, List.of(), List.of(), null,
                null, null, List.of(), null, copy);
    }
}
