package com.thedndpub.data.feat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.controllers.FeatsController;
import com.thedndpub.data.dto.feat.FeatCombinedGrant;
import com.thedndpub.data.dto.feat.FeatDefenses;
import com.thedndpub.data.dto.feat.FeatMechanics;
import com.thedndpub.data.dto.feat.FeatMapper;
import com.thedndpub.data.dto.feat.FeatOptionalFeatureProgression;
import com.thedndpub.data.dto.feat.FeatProficiencies;
import com.thedndpub.data.dto.feat.FeatSenseValues;
import com.thedndpub.data.dto.race.RaceGrantChoiceDto;
import com.thedndpub.data.dto.race.RaceGrantDto;
import com.thedndpub.data.dto.race.RaceGrantGroupDto;
import com.thedndpub.data.dto.race.RaceResistanceDto;
import com.thedndpub.data.dte.feat.FeatDte;
import com.thedndpub.data.dte.feat.FeatsFileDte;
import com.thedndpub.data.dte.feat.FeatVersionExpander;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.FeatsService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FeatStructuredMechanicsTests {
    private static final Map<String, Integer> EXPECTED_FIELDS = Map.ofEntries(
            Map.entry("skillProficiencies", 12), Map.entry("expertise", 7),
            Map.entry("languageProficiencies", 7), Map.entry("toolProficiencies", 10),
            Map.entry("weaponProficiencies", 4), Map.entry("armorProficiencies", 6),
            Map.entry("savingThrowProficiencies", 2), Map.entry("skillToolLanguageProficiencies", 2),
            Map.entry("conditionImmune", 2), Map.entry("immune", 2), Map.entry("resist", 11),
            Map.entry("senses", 4), Map.entry("bonusSenses", 1), Map.entry("optionalfeatureProgression", 4));

    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeatCatalog catalog;

    @Autowired
    private FeatsService service;

    @Autowired
    private CrossCatalogResolutionCoordinator references;

    @Autowired
    private FeatMapper mapper;

    @Autowired
    private FeatVersionExpander expander;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Test
    void identitiesLegacyFieldsLineagePublicJsonAndResolverResultsRemainUnchanged() throws Exception {
        var definitions = catalog.definitions();
        assertEquals(314, definitions.size());
        var ids = definitions.stream().map(definition -> definition.id().value()).toList();
        ArrayNode existingFields = objectMapper.valueToTree(definitions);
        existingFields.forEach(value -> {
            ((ObjectNode) value).remove("mechanics");
            ((ObjectNode) value).remove("structuredProficiencyPrerequisites");
        });
        TreeMap<String, JsonNode> lineage = new TreeMap<>();
        for (String id : ids) {
            ObjectNode record = objectMapper.valueToTree(catalog.importById(id));
            record.remove("definition");
            lineage.put(id, record);
        }
        // Captured before changing production code; only the new internal mechanics field is excluded.
        assertFingerprint("55269cd5c7bc9490e7053a62bca5663afa5f5dd7a47e14ea67bce7877e86822d", ids);
        assertFingerprint("c3edb77d5756944bb38c4363a23ea8503369c7342a9ee0a813e1809cbee93b5a", existingFields);
        assertFingerprint("8fcec7ace48a0ff2f2fce0ff548eb3be59dcd071d62b72afbe49063d73145668", lineage);
        assertFingerprint("369181bf3f98aeb9dc8fe6019afbeab8b1f66892bace0854304c5f2955f35a88",
                ids.stream().map(service::findById).toList());
        assertFingerprint("99c34b1e6dd1a5082df1c39782bd9ca8a513961af526920db6574f2a0e54a955", catalog.summaries());
        assertFingerprint("bdd0c7e036b87b211456b43a463a6781a0a7754aab638b0fbc6f5050a1f48036",
                references.snapshot().results());
        assertEquals(6_246, references.snapshot().results().size());
        assertEquals(Map.of(ResolutionOutcome.RESOLVED, 2_517L, ResolutionOutcome.AMBIGUOUS, 2_054L,
                ResolutionOutcome.UNRESOLVED, 181L, ResolutionOutcome.DEFERRED, 1_494L),
                references.snapshot().countsByOutcome());
    }

    @Test
    void everyAuthoredFieldRoundTripsFromNormalizedValuesAcrossAllFiftyNineRoots() throws Exception {
        JsonNode roots = sourceJson().get("feat");
        FeatsFileDte typed = objectMapper.readerFor(FeatsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(sourceJson());
        Map<String, Integer> counts = new TreeMap<>();
        int affected = 0;
        for (int index = 0; index < roots.size(); index++) {
            JsonNode source = roots.get(index);
            FeatDte dte = typed.feat().get(index);
            var definition = catalog.findById(mapper.idFor(dte.name(), dte.source()).value());
            ObjectNode expected = sourceFields(source);
            assertEquals(expected, authoredFields(definition.mechanics()), "$.feat[" + index + "]");
            if (!expected.isEmpty()) {
                affected++;
                expected.fieldNames().forEachRemaining(field -> counts.merge(field, 1, Integer::sum));
            }
        }
        assertEquals(305, roots.size());
        assertEquals(59, affected);
        assertEquals(EXPECTED_FIELDS, counts);
        assertEquals(74, counts.values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void allEffectiveDefinitionsAndNineVersionsRetainTheirExactMechanicsAndLineage() throws Exception {
        JsonNode roots = sourceJson().get("feat");
        Map<String, JsonNode> sourceFields = new TreeMap<>();
        for (JsonNode root : roots) {
            sourceFields.put(mapper.idFor(root.get("name").textValue(), root.get("source").textValue()).value(),
                    sourceFields(root));
        }
        Map<String, Integer> counts = new TreeMap<>();
        int affected = 0;
        int versions = 0;
        for (var definition : catalog.definitions()) {
            String sourceId = definition.variant() ? definition.parentId().value() : definition.id().value();
            JsonNode expected = sourceFields.get(sourceId);
            assertEquals(expected, authoredFields(definition.mechanics()), definition.id().value());
            if (!expected.isEmpty()) {
                affected++;
                expected.fieldNames().forEachRemaining(field -> counts.merge(field, 1, Integer::sum));
            }
            if (definition.variant()) {
                versions++;
                assertNull(definition.mechanics());
                assertEquals(catalog.findById(sourceId).mechanics(), definition.mechanics());
                assertEquals(definition.parentId(), catalog.importById(definition.id().value())
                        .expansionProvenance().parentId());
            }
        }
        assertEquals(314, catalog.size());
        assertEquals(9, versions);
        assertEquals(59, affected);
        assertEquals(EXPECTED_FIELDS, counts);
    }

    @Test
    void namedGrantsSelectorsExpertiseAndCombinedChoicesRemainDistinct() {
        var anatomy = mechanics("feat:rhw:aberrant-anatomy");
        assertEquals(Boolean.TRUE, anatomy.proficiencies().skills().getFirst().grants().getFirst().granted());
        assertEquals("perception", anatomy.proficiencies().expertise().getFirst().grants().getFirst().name());
        var expertise = mechanics("feat:tce:skill-expert").proficiencies().expertise().getFirst().grants().getFirst();
        assertEquals("anyProficientSkill", expertise.name());
        assertEquals(1, expertise.count());
        assertNull(expertise.granted());
        var keenMind = mechanics("feat:xphb:keen-mind").proficiencies().skills().getFirst().grants().getFirst();
        assertEquals("choose", keenMind.name());
        assertNull(keenMind.granted());
        assertNull(keenMind.choice().count());
        assertEquals(List.of("arcana", "history", "investigation", "nature", "religion"), keenMind.choice().from());
        var crafter = mechanics("feat:xphb:crafter").proficiencies().tools().getFirst().grants().getFirst();
        assertEquals(3, crafter.choice().count());
        var weaponMaster = mechanics("feat:phb:weapon-master").proficiencies().weapons()
                .getFirst().grants().getFirst().choice();
        assertEquals(4, weaponMaster.count());
        assertTrue(weaponMaster.from().isEmpty());
        assertEquals("type=martial weapon;mundane weapon|miscellaneous=mundane", weaponMaster.fromFilter());

        var languages = mechanics("feat:frhof:cult-of-the-dragon-initiate").proficiencies().languages();
        assertEquals(2, languages.size());
        assertEquals("draconic", languages.get(0).grants().getFirst().name());
        assertEquals("any", languages.get(1).grants().getFirst().name());
        var combined = mechanics("feat:phb:skilled").proficiencies().combined();
        assertEquals(1, combined.size());
        assertEquals(List.of("anySkill", "anyTool"), combined.getFirst().choose().getFirst().from());
        assertEquals(3, combined.getFirst().choose().getFirst().count());
    }

    @Test
    void resistanceImmunityAndOrdinaryVersusBonusSensesKeepTheirSourceRoles() {
        var poison = mechanics("feat:frhof:boon-of-poison-mastery").defenses();
        assertEquals(List.of("poisoned"), poison.conditionImmunities());
        assertEquals(List.of("poison"), poison.damageImmunities());
        assertNull(poison.resistances());
        var choice = mechanics("feat:xphb:boon-of-energy-resistance").defenses().resistances().getFirst();
        assertNull(choice.type());
        assertEquals(2, choice.choose().count());
        assertEquals(9, choice.choose().from().size());
        var fixed = mechanics("feat:xge:infernal-constitution").defenses().resistances();
        assertEquals(List.of("cold", "poison"), fixed.stream().map(RaceResistanceDto::type).toList());
        assertTrue(fixed.stream().allMatch(value -> value.choose() == null));
        var ordinary = mechanics("feat:xphb:boon-of-truesight");
        assertEquals(Map.of("truesight", 60), ordinary.senses().getFirst().ranges());
        assertNull(ordinary.bonusSenses());
        var bonus = mechanics("feat:bgg:keenness-of-the-stone-giant");
        assertEquals(Map.of("darkvision", 60), bonus.bonusSenses().getFirst().ranges());
        assertNull(bonus.senses());
    }

    @Test
    void allFourProgressionsPreserveWildcardKeysFeatureTypesAndOrderedGroups() throws Exception {
        Map<String, String> types = Map.of("feat:tce:eldritch-adept", "EI", "feat:tce:fighting-initiate", "FS:F",
                "feat:phb:martial-adept", "MV:B", "feat:tce:metamagic-adept", "MM");
        for (var entry : types.entrySet()) {
            var progression = mechanics(entry.getKey()).optionalFeatureProgressions().getFirst();
            assertEquals(List.of(entry.getValue()), progression.featureTypes());
            assertEquals(List.of("*"), new ArrayList<>(progression.progression().keySet()));
            assertEquals(entry.getValue().equals("EI") || entry.getValue().equals("FS:F") ? 1 : 2,
                    progression.progression().get("*"));
        }
        ObjectNode fixture = compositeFixture();
        ArrayNode progressions = objectMapper.createArrayNode();
        progressions.add(sourceById("feat:tce:metamagic-adept").get("optionalfeatureProgression").get(0));
        progressions.add(sourceById("feat:tce:eldritch-adept").get("optionalfeatureProgression").get(0));
        fixture.set("optionalfeatureProgression", progressions);
        var normalized = mapper.map(parse(fixture)).mechanics().optionalFeatureProgressions();
        assertEquals(List.of("Metamagic", "Eldritch Invocations"), normalized.stream()
                .map(FeatOptionalFeatureProgression::name).toList());
    }

    @Test
    void supportedEmptyNullZeroAndFalseValuesSurviveWithoutChoosingAnOption() throws Exception {
        FeatDte source = parse(objectMapper.readTree("""
                {"name":"Optional Fixture","source":"PHB",
                 "skillProficiencies":[{"perception":false,"any":0,"choose":{"count":0,"from":[]}}],
                 "expertise":[],"languageProficiencies":null,
                 "skillToolLanguageProficiencies":[{"choose":[]}],
                 "conditionImmune":[],"immune":[],"resist":[{"choose":{"from":[],"count":0}}],
                 "senses":[{"blindsight":0}],"bonusSenses":[],
                 "optionalfeatureProgression":[{"featureType":[],"progression":{"*":0}}]}
                """));
        var mechanics = mapper.map(source).mechanics();
        var skills = mechanics.proficiencies().skills().getFirst().grants();
        var fixed = skills.stream().filter(value -> value.name().equals("perception")).findFirst().orElseThrow();
        var count = skills.stream().filter(value -> value.name().equals("any")).findFirst().orElseThrow();
        var choice = skills.stream().filter(value -> value.name().equals("choose")).findFirst().orElseThrow();
        assertEquals(Boolean.FALSE, fixed.granted());
        assertEquals(0, count.count());
        assertEquals(0, choice.choice().count());
        assertTrue(choice.choice().from().isEmpty());
        assertTrue(mechanics.proficiencies().expertise().isEmpty());
        assertNull(mechanics.proficiencies().languages());
        assertTrue(mechanics.proficiencies().combined().getFirst().choose().isEmpty());
        assertTrue(mechanics.defenses().conditionImmunities().isEmpty());
        assertTrue(mechanics.defenses().damageImmunities().isEmpty());
        assertNull(mechanics.defenses().resistances().getFirst().type());
        assertEquals(0, mechanics.defenses().resistances().getFirst().choose().count());
        assertEquals(0, mechanics.senses().getFirst().ranges().get("blindsight"));
        assertTrue(mechanics.bonusSenses().isEmpty());
        assertNull(mechanics.optionalFeatureProgressions().getFirst().name());
        assertTrue(mechanics.optionalFeatureProgressions().getFirst().featureTypes().isEmpty());
        assertEquals(0, mechanics.optionalFeatureProgressions().getFirst().progression().get("*"));
        assertTrue(objectMapper.valueToTree(fixed).has("granted"));
        assertTrue(objectMapper.valueToTree(count).has("count"));
        assertNull(mapper.map(parse(objectMapper.readTree("{\"name\":\"Absent\",\"source\":\"PHB\"}"))).mechanics());
    }

    @Test
    void versionExpansionInheritsAllFourteenFieldsWithoutChangingParentOrChoosingBenefits() throws Exception {
        ObjectNode fixture = compositeFixture();
        fixture.set("_versions", objectMapper.readTree("[{\"name\":\"Composite Version\",\"source\":\"PHB\"}]"));
        var expanded = expander.expand(parse(fixture));
        var parent = mapper.map(expanded.get(0));
        var version = mapper.map(expanded.get(1));
        assertEquals(14, authoredFields(parent.mechanics()).size());
        assertEquals(parent.mechanics(), version.mechanics());
        assertEquals(parent.id(), version.parentId());
        assertTrue(version.variant());
        assertFalse(parent.variant());
        assertEquals(0, expanded.get(1).versionIndex());
    }

    @Test
    void allNormalizedCollectionsAreImmutableAndDetachedFromSourceCollections() throws Exception {
        FeatDte source = parse(compositeFixture());
        FeatMechanics mechanics = mapper.map(source).mechanics();
        JsonNode before = authoredFields(mechanics);
        source.skillProficiencies().clear();
        source.skillToolLanguageProficiencies().getFirst().choose().getFirst().from().clear();
        source.conditionImmune().clear();
        source.immune().clear();
        source.resist().clear();
        source.senses().clear();
        source.bonusSenses().clear();
        source.optionalfeatureProgression().getFirst().featureType().clear();
        source.optionalfeatureProgression().getFirst().progression().put("*", 999);
        assertEquals(before, authoredFields(mechanics));

        var proficiencies = mechanics.proficiencies();
        for (var groups : List.of(proficiencies.skills(), proficiencies.expertise(), proficiencies.languages(),
                proficiencies.tools(), proficiencies.weapons(), proficiencies.armor(), proficiencies.savingThrows())) {
            assertImmutable(groups);
            for (var group : groups) {
                assertImmutable(group.grants());
                for (var grant : group.grants()) {
                    if (grant.choice() != null) {
                        assertImmutable(grant.choice().from());
                        assertImmutable(grant.choice().categories());
                    }
                }
            }
        }
        assertImmutable(proficiencies.combined());
        assertImmutable(proficiencies.combined().getFirst().choose());
        assertImmutable(proficiencies.combined().getFirst().choose().getFirst().from());
        assertImmutable(mechanics.defenses().conditionImmunities());
        assertImmutable(mechanics.defenses().damageImmunities());
        assertImmutable(mechanics.defenses().resistances());
        assertImmutable(mechanics.defenses().resistances().getFirst().choose().from());
        assertImmutable(mechanics.senses());
        assertImmutable(mechanics.bonusSenses());
        assertThrows(UnsupportedOperationException.class, () -> mechanics.senses().getFirst().ranges().clear());
        assertImmutable(mechanics.optionalFeatureProgressions());
        assertImmutable(mechanics.optionalFeatureProgressions().getFirst().featureTypes());
        assertThrows(UnsupportedOperationException.class,
                () -> mechanics.optionalFeatureProgressions().getFirst().progression().clear());

        List<String> labels = new ArrayList<>(List.of("EI", "FS:F"));
        Map<String, Integer> progression = new LinkedHashMap<>(Map.of("*", 0));
        var value = new FeatOptionalFeatureProgression(null, labels, progression);
        var sense = new FeatSenseValues(progression);
        labels.clear();
        progression.clear();
        assertEquals(List.of("EI", "FS:F"), value.featureTypes());
        assertEquals(Map.of("*", 0), value.progression());
        assertEquals(Map.of("*", 0), sense.ranges());

        List<RaceGrantGroupDto> groups = new ArrayList<>(proficiencies.skills());
        List<FeatCombinedGrant> combined = new ArrayList<>(proficiencies.combined());
        var copied = new FeatProficiencies(groups, groups, groups, groups, groups, groups, groups, combined);
        groups.clear();
        combined.clear();
        assertEquals(1, copied.skills().size());
        assertEquals(1, copied.combined().size());
    }

    @Test
    void unsupportedSourceShapesAndUnknownFieldsRemainStrictlyRejected() throws Exception {
        for (String field : List.of("\"skillProficiencies\":[{\"perception\":\"true\"}]",
                "\"expertise\":[{\"anyProficientSkill\":1.5}]", "\"senses\":[{\"blindsight\":\"15\"}]",
                "\"resist\":[1]", "\"resist\":[{\"note\":\"unsupported\"}]",
                "\"toolProficiencies\":[{\"choose\":{\"from\":[],\"unknown\":true}}]",
                "\"skillToolLanguageProficiencies\":[{\"choose\":[{\"unknown\":true}]}]",
                "\"optionalfeatureProgression\":[{\"name\":\"Unknown\",\"unknown\":true}]",
                "\"unknownMechanic\":true", "\"_copy\":{\"name\":\"Unknown\",\"source\":\"PHB\"}")) {
            String json = "{\"name\":\"Invalid\",\"source\":\"PHB\"," + field + "}";
            assertThrows(JsonMappingException.class, () -> parse(objectMapper.readTree(json)), field);
        }
        FeatDte invalidGrant = parse(objectMapper.readTree(
                "{\"name\":\"Null Grant\",\"source\":\"PHB\",\"skillProficiencies\":[{\"perception\":null}]}"));
        assertThrows(IllegalArgumentException.class, () -> mapper.map(invalidGrant));
    }

    @Test
    void existingFeatEndpointsRetainTheirPublicContractsAndExposeNoMechanics() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new FeatsController(service)).build();
        for (String id : List.of("feat:rhw:aberrant-anatomy", "feat:phb:skilled",
                "feat:phb:weapon-master", "feat:frhof:boon-of-poison-mastery", "feat:tce:eldritch-adept",
                "feat:xphb:magic-initiate-cleric", "feat:bgg:strike-of-the-giants-fire")) {
            JsonNode result = objectMapper.readTree(mvc.perform(get("/api/feats/" + id)).andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString());
            assertEquals(objectMapper.valueToTree(service.findById(id)), result);
            assertFalse(result.has("mechanics"));
            assertFalse(result.has("entries"));
            assertFalse(result.toString().contains("{@"));
        }
        JsonNode summaries = objectMapper.readTree(mvc.perform(get("/api/feats")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        assertEquals(objectMapper.valueToTree(catalog.summaries()), summaries);
        JsonNode byName = objectMapper.readTree(mvc.perform(get("/api/feats/by-name/Skilled").param("source", "PHB"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, byName.size());
        assertFalse(byName.get(0).has("mechanics"));
    }

    @Test
    void isolatedJarResourcesNormalizeAllThreeHundredFourteenDefinitionsIncludingVersions() throws Exception {
        Path jarPath = temporaryDirectory.resolve("feat-mechanics-resources.jar");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/feats.json"));
            try (InputStream input = new ClassPathResource("data/feats.json").getInputStream()) {
                input.transferTo(jar);
            }
            jar.closeEntry();
        }
        try (URLClassLoader loader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null);
                InputStream input = new ClassPathResource("data/feats.json", loader).getInputStream()) {
            FeatsFileDte source = objectMapper.readerFor(FeatsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
            var packaged = new FeatCatalog(source, mapper, sourceRegistry, expander);
            assertEquals(314, packaged.size());
            assertEquals(9, packaged.variantCount());
            assertEquals(catalog.definitions(), packaged.definitions());
        }
    }

    private FeatMechanics mechanics(String id) {
        return catalog.findById(id).mechanics();
    }

    private JsonNode sourceJson() throws Exception {
        try (InputStream input = new ClassPathResource("data/feats.json").getInputStream()) {
            return objectMapper.readTree(input);
        }
    }

    private JsonNode sourceById(String id) throws Exception {
        for (JsonNode root : sourceJson().get("feat")) {
            if (mapper.idFor(root.get("name").textValue(), root.get("source").textValue()).value().equals(id)) {
                return root;
            }
        }
        throw new AssertionError("Missing source Feat " + id);
    }

    private ObjectNode compositeFixture() throws Exception {
        ObjectNode fixture = objectMapper.createObjectNode().put("name", "Composite Fixture").put("source", "PHB");
        for (JsonNode root : sourceJson().get("feat")) {
            for (String field : EXPECTED_FIELDS.keySet()) {
                if (!fixture.has(field) && root.has(field)) {
                    fixture.set(field, root.get(field).deepCopy());
                }
            }
        }
        return fixture;
    }

    private FeatDte parse(JsonNode value) throws Exception {
        return objectMapper.readerFor(FeatDte.class).with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(value);
    }

    private ObjectNode sourceFields(JsonNode source) {
        ObjectNode fields = objectMapper.createObjectNode();
        EXPECTED_FIELDS.keySet().forEach(field -> {
            if (source.has(field)) {
                fields.set(field, source.get(field));
            }
        });
        return fields;
    }

    /** Reconstructs only the 14 authored shapes to compare every corpus field independently of the mapper. */
    private ObjectNode authoredFields(FeatMechanics mechanics) {
        ObjectNode result = objectMapper.createObjectNode();
        if (mechanics == null) {
            return result;
        }
        FeatProficiencies proficiencies = mechanics.proficiencies();
        if (proficiencies != null) {
            put(result, "skillProficiencies", grants(proficiencies.skills()));
            put(result, "expertise", grants(proficiencies.expertise()));
            put(result, "languageProficiencies", grants(proficiencies.languages()));
            put(result, "toolProficiencies", grants(proficiencies.tools()));
            put(result, "weaponProficiencies", grants(proficiencies.weapons()));
            put(result, "armorProficiencies", grants(proficiencies.armor()));
            put(result, "savingThrowProficiencies", grants(proficiencies.savingThrows()));
            if (proficiencies.combined() != null) {
                ArrayNode combined = objectMapper.createArrayNode();
                for (var group : proficiencies.combined()) {
                    ObjectNode value = objectMapper.createObjectNode();
                    if (group.choose() != null) {
                        ArrayNode choices = objectMapper.createArrayNode();
                        group.choose().forEach(choice -> choices.add(choice(choice)));
                        value.set("choose", choices);
                    }
                    combined.add(value);
                }
                result.set("skillToolLanguageProficiencies", combined);
            }
        }
        if (mechanics.defenses() != null) {
            FeatDefenses defenses = mechanics.defenses();
            put(result, "conditionImmune", defenses.conditionImmunities());
            put(result, "immune", defenses.damageImmunities());
            if (defenses.resistances() != null) {
                ArrayNode resistances = objectMapper.createArrayNode();
                for (RaceResistanceDto resistance : defenses.resistances()) {
                    if (resistance.type() != null) {
                        resistances.add(resistance.type());
                    } else {
                        resistances.add(objectMapper.createObjectNode().set("choose", choice(resistance.choose())));
                    }
                }
                result.set("resist", resistances);
            }
        }
        put(result, "senses", ranges(mechanics.senses()));
        put(result, "bonusSenses", ranges(mechanics.bonusSenses()));
        if (mechanics.optionalFeatureProgressions() != null) {
            ArrayNode progressions = objectMapper.createArrayNode();
            for (var progression : mechanics.optionalFeatureProgressions()) {
                ObjectNode value = objectMapper.createObjectNode();
                put(value, "name", progression.name());
                put(value, "featureType", progression.featureTypes());
                put(value, "progression", progression.progression());
                progressions.add(value);
            }
            result.set("optionalfeatureProgression", progressions);
        }
        return result;
    }

    private ArrayNode ranges(List<FeatSenseValues> senses) {
        if (senses == null) {
            return null;
        }
        ArrayNode result = objectMapper.createArrayNode();
        senses.forEach(value -> result.add(objectMapper.valueToTree(value.ranges())));
        return result;
    }

    private ArrayNode grants(List<RaceGrantGroupDto> groups) {
        if (groups == null) {
            return null;
        }
        ArrayNode result = objectMapper.createArrayNode();
        for (var group : groups) {
            ObjectNode object = objectMapper.createObjectNode();
            for (RaceGrantDto grant : group.grants()) {
                if (grant.granted() != null) {
                    object.put(grant.name(), grant.granted());
                } else if (grant.count() != null) {
                    object.put(grant.name(), grant.count());
                } else {
                    object.set(grant.name(), choice(grant.choice()));
                }
            }
            result.add(object);
        }
        return result;
    }

    private ObjectNode choice(RaceGrantChoiceDto choice) {
        ObjectNode result = objectMapper.createObjectNode();
        put(result, "count", choice.count());
        if (!choice.from().isEmpty()) {
            put(result, "from", choice.from());
        }
        put(result, "fromFilter", choice.fromFilter());
        return result;
    }

    private void put(ObjectNode owner, String field, Object value) {
        if (value != null) {
            owner.set(field, objectMapper.valueToTree(value));
        }
    }

    private static void assertImmutable(List<?> values) {
        assertThrows(UnsupportedOperationException.class, values::clear);
    }

    private void assertFingerprint(String expected, Object value) throws Exception {
        assertEquals(expected, fingerprint(objectMapper.valueToTree(value)));
    }

    private String fingerprint(JsonNode value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(objectMapper.writeValueAsBytes(canonical(value))));
    }

    private JsonNode canonical(JsonNode value) {
        if (value.isObject()) {
            ObjectNode result = objectMapper.createObjectNode();
            TreeSet<String> names = new TreeSet<>();
            value.fieldNames().forEachRemaining(names::add);
            for (String name : names) {
                result.set(name, canonical(value.get(name)));
            }
            return result;
        }
        if (value.isArray()) {
            ArrayNode result = objectMapper.createArrayNode();
            value.forEach(child -> result.add(canonical(child)));
            return result;
        }
        return value;
    }
}
