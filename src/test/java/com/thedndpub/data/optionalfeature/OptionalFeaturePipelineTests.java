package com.thedndpub.data.optionalfeature;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.controllers.OptionalFeaturesController;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureConsumptionDto;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureMapper;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureProficiencyGrantDto;
import com.thedndpub.data.dte.optionalfeature.OptionalFeatureDte;
import com.thedndpub.data.dte.optionalfeature.OptionalFeaturesFileDte;
import com.thedndpub.data.services.OptionalFeatureCatalog;
import com.thedndpub.data.services.OptionalFeaturesService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
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
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class OptionalFeaturePipelineTests {
    private static final Set<String> EXPECTED_FEATURE_TYPES = Set.of(
            "EI", "MV:B", "AI", "FS:F", "FS:R", "AS", "FS:P", "ED", "MM", "RP", "RN", "FS:B", "PB");

    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OptionalFeatureCatalog catalog;

    @Autowired
    private OptionalFeaturesService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private OptionalFeatureMapper mapper;

    @Test
    void fullCorpusLoadsFromClasspathAndAccountsForEveryRootDefinition() throws Exception {
        assertTrue(new ClassPathResource("data/optionalfeatures.json").exists());
        OptionalFeaturesFileDte source;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/optionalfeatures.json")) {
            assertNotNull(input);
            source = objectMapper.readerFor(OptionalFeaturesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
        assertEquals(221, source.optionalfeature().size());
        assertEquals(221, catalog.rootCount());
        assertEquals(0, catalog.expandedCount());
        assertEquals(221, catalog.size());
        assertEquals(221, catalog.summaries().size());
    }

    @Test
    void identitiesAreUniqueAndSourceQualified() {
        List<String> ids = catalog.summaries().stream().map(item -> item.id()).toList();
        assertEquals(ids.size(), new HashSet<>(ids).size());
        List<String> identities = catalog.summaries().stream()
                .map(item -> item.name() + "\u0000" + item.source().identity().code()).toList();
        assertEquals(identities.size(), new HashSet<>(identities).size());
        assertNotNull(catalog.findById("optional-feature:phb:agonizing-blast"));
        assertNotNull(catalog.findById("optional-feature:xphb:agonizing-blast"));
    }

    @Test
    void allSourceOccurrencesAndReprintReferencesAreRegisteredAndPreserved() throws Exception {
        OptionalFeaturesFileDte source = sourceCorpus();
        for (OptionalFeatureDte feature : source.optionalfeature()) {
            assertNotNull(sourceRegistry.find(feature.source()), feature.source());
            if (feature.otherSources() != null) {
                feature.otherSources().forEach(reference ->
                        assertNotNull(sourceRegistry.find(reference.source()), reference.source()));
            }
            if (feature.reprintedAs() != null) {
                feature.reprintedAs().forEach(reference -> {
                    String[] parts = reference.uid().split("\\|", -1);
                    assertEquals(2, parts.length);
                    assertNotNull(sourceRegistry.find(parts[1]), parts[1]);
                });
            }
        }
        assertTrue(catalog.summaries().stream().anyMatch(item -> !item.featureTypes().isEmpty()));
        assertTrue(service.byName("Agonizing Blast", "PHB").getFirst().reprintedAs().stream()
                .anyMatch(reference -> reference.uid().equals("Agonizing Blast|XPHB")));
        OptionalFeatureDte secondarySourceFeature = source.optionalfeature().stream()
                .filter(feature -> feature.otherSources() != null && !feature.otherSources().isEmpty())
                .findFirst().orElseThrow();
        var normalized = catalog.findByName(secondarySourceFeature.name()).stream()
                .filter(feature -> feature.source().identity().code().equals(secondarySourceFeature.source()))
                .findFirst().orElseThrow();
        assertEquals(secondarySourceFeature.otherSources().getFirst().source(),
                normalized.otherSources().getFirst().identity().code());
    }

    @Test
    void allFeatureTypeCodesAndMultipleMembershipsArePreserved() {
        Set<String> actual = new HashSet<>();
        catalog.summaries().forEach(item -> actual.addAll(item.featureTypes()));
        assertEquals(EXPECTED_FEATURE_TYPES, actual);
        assertEquals(9, catalog.summaries().stream().filter(item -> item.featureTypes().size() > 1).count());
        assertTrue(catalog.summaries().stream().anyMatch(item -> item.featureTypes().size() > 1));
        assertEquals(7, service.find(null, null, "fs:r").size());
        assertTrue(service.find(null, null, "eI").size() > 0);
    }

    @Test
    void structuredPrerequisitesKeepAlternativeGroupsAndClassLevelMeaning() throws Exception {
        OptionalFeaturesFileDte source = sourceCorpus();
        assertTrue(source.optionalfeature().stream().anyMatch(feature ->
                feature.prerequisite() != null && !feature.prerequisite().isEmpty()));
        assertTrue(source.optionalfeature().stream()
                .filter(feature -> feature.prerequisite() != null)
                .allMatch(feature -> feature.prerequisite().size() == 1));
        assertTrue(source.optionalfeature().stream().anyMatch(feature ->
                feature.prerequisite() != null && feature.prerequisite().stream()
                        .anyMatch(group -> group.level() != null && group.spell() != null)));
        OptionalFeatureDte classLevel = source.optionalfeature().stream()
                .filter(feature -> feature.prerequisite() != null)
                .filter(feature -> feature.prerequisite().stream().anyMatch(group -> group.level() != null
                        && group.level().classReference() != null))
                .findFirst().orElseThrow();
        var normalized = catalog.findByName(classLevel.name()).stream()
                .filter(feature -> feature.source().identity().code().equals(classLevel.source()))
                .findFirst().orElseThrow();
        assertTrue(normalized.prerequisites().stream().anyMatch(group -> group.level() != null
                && group.level().characterClass() != null
                && group.level().level() != null));
    }

    @Test
    void structuredSpellResourcesProficienciesSensesAndProgressionsAreMapped() throws Exception {
        OptionalFeaturesFileDte source = sourceCorpus();
        assertTrue(source.optionalfeature().stream().anyMatch(feature -> feature.additionalSpells() != null
                && feature.additionalSpells().stream().anyMatch(spell -> spell.known() != null)));
        assertTrue(source.optionalfeature().stream().anyMatch(feature -> feature.additionalSpells() != null
                && feature.additionalSpells().stream().anyMatch(spell -> spell.innate() != null
                && !spell.innate().daily().isEmpty())));
        assertTrue(source.optionalfeature().stream().anyMatch(feature -> feature.consumes() != null
                && feature.consumes().amount() != null));
        assertTrue(source.optionalfeature().stream().anyMatch(feature -> feature.skillProficiencies() != null));
        assertTrue(source.optionalfeature().stream().anyMatch(feature -> feature.senses() != null));
        assertTrue(source.optionalfeature().stream().anyMatch(feature -> feature.featProgression() != null));
        assertTrue(source.optionalfeature().stream().anyMatch(feature -> feature.optionalfeatureProgression() != null));

        var spells = catalog.findByName("Agonizing Blast").stream()
                .filter(feature -> feature.source().identity().code().equals("XPHB"))
                .findFirst().orElseThrow();
        assertFalse(spells.prerequisites().getFirst().spells().isEmpty());
        var prof = catalog.findByName("Beguiling Influence").getFirst();
        assertEquals(new OptionalFeatureProficiencyGrantDto(true, true), prof.proficiencyGrants().getFirst());
    }

    @Test
    void everyObservedStructuredSourceFieldReachesTheNormalizedDefinition() throws Exception {
        OptionalFeaturesFileDte source = sourceCorpus();
        for (OptionalFeatureDte record : source.optionalfeature()) {
            var definition = catalog.findByName(record.name()).stream()
                    .filter(value -> value.source().identity().code().equals(record.source()))
                    .findFirst().orElseThrow();
            assertEquals(record.featureType(), definition.featureTypes());
            assertEquals(record.reprintedAs() == null ? 0 : record.reprintedAs().size(),
                    definition.reprintedAs().size());
            assertEquals(record.otherSources() == null ? 0 : record.otherSources().size(),
                    definition.otherSources().size());
            if (record.reprintedAs() != null) {
                for (int index = 0; index < record.reprintedAs().size(); index++) {
                    assertEquals(record.reprintedAs().get(index).uid(), definition.reprintedAs().get(index).uid());
                    assertEquals(record.reprintedAs().get(index).tag(), definition.reprintedAs().get(index).tag());
                }
            }
            if (record.otherSources() != null) {
                for (int index = 0; index < record.otherSources().size(); index++) {
                    assertEquals(record.otherSources().get(index).source(),
                            definition.otherSources().get(index).identity().code());
                    assertEquals(record.otherSources().get(index).page(), definition.otherSources().get(index).page());
                }
            }
            assertEquals(record.prerequisite() == null ? 0 : record.prerequisite().size(),
                    definition.prerequisites().size());
            assertEquals(record.additionalSpells() == null ? 0 : record.additionalSpells().size(),
                    definition.spellGrants().size());
            assertEquals(record.skillProficiencies() == null ? 0 : record.skillProficiencies().size(),
                    definition.proficiencyGrants().size());
            assertEquals(record.senses() == null ? 0 : record.senses().size(), definition.senseGrants().size());
            assertEquals(record.featProgression() == null ? 0 : record.featProgression().size(),
                    definition.featProgressions().size());
            assertEquals(record.optionalfeatureProgression() == null ? 0 : record.optionalfeatureProgression().size(),
                    definition.optionalFeatureProgressions().size());
            if (record.consumes() != null) {
                assertEquals(record.consumes().name(), definition.consumption().name());
                assertEquals(record.consumes().amount(), definition.consumption().amount());
                assertEquals(record.consumes().amountMin(), definition.consumption().amountMin());
                assertEquals(record.consumes().amountMax(), definition.consumption().amountMax());
            }
            if (record.prerequisite() != null) {
                for (int index = 0; index < record.prerequisite().size(); index++) {
                    var imported = record.prerequisite().get(index);
                    var normalized = definition.prerequisites().get(index);
                    assertEquals(imported.pact(), normalized.pact());
                    assertEquals(imported.item() == null ? List.of() : imported.item(), normalized.items());
                    assertEquals(imported.optionalfeature() == null ? List.of() : imported.optionalfeature(),
                            normalized.optionalFeatures());
                    assertEquals(imported.spell() == null ? 0 : imported.spell().size(), normalized.spells().size());
                    if (imported.level() != null) {
                        assertEquals(imported.level().level(), normalized.level().level());
                        assertEquals(imported.level().classReference() == null ? null
                                        : imported.level().classReference().name(),
                                normalized.level().characterClass() == null ? null
                                        : normalized.level().characterClass().name());
                    }
                }
            }
            if (record.skillProficiencies() != null) {
                for (int index = 0; index < record.skillProficiencies().size(); index++) {
                    assertEquals(record.skillProficiencies().get(index).deception(),
                            definition.proficiencyGrants().get(index).deception());
                    assertEquals(record.skillProficiencies().get(index).persuasion(),
                            definition.proficiencyGrants().get(index).persuasion());
                }
            }
            if (record.senses() != null) {
                for (int index = 0; index < record.senses().size(); index++) {
                    var imported = record.senses().get(index);
                    var normalized = definition.senseGrants().get(index);
                    assertEquals(imported.blindsight(), normalized.blindsight());
                    assertEquals(imported.darkvision(), normalized.darkvision());
                    assertEquals(imported.truesight(), normalized.truesight());
                }
            }
            if (record.featProgression() != null) {
                assertEquals(record.featProgression().getFirst().progression(),
                        definition.featProgressions().getFirst().progression());
            }
            if (record.optionalfeatureProgression() != null) {
                assertEquals(record.optionalfeatureProgression().getFirst().progression(),
                        definition.optionalFeatureProgressions().getFirst().progression());
            }
            if (record.additionalSpells() != null) {
                for (int index = 0; index < record.additionalSpells().size(); index++) {
                    var importedGrant = record.additionalSpells().get(index);
                    var normalizedGrant = definition.spellGrants().get(index);
                    assertEquals(importedGrant.ability(), normalizedGrant.ability());
                    assertEquals(importedGrant.resourceName(), normalizedGrant.resourceName());
                    assertEquals(importedGrant.known() == null ? 0 : importedGrant.known().values().size(),
                            normalizedGrant.known().size());
                    assertEquals(importedGrant.prepared() == null ? 0 : importedGrant.prepared().values().size(),
                            normalizedGrant.prepared().size());
                    if (importedGrant.known() != null) {
                        for (int choiceIndex = 0; choiceIndex < importedGrant.known().values().size(); choiceIndex++) {
                            var importedChoice = importedGrant.known().values().get(choiceIndex);
                            var normalizedChoice = normalizedGrant.known().get(choiceIndex);
                            assertEquals(importedChoice.choose(), normalizedChoice.choose());
                            assertEquals(importedChoice.count(), normalizedChoice.count());
                        }
                    }
                    if (importedGrant.innate() != null) {
                        assertEquals(importedGrant.innate().spells(), normalizedGrant.innateSpells());
                        assertEquals(importedGrant.innate().daily(), normalizedGrant.daily());
                        assertEquals(importedGrant.innate().resource(), normalizedGrant.resource());
                        assertEquals(importedGrant.innate().ritual().size(), normalizedGrant.ritual().size());
                    }
                }
            }
        }
    }

    @Test
    void noCopyVersionOrModificationDeclarationsRequireExpansion() throws Exception {
        String rawSource;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/optionalfeatures.json")) {
            assertNotNull(input);
            rawSource = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertFalse(rawSource.contains("\"_copy\""));
        assertFalse(rawSource.contains("\"_versions\""));
        assertFalse(rawSource.contains("\"_mod\""));
        assertEquals(221, sourceCorpus().optionalfeature().size());
        assertEquals(221, catalog.size());
    }

    @Test
    void rejectsOptionalFeatureDefinitionsWithUnregisteredPrimarySources() throws Exception {
        OptionalFeaturesFileDte corpus = sourceCorpus();
        List<OptionalFeatureDte> changed = new ArrayList<>(corpus.optionalfeature());
        OptionalFeatureDte first = changed.getFirst();
        changed.set(0, new OptionalFeatureDte(
                first.name(), "NOT_A_SOURCE", first.page(), first.featureType(), first.prerequisite(),
                first.otherSources(), first.reprintedAs(), first.entries(), first.consumes(),
                first.additionalSpells(), first.skillProficiencies(), first.senses(), first.featProgression(),
                first.optionalfeatureProgression(), first.isClassFeatureVariant(), first.srd(), first.srd52(),
                first.hasFluffImages()));
        assertThrows(IllegalStateException.class, () ->
                new OptionalFeatureCatalog(new OptionalFeaturesFileDte(changed), mapper, sourceRegistry));
    }

    @Test
    void packagedJarClasspathResourceCanBeReadAndDeserialized() throws Exception {
        Path jarPath = temporaryDirectory.resolve("optional-features-resource.jar");
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("data/optionalfeatures.json");
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            assertNotNull(resource);
            jar.putNextEntry(new JarEntry("data/optionalfeatures.json"));
            resource.transferTo(jar);
            jar.closeEntry();
        }
        URL[] urls = {jarPath.toUri().toURL()};
        try (URLClassLoader classLoader = new URLClassLoader(urls, null);
             InputStream input = new ClassPathResource("data/optionalfeatures.json", classLoader).getInputStream()) {
            OptionalFeaturesFileDte packaged = objectMapper.readerFor(OptionalFeaturesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
            assertEquals(221, packaged.optionalfeature().size());
        }
    }

    @Test
    void sortingFilteringAndLookupsAreStableAndCaseInsensitive() {
        var summaries = catalog.summaries();
        List<String> expectedOrder = summaries.stream()
                .sorted(Comparator.comparing(
                                (com.thedndpub.data.dto.optionalfeature.OptionalFeatureSummaryDto item) -> item.name(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(item -> item.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(item -> item.id()))
                .map(item -> item.id()).toList();
        assertEquals(expectedOrder, summaries.stream().map(item -> item.id()).toList());
        assertEquals(service.find("agonizing blast", "phb", null), service.find("AGONIZING BLAST", "PHB", null));
        assertEquals(1, service.byName("Agonizing Blast", "PHB").size());
        assertEquals(2, service.byName("Agonizing Blast", null).size());
        assertEquals("Agonizing Blast", service.byId("optional-feature:phb:agonizing-blast").name());
    }

    @Test
    void publicSerializationOmitsEmptyDataAndPreservesFalseAndZero() throws Exception {
        String json = objectMapper.writeValueAsString(new OptionalFeatureProficiencyGrantDto(false, false));
        assertTrue(json.contains("\"deception\":false"));
        String consumption = objectMapper.writeValueAsString(new OptionalFeatureConsumptionDto("Uses", 0, 0, 0));
        assertTrue(consumption.contains("\"amount\":0"));
        String emptySpellGrant = objectMapper.writeValueAsString(
                new com.thedndpub.data.dto.optionalfeature.OptionalFeatureSpellGrantDto(
                        null, List.of(), List.of(), List.of(), java.util.Map.of(), java.util.Map.of(),
                        List.of(), null));
        assertEquals("{}", emptySpellGrant);
        var detail = service.byId("optional-feature:phb:agonizing-blast");
        String detailJson = objectMapper.writeValueAsString(detail);
        assertFalse(detailJson.contains("entries"));
        assertFalse(detailJson.contains("{@spell"));
        assertFalse(detailJson.contains("hasFluffImages"));
        assertFalse(detailJson.contains("null"));
    }

    @Test
    void malformedKnownStructuresAndUnknownFieldsAreRejected() {
        String unknownRootField = "{\"optionalfeature\":[{\"name\":\"x\",\"source\":\"PHB\","
                + "\"page\":1,\"featureType\":[\"EI\"],\"entries\":[],\"mystery\":true}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(OptionalFeaturesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownRootField));
        String unknownInnateField = "{\"optionalfeature\":[{\"name\":\"x\",\"source\":\"PHB\","
                + "\"page\":1,\"featureType\":[\"EI\"],\"entries\":[],\"additionalSpells\":["
                + "{\"innate\":{\"_\":{\"mystery\":[]}}}]}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(OptionalFeaturesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownInnateField));
        String malformedUsage = "{\"optionalfeature\":[{\"name\":\"x\",\"source\":\"PHB\","
                + "\"page\":1,\"featureType\":[\"EI\"],\"entries\":[],\"additionalSpells\":["
                + "{\"innate\":{\"_\":{\"daily\":\"once\"}}}]}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(OptionalFeaturesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(malformedUsage));
    }

    @Test
    void restEndpointsExposeSummaryDetailsAndNameSourceMatches() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new OptionalFeaturesController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler()).build();
        mvc.perform(get("/api/optional-features").param("featureType", "EI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].featureTypes").isArray());
        mvc.perform(get("/api/optional-features/optional-feature:phb:agonizing-blast"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prerequisites").isArray());
        mvc.perform(get("/api/optional-features/by-name/Ambush").param("source", "TCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("TCE"));
        mvc.perform(get("/api/optional-features/unknown-id")).andExpect(status().isNotFound());
    }

    private OptionalFeaturesFileDte sourceCorpus() throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/optionalfeatures.json")) {
            assertNotNull(input);
            return objectMapper.readerFor(OptionalFeaturesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
    }
}
