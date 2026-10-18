package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.controllers.ClassFeaturesController;
import com.thedndpub.data.controllers.SubclassFeaturesController;
import com.thedndpub.data.dto.classes.ClassChoiceProjectionStatus;
import com.thedndpub.data.dto.classes.ClassChoiceReferenceDto;
import com.thedndpub.data.dto.classes.ClassExternalFeatureNode;
import com.thedndpub.data.dto.classes.ClassExternalKind;
import com.thedndpub.data.dto.classes.ClassExternalReference;
import com.thedndpub.data.dto.classes.ClassFeatureAbilityDeclaration;
import com.thedndpub.data.dto.classes.ClassFeatureChoice;
import com.thedndpub.data.dto.classes.ClassFeatureDefinition;
import com.thedndpub.data.dto.classes.ClassFeatureDto;
import com.thedndpub.data.dto.classes.ClassFeatureId;
import com.thedndpub.data.dto.classes.ClassFeatureMechanicsDto;
import com.thedndpub.data.dto.classes.ClassFeatureNode;
import com.thedndpub.data.dto.classes.ClassFeatureReferenceNode;
import com.thedndpub.data.dto.classes.ClassFeatureSection;
import com.thedndpub.data.dto.classes.ClassFeatureText;
import com.thedndpub.data.dto.classes.ClassId;
import com.thedndpub.data.dto.classes.ClassImportProvenance;
import com.thedndpub.data.dto.classes.ClassPublicMapper;
import com.thedndpub.data.dto.classes.ClassReferenceStatus;
import com.thedndpub.data.dto.classes.SubclassFeatureDefinition;
import com.thedndpub.data.dto.classes.SubclassFeatureDto;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.classes.ClassBlockEntryDte;
import com.thedndpub.data.dte.classes.ClassEntryDte;
import com.thedndpub.data.dte.classes.ClassFeatureDte;
import com.thedndpub.data.dte.classes.ClassOptionsEntryDte;
import com.thedndpub.data.dte.classes.ClassReferenceEntryDte;
import com.thedndpub.data.dte.classes.SubclassFeatureDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.services.ClassDomainSnapshot;
import com.thedndpub.data.services.ClassesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClassChoiceCompletenessTests {
    @Autowired private ClassDomainSnapshot snapshot;
    @Autowired private ClassesService service;
    @Autowired private ClassPublicMapper mapper;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CrossCatalogResolutionCoordinator references;

    @Test
    void reconcilesEveryAuthoredChoiceAndEveryEffectiveFeature() {
        Map<ClassChoiceProjectionStatus, Integer> statuses = new TreeMap<>();
        int authoredNodes = 0;
        for (ClassFeatureDefinition value : snapshot.result().classFeatures()) {
            ClassFeatureDto dto = service.classFeatureById(value.id().value());
            authoredNodes += reconcile(value.provenance(), value.content(), dto.mechanics(),
                    dto.choiceProjectionStatus());
            statuses.merge(dto.choiceProjectionStatus(), 1, Integer::sum);
        }
        assertEquals(12, authoredNodes);
        for (SubclassFeatureDefinition value : snapshot.result().subclassFeatures()) {
            SubclassFeatureDto dto = service.subclassFeatureById(value.id().value());
            authoredNodes += reconcile(value.provenance(), value.content(), dto.mechanics(),
                    dto.choiceProjectionStatus());
            statuses.merge(dto.choiceProjectionStatus(), 1, Integer::sum);
        }
        assertEquals(40, authoredNodes);
        assertEquals(Map.of(ClassChoiceProjectionStatus.NONE, 2_136,
                ClassChoiceProjectionStatus.COMPLETE, 36, ClassChoiceProjectionStatus.PARTIAL, 2,
                ClassChoiceProjectionStatus.WITHHELD, 2), statuses);
        int referencesInChoices = allFeatureDtos().stream().<JsonNode>map(objectMapper::valueToTree)
                .mapToInt(value -> countPublicOptions(value.path("mechanics").path("choices"))).sum();
        assertEquals(222, referencesInChoices);
    }

    @Test
    void withheldWizardChoicesAreVisibleAtBothDetailRoutesWithoutPlaceholderMechanics() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new SubclassFeaturesController(service)).build();
        for (String name : List.of("The Third Eye", "Master Transmuter")) {
            var feature = subclassFeature(name, "PHB");
            var dto = service.subclassFeatureById(feature.id().value());
            assertEquals(ClassChoiceProjectionStatus.WITHHELD, dto.choiceProjectionStatus());
            assertNull(dto.mechanics());
            mvc.perform(get("/api/subclass-features/{id}", feature.id().value()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.choiceProjectionStatus").value("WITHHELD"))
                    .andExpect(jsonPath("$.mechanics").doesNotExist())
                    .andExpect(jsonPath("$.content").doesNotExist());
            mvc.perform(get("/api/subclass-features/by-name/{name}", name).param("source", "PHB"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].choiceProjectionStatus").value("WITHHELD"));
        }
    }

    @Test
    void partialMetadataIsSignalledWithoutPublishingLabelsOrRequiredMarkers() throws Exception {
        for (String name : List.of("Rune Carver", "Elemental Disciplines")) {
            String source = name.equals("Rune Carver") ? "TCE" : "PHB";
            var feature = subclassFeature(name, source);
            var dto = service.subclassFeatureById(feature.id().value());
            assertEquals(ClassChoiceProjectionStatus.PARTIAL, dto.choiceProjectionStatus());
            String json = objectMapper.writeValueAsString(dto);
            assertFalse(json.contains("Level Required"));
            assertFalse(json.contains("Level or Higher"));
            assertFalse(json.contains("\"required\""));
            assertFalse(json.contains("isRequiredOption"));
            assertEquals(name.equals("Rune Carver") ? 6 : 17, dto.mechanics().choices().getFirst().options().size());
        }
    }

    @Test
    void completeReferenceChoicePreservesSelectionCountAndAlternatives() throws Exception {
        var feature = subclassFeature("Totem Spirit", "PHB");
        var mvc = MockMvcBuilders.standaloneSetup(new SubclassFeaturesController(service)).build();
        mvc.perform(get("/api/subclass-features/{id}", feature.id().value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choiceProjectionStatus").value("COMPLETE"))
                .andExpect(jsonPath("$.mechanics.choices[0].count").value(1))
                .andExpect(jsonPath("$.mechanics.choices[0].options.length()").value(5));
        var fightingStyle = snapshot.result().subclassFeatures().stream()
                .filter(value -> value.name().equals("Fighting Style")
                        && value.source().identity().code().equals("XGE")).findFirst().orElseThrow();
        var options = service.subclassFeatureById(fightingStyle.id().value()).mechanics().choices().getFirst().options();
        assertEquals(List.of("Dueling", "Two-Weapon Fighting"), options.stream().map(ClassChoiceReferenceDto::name).toList());
        assertTrue(options.stream().allMatch(option -> option.source().code().equals("PHB") && option.id() != null));
    }

    @Test
    void noChoiceStatusDoesNotTreatAbilityDeclarationsAsChoices() throws Exception {
        var feature = snapshot.result().classFeatures().stream()
                .filter(value -> value.name().equals("Spellcasting") && value.parentClassId().value().equals("class:phb:bard"))
                .findFirst().orElseThrow();
        var mvc = MockMvcBuilders.standaloneSetup(new ClassFeaturesController(service)).build();
        mvc.perform(get("/api/class-features/{id}", feature.id().value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choiceProjectionStatus").value("NONE"))
                .andExpect(jsonPath("$.content").doesNotExist());
        assertEquals(ClassChoiceProjectionStatus.NONE, project(List.of(new ClassFeatureAbilityDeclaration(
                "abilityDc", "Spell", List.of("int")))).choiceProjectionStatus());
    }

    @Test
    void aggregatesMultipleAndNestedChoicesWithoutOverstatingCompleteness() {
        ClassFeatureChoice complete = new ClassFeatureChoice(1, null, List.of(reference(null, null)));
        ClassFeatureChoice withheld = new ClassFeatureChoice(null, null, List.of(new ClassFeatureText("Withheld")));
        assertEquals(ClassChoiceProjectionStatus.PARTIAL, project(List.of(complete, withheld)).choiceProjectionStatus());
        assertEquals(ClassChoiceProjectionStatus.WITHHELD, project(List.of(withheld, withheld)).choiceProjectionStatus());
        var nested = project(List.of(new ClassFeatureChoice(2, null, List.of(complete, complete))));
        assertEquals(ClassChoiceProjectionStatus.COMPLETE, nested.choiceProjectionStatus());
        assertEquals(2, nested.mechanics().choices().getFirst().groups().size());
        var mixed = project(List.of(new ClassFeatureChoice(2, null, List.of(complete, withheld))));
        assertEquals(ClassChoiceProjectionStatus.PARTIAL, mixed.choiceProjectionStatus());
        assertEquals(1, mixed.mechanics().choices().getFirst().groups().size());
    }

    @Test
    void flattenedSectionGroupsArePartialButSurroundingSectionsDoNotChangeChoiceStatus() {
        var section = new ClassFeatureSection("entries", "Group", null, List.of(reference(null, null)), List.of());
        assertEquals(ClassChoiceProjectionStatus.PARTIAL,
                project(List.of(new ClassFeatureChoice(1, null, List.of(section)))).choiceProjectionStatus());
        var choice = new ClassFeatureChoice(1, null, List.of(reference(null, null)));
        var surrounding = new ClassFeatureSection("entries", "Feature context", null, List.of(choice), List.of());
        assertEquals(ClassChoiceProjectionStatus.COMPLETE, project(List.of(surrounding)).choiceProjectionStatus());
    }

    @Test
    void omittedLabelOrExplicitFalseRequiredMarkerPreventsCompleteStatus() {
        assertEquals(ClassChoiceProjectionStatus.PARTIAL,
                projectChoice(reference("Different authored label", null)).choiceProjectionStatus());
        assertEquals(ClassChoiceProjectionStatus.PARTIAL,
                projectChoice(reference(null, false)).choiceProjectionStatus());
        assertEquals(ClassChoiceProjectionStatus.COMPLETE,
                projectChoice(reference("Option", null)).choiceProjectionStatus());
        var importerSwitches = new ClassExternalFeatureNode(reference(null, null).reference(), null,
                null, false, true);
        assertEquals(ClassChoiceProjectionStatus.COMPLETE, projectChoice(importerSwitches).choiceProjectionStatus());
    }

    @Test
    void projectionCompletenessDoesNotPretendToResolveUnlinkedAlternatives() {
        var unlinked = new ClassExternalFeatureNode(new ClassExternalReference(ClassExternalKind.OPTIONAL_FEATURE,
                "Unlinked option", null, null, ClassReferenceStatus.UNQUALIFIED), null, null, null, null);
        var dto = projectChoice(unlinked);
        assertEquals(ClassChoiceProjectionStatus.COMPLETE, dto.choiceProjectionStatus());
        assertNull(dto.mechanics().choices().getFirst().options().getFirst().id());
        assertEquals("Unlinked option", dto.mechanics().choices().getFirst().options().getFirst().name());
    }

    @Test
    void oldJavaConstructorsAndNonEmptySerializationRemainCompatible() throws Exception {
        var oldClass = new ClassFeatureDto("sample", "Sample", new SourceReference("PHB", 0), "class:phb:sample",
                0, 0, false, null, null, List.of());
        var oldSubclass = new SubclassFeatureDto("sample", "Sample", new SourceReference("PHB", 0),
                "class:phb:sample", "subclass:phb:sample", 0, 0, false, null, null, List.of());
        for (Object old : List.of(oldClass, oldSubclass)) {
            JsonNode json = objectMapper.valueToTree(old);
            assertFalse(json.has("choiceProjectionStatus"));
            assertFalse(json.has("mechanics"));
            assertTrue(json.has("variant"));
            assertTrue(json.has("order"));
            assertTrue(json.path("source").has("page"));
            assertFalse(json.path("variant").booleanValue());
            assertEquals(0, json.path("order").intValue());
            assertEquals(0, json.path("source").path("page").intValue());
        }
        assertNull(objectMapper.readValue(objectMapper.writeValueAsString(oldClass), ClassFeatureDto.class)
                .choiceProjectionStatus());
        var withZeroCount = project(List.of(new ClassFeatureChoice(0, null, List.of(reference(null, null)))));
        JsonNode zeroJson = objectMapper.valueToTree(withZeroCount);
        assertTrue(zeroJson.path("mechanics").path("choices").get(0).has("count"));
        assertEquals(0, zeroJson.path("mechanics").path("choices").get(0).path("count").intValue());
        assertEquals(ClassChoiceProjectionStatus.COMPLETE,
                withZeroCount.withCrossCatalogReferences(List.of()).choiceProjectionStatus());
        var withheld = service.subclassFeatureById(subclassFeature("The Third Eye", "PHB").id().value());
        assertEquals(ClassChoiceProjectionStatus.WITHHELD,
                withheld.withCrossCatalogReferences(List.of()).choiceProjectionStatus());
        assertThrows(UnsupportedOperationException.class, () -> withZeroCount.mechanics().choices().clear());
    }

    @Test
    void allLegacyPublicFieldsNormalizedMechanicsAndResolverResultsKeepTheirBaselineFingerprints() throws Exception {
        List<Object> publicValues = new ArrayList<>();
        snapshot.result().classes().forEach(value -> publicValues.add(service.classById(value.id().value())));
        snapshot.result().subclasses().forEach(value -> publicValues.add(service.subclassById(value.id().value())));
        publicValues.addAll(allFeatureDtos());
        ArrayNode publicJson = objectMapper.valueToTree(publicValues);
        for (JsonNode value : publicJson) {
            ((ObjectNode) value).remove("choiceProjectionStatus");
        }
        assertEquals("e0a23d5e00be6e7596698f6580557461e497ddb48b403a3bc95aff0f7bccb0df", fingerprint(publicJson));
        assertEquals("fec61e02109b8d8dc53e42be0c13e12ebdf1fc5126745bcdab5c969bdc1df2e0",
                fingerprint(objectMapper.valueToTree(snapshot.result())));
        assertEquals("bdd0c7e036b87b211456b43a463a6781a0a7754aab638b0fbc6f5050a1f48036",
                fingerprint(objectMapper.valueToTree(references.snapshot().results())));
        String first = objectMapper.writeValueAsString(allFeatureDtos());
        assertEquals(first, objectMapper.writeValueAsString(allFeatureDtos()));
        assertFalse(first.contains("{@"));
    }

    private int reconcile(ClassImportProvenance provenance, List<ClassFeatureNode> content,
            ClassFeatureMechanicsDto publicMechanics, ClassChoiceProjectionStatus status) {
        var source = provenance.authored().data();
        List<ClassEntryDte> entries = switch (source) {
            case ClassFeatureDte value -> value.entries();
            case SubclassFeatureDte value -> value.entries();
            default -> throw new AssertionError("Unexpected feature kind");
        };
        List<ClassOptionsEntryDte> authored = new ArrayList<>();
        sourceChoices(entries, authored);
        List<ClassFeatureChoice> normalized = new ArrayList<>();
        normalizedChoices(content, normalized);
        assertEquals(authored.size(), normalized.size(), provenance.authored().location().pointer());
        if (authored.isEmpty()) {
            assertEquals(ClassChoiceProjectionStatus.NONE, status);
            return 0;
        }
        assertFalse(provenance.inheritedFields().contains("entries"));
        if (source.name().equals("The Third Eye") || source.name().equals("Master Transmuter")) {
            assertEquals(ClassChoiceProjectionStatus.WITHHELD, status);
            assertTrue(publicMechanics == null || publicMechanics.choices().isEmpty());
            return authored.size();
        }
        boolean partial = source.name().equals("Rune Carver") || source.name().equals("Elemental Disciplines");
        assertEquals(partial ? ClassChoiceProjectionStatus.PARTIAL : ClassChoiceProjectionStatus.COMPLETE, status);
        assertNotNull(publicMechanics);
        assertEquals(authored.size(), publicMechanics.choices().size());
        for (int index = 0; index < authored.size(); index++) {
            var original = authored.get(index);
            var internal = normalized.get(index);
            var projected = publicMechanics.choices().get(index);
            assertEquals(original.count(), projected.count());
            assertEquals(original.style(), projected.style());
            assertEquals(original.entries().size(), projected.options().size());
            assertTrue(projected.groups().isEmpty());
            for (int optionIndex = 0; optionIndex < original.entries().size(); optionIndex++) {
                assertInstanceOf(ClassReferenceEntryDte.class, original.entries().get(optionIndex));
                ClassChoiceReferenceDto expected = switch (internal.options().get(optionIndex)) {
                    case ClassFeatureReferenceNode reference -> new ClassChoiceReferenceDto(
                            reference.reference().kind().name(), reference.reference().identity().name(),
                            reference.reference().identity().source(), reference.reference().targetId());
                    case ClassExternalFeatureNode reference -> new ClassChoiceReferenceDto(
                            reference.reference().kind().name(), reference.reference().name(),
                            reference.reference().source(), reference.reference().targetId());
                    default -> throw new AssertionError("Unexpected reference alternative");
                };
                assertEquals(expected, projected.options().get(optionIndex));
                assertNotNull(expected.id());
            }
        }
        return authored.size();
    }

    private void sourceChoices(List<ClassEntryDte> entries, List<ClassOptionsEntryDte> choices) {
        if (entries == null) {
            return;
        }
        for (ClassEntryDte entry : entries) {
            if (entry instanceof ClassOptionsEntryDte choice) {
                choices.add(choice);
                sourceChoices(choice.entries(), choices);
            } else if (entry instanceof ClassBlockEntryDte block) {
                sourceChoices(block.entries(), choices);
                sourceChoices(block.items(), choices);
            }
        }
    }

    private void normalizedChoices(List<ClassFeatureNode> nodes, List<ClassFeatureChoice> choices) {
        for (ClassFeatureNode node : nodes) {
            if (node instanceof ClassFeatureChoice choice) {
                choices.add(choice);
                normalizedChoices(choice.options(), choices);
            } else if (node instanceof ClassFeatureSection section) {
                normalizedChoices(section.entries(), choices);
                normalizedChoices(section.items(), choices);
            }
        }
    }

    private int countPublicOptions(JsonNode choices) {
        int count = 0;
        for (JsonNode choice : choices) {
            count += choice.path("options").size() + countPublicOptions(choice.path("groups"));
        }
        return count;
    }

    private List<Object> allFeatureDtos() {
        List<Object> values = new ArrayList<>();
        snapshot.result().classFeatures().forEach(value -> values.add(service.classFeatureById(value.id().value())));
        snapshot.result().subclassFeatures().forEach(value -> values.add(service.subclassFeatureById(value.id().value())));
        return values;
    }

    private SubclassFeatureDefinition subclassFeature(String name, String source) {
        return snapshot.result().subclassFeatures().stream()
                .filter(value -> value.name().equals(name) && value.source().identity().code().equals(source))
                .findFirst().orElseThrow();
    }

    private ClassFeatureDto projectChoice(ClassFeatureNode alternative) {
        return project(List.of(new ClassFeatureChoice(1, null, List.of(alternative))));
    }

    private ClassFeatureDto project(List<ClassFeatureNode> content) {
        var value = new ClassFeatureDefinition(new ClassFeatureId("class-feature:phb:phb:sample:0:sample"),
                null, "Sample", new SourceReference("PHB", 0), new ClassId("class:phb:sample"),
                0, false, null, content, null, null);
        return mapper.detail(value, 0);
    }

    private ClassExternalFeatureNode reference(String displayName, Boolean required) {
        return new ClassExternalFeatureNode(new ClassExternalReference(ClassExternalKind.OPTIONAL_FEATURE,
                "Option", new SourceIdentity("PHB"), "optional-feature:phb:option", ClassReferenceStatus.RESOLVED),
                displayName, required, null, null);
    }

    private String fingerprint(JsonNode value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(objectMapper.writeValueAsBytes(canonical(value))));
    }

    private JsonNode canonical(JsonNode value) {
        if (value.isObject()) {
            ObjectNode result = objectMapper.createObjectNode();
            List<String> fields = new ArrayList<>();
            value.fieldNames().forEachRemaining(fields::add);
            fields.stream().sorted().filter(field -> !field.equals("provenance"))
                    .forEach(field -> result.set(field, canonical(value.get(field))));
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
