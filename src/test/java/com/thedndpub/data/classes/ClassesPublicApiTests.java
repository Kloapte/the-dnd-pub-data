package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.classes.*;
import com.thedndpub.data.controllers.ClassesController;
import com.thedndpub.data.controllers.SubclassesController;
import com.thedndpub.data.controllers.ClassFeaturesController;
import com.thedndpub.data.controllers.SubclassFeaturesController;
import com.thedndpub.data.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClassesPublicApiTests {
    @Autowired private ClassCatalog classes;
    @Autowired private SubclassCatalog subclasses;
    @Autowired private ClassFeatureCatalog classFeatures;
    @Autowired private SubclassFeatureCatalog subclassFeatures;
    @Autowired private ClassDomainIndex index;
    @Autowired private ClassDomainSnapshot snapshot;
    @Autowired private ClassesService service;
    @Autowired private ClassPublicMapper mapper;
    @Autowired private ObjectMapper objectMapper;
    private MockMvc mvc;

    @BeforeEach
    void configureRoutes() {
        mvc = MockMvcBuilders.standaloneSetup(new ClassesController(service), new SubclassesController(service),
                new ClassFeaturesController(service), new SubclassFeaturesController(service)).build();
    }

    @Test
    void catalogsExposeAllDefinitionsWithStableIdsAndImmutableCollections() {
        assertEquals(30, classes.size());
        assertEquals(330, subclasses.size());
        assertEquals(677, classFeatures.size());
        assertEquals(1499, subclassFeatures.size());
        assertEquals(2536, classes.findAll().size() + subclasses.findAll().size()
                + classFeatures.findAll().size() + subclassFeatures.findAll().size());
        assertThrows(UnsupportedOperationException.class, () -> classes.findAll().clear());
        assertThrows(UnsupportedOperationException.class, () -> subclasses.findAll().clear());
        assertEquals(30, classes.findAll().stream().map(value -> value.id().value()).distinct().count());
        assertEquals(1499, subclassFeatures.findAll().stream().map(value -> value.id().value()).distinct().count());
        assertEquals(2, classes.findByName("WIZARD").size());
        assertFalse(subclassFeatures.findBySource("xphb").isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> classes.findByName("wizard").clear());
        assertEquals(1598, index.orderedFeatureCount());
        assertEquals(2725, snapshot.result().featureGraph().size());
    }

    @Test
    void everyFeatureReferenceAndParentRelationshipResolvesToThePublicDetailId() {
        for (ClassDefinition owner : classes.findAll()) {
            for (ClassFeatureUnlock unlock : owner.features()) {
                assertNotNull(classFeatures.findById(unlock.feature().targetId()));
            }
            for (SubclassId child : owner.subclasses()) {
                assertEquals(owner.id(), subclasses.findById(child.value()).parentClassId());
            }
        }
        for (SubclassDefinition owner : subclasses.findAll()) {
            assertNotNull(classes.findById(owner.parentClassId().value()));
            for (ClassFeatureUnlock unlock : owner.features()) {
                assertNotNull(subclassFeatures.findById(unlock.feature().targetId()));
            }
        }
        for (ClassFeatureGraphEdge edge : snapshot.result().featureGraph()) {
            assertTrue(classFeatures.findById(edge.target().targetId()) != null
                    || subclassFeatures.findById(edge.target().targetId()) != null);
        }
    }

    @Test
    void queriesAreDeterministicCaseInsensitiveAndKeepNameAmbiguity() {
        assertTrue(service.findClasses("fIgHtEr", "phb").stream().anyMatch(value -> value.name().equals("Fighter")));
        assertEquals(2, service.classesByName("wizard", null).size());
        assertEquals(1, service.classesByName("WIZARD", "xphb").size());
        assertEquals(classes.findAll().stream().map(value -> value.id().value()).toList(),
                service.findClasses(null, null).stream().map(ClassSummaryDto::id).toList());
        assertFalse(service.findClassFeatures(null, null, "class:phb:fighter", 1).isEmpty());
        assertTrue(service.findSubclassFeatures(null, null, null, "missing", null).isEmpty());
        assertTrue(service.subclassesOf("class:xphb:cleric").stream()
                .allMatch(value -> value.parentClassId().equals("class:xphb:cleric")));
        String championId = service.subclassesByName("Champion", null).getFirst().id();
        assertTrue(service.featuresOfSubclass(championId).stream()
                .allMatch(value -> value.parentSubclassId().equals(championId)));
    }

    @Test
    void projectionsKeepStructuredMechanicsAndExcludeProseAndImportInstructions() throws Exception {
        ClassDto fighter = service.classById("class:phb:fighter");
        assertFalse(fighter.features().isEmpty());
        assertTrue(fighter.tables().stream().flatMap(table -> table.rows().stream())
                .flatMap(row -> row.cells().stream()).noneMatch(cell -> cell.kind() == ClassTableCellKind.PRESENTATION_TEXT));
        assertTrue(classes.findAll().stream().map(mapper::detail).anyMatch(value -> value.spellcasting() != null));
        assertTrue(classes.findAll().stream().map(mapper::detail).anyMatch(value -> value.startingOptions() != null));
        assertTrue(classFeatures.findAll().stream().map(value -> mapper.detail(value, index.order(value.id().value())))
                .anyMatch(value -> value.mechanics() != null && !value.mechanics().optionalFeatureIds().isEmpty()));
        assertTrue(subclassFeatures.findAll().stream().map(value -> mapper.detail(value, index.order(value.id().value())))
                .anyMatch(value -> value.mechanics() != null && !value.mechanics().referencedFeatureIds().isEmpty()));

        String json = objectMapper.writeValueAsString(List.of(fighter,
                service.classFeatureById(classFeatures.findAll().getFirst().id().value()),
                service.subclassFeatureById(subclassFeatures.findAll().getFirst().id().value())));
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("\"content\""));
        assertFalse(json.contains("provenance"));
        assertFalse(json.contains("_copy"));
        assertFalse(json.contains("publicationFlags"));

        List<Object> publicCorpus = new java.util.ArrayList<>();
        classes.findAll().forEach(value -> publicCorpus.add(mapper.detail(value)));
        subclasses.findAll().forEach(value -> publicCorpus.add(mapper.detail(value)));
        classFeatures.findAll().forEach(value -> publicCorpus.add(mapper.detail(value, index.order(value.id().value()))));
        subclassFeatures.findAll().forEach(value -> publicCorpus.add(mapper.detail(value, index.order(value.id().value()))));
        String completeJson = objectMapper.writeValueAsString(publicCorpus);
        assertNoEmptyJsonValues(objectMapper.readTree(completeJson), "$publicDefinitions");
        for (Object definition : publicCorpus) {
            assertFalse(objectMapper.writeValueAsString(definition).contains("{@"), definition.toString());
        }
        assertFalse(completeJson.contains("\"provenance\""));
        assertFalse(completeJson.contains("\"publicationFlags\""));
        assertFalse(completeJson.contains("\"content\""));
    }

    @Test
    void publicSpellAndEquipmentReferencesRetainDeferredIdentities() {
        List<ClassSpellcasting> spellcasting = new java.util.ArrayList<>();
        classes.findAll().stream().map(mapper::detail).map(ClassDto::spellcasting)
                .filter(java.util.Objects::nonNull).forEach(spellcasting::add);
        subclasses.findAll().stream().map(mapper::detail).map(SubclassDto::spellcasting)
                .filter(java.util.Objects::nonNull).forEach(spellcasting::add);
        long direct = spellcasting.stream().flatMap(value -> value.additionalSpells().stream())
                .flatMap(group -> group.grants().stream()).flatMap(grant -> grant.selections().stream())
                .filter(selection -> selection.spell() != null).count();
        long choiceCandidates = spellcasting.stream().flatMap(value -> value.additionalSpells().stream())
                .flatMap(group -> group.grants().stream()).flatMap(grant -> grant.selections().stream())
                .mapToLong(selection -> selection.from().size()).sum();
        assertEquals(1694, direct);
        assertEquals(3, choiceCandidates);
        assertTrue(spellcasting.stream().flatMap(value -> value.additionalSpells().stream())
                .flatMap(group -> group.grants().stream()).flatMap(grant -> grant.selections().stream())
                .anyMatch(selection -> selection.spell() != null && selection.spell().source() == null));

        long deferredItemGroups = classes.findAll().stream().map(mapper::detail).map(ClassDto::startingOptions)
                .filter(java.util.Objects::nonNull).flatMap(value -> value.equipment().stream())
                .flatMap(group -> group.branches().stream()).flatMap(branch -> branch.entries().stream())
                .filter(option -> option.kind() == ClassEquipmentKind.ITEM_GROUP).count();
        assertEquals(3, deferredItemGroups);
    }

    @Test
    void exposesAllRoutesAndNotFoundBehavior() throws Exception {
        mvc.perform(get("/api/classes").param("name", "wizard").param("source", "PHB"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("class:phb:wizard"));
        mvc.perform(get("/api/classes/class:phb:fighter/subclasses"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].parentClassId").value("class:phb:fighter"));
        mvc.perform(get("/api/classes/class:phb:fighter/features")).andExpect(status().isOk());
        mvc.perform(get("/api/subclasses").param("classId", "class:xphb:cleric")).andExpect(status().isOk());
        mvc.perform(get("/api/subclasses/by-name/Champion")).andExpect(status().isOk());
        mvc.perform(get("/api/subclass-features").param("level", "3")).andExpect(status().isOk());
        mvc.perform(get("/api/class-features/by-name/{name}", classFeatures.findAll().getFirst().name()))
                .andExpect(status().isOk());
        mvc.perform(get("/api/classes/class:absent:none")).andExpect(status().isNotFound());
        mvc.perform(get("/api/subclasses/subclass:absent:none/features")).andExpect(status().isNotFound());
        mvc.perform(get("/api/classes/by-name/NoSuchClass")).andExpect(status().isNotFound());
    }

    @Test
    void nonEmptySerializationPreservesFalseAndZero() throws Exception {
        var sample = new ClassFeatureDto("class-feature:test:phb:sample:0:zero", "Zero",
                new com.thedndpub.data.dto.source.SourceReference("PHB", 0), "class:phb:sample", 0, 0,
                false, new ClassFeatureMechanicsDto(List.of(), List.of(), List.of(), List.of(),
                new ClassFeatureConsumption("resource", 0, 0, 0)), null, List.of());
        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsBytes(sample));
        assertEquals(0, json.path("source").path("page").intValue());
        assertEquals(0, json.path("level").intValue());
        assertEquals(0, json.path("order").intValue());
        assertFalse(json.path("variant").booleanValue());
        assertEquals(0, json.path("mechanics").path("consumption").path("amount").intValue());
        assertFalse(json.has("sources"));
    }

    private static void assertNoEmptyJsonValues(JsonNode node, String path) {
        if (node.isObject()) {
            assertFalse(node.isEmpty(), path);
            node.fields().forEachRemaining(entry ->
                    assertNoEmptyJsonValues(entry.getValue(), path + "." + entry.getKey()));
        } else if (node.isArray()) {
            assertFalse(node.isEmpty(), path);
            for (int index = 0; index < node.size(); index++) {
                assertNoEmptyJsonValues(node.get(index), path + "[" + index + "]");
            }
        } else {
            assertFalse(node.isNull(), path);
            assertFalse(node.isTextual() && node.textValue().isEmpty(), path);
        }
    }
}
