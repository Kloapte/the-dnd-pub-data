package com.thedndpub.data.feat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.feat.FeatMapper;
import com.thedndpub.data.dte.feat.FeatsFileDte;
import com.thedndpub.data.services.FeatCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FeatStructuredPrerequisiteTests {
    private static final Map<String, Integer> EXPECTED_CATEGORIES = Map.of(
            "armor", 9,
            "weapon", 1,
            "weaponGroup", 2);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeatCatalog catalog;

    @Autowired
    private FeatMapper mapper;

    @Test
    void allTwelveAuthoredRootsPreserveExactStructuredCategoriesAndValues() throws Exception {
        JsonNode source = sourceJson();
        FeatsFileDte typed = objectMapper.treeToValue(source, FeatsFileDte.class);
        TreeMap<String, Integer> categoryCounts = new TreeMap<>();
        int affectedRoots = 0;
        int authoredRequirements = 0;

        for (int rootIndex = 0; rootIndex < source.path("feat").size(); rootIndex++) {
            JsonNode root = source.path("feat").get(rootIndex);
            var sourceFeat = typed.feat().get(rootIndex);
            var definition = catalog.findById(mapper.idFor(sourceFeat.name(), sourceFeat.source()).value());
            JsonNode prerequisite = root.path("prerequisite");
            int expectedGroups = 0;

            for (int groupIndex = 0; groupIndex < prerequisite.size(); groupIndex++) {
                JsonNode proficiency = prerequisite.get(groupIndex).get("proficiency");
                if (proficiency == null) {
                    continue;
                }

                expectedGroups++;
                affectedRoots++;
                int sourceGroupIndex = groupIndex;
                List<?> actualGroup = definition.structuredProficiencyPrerequisites().stream()
                        .filter(group -> group.prerequisiteAlternativeIndex() == sourceGroupIndex)
                        .toList();
                assertEquals(1, actualGroup.size(), sourceFeat.name() + " prerequisite group " + sourceGroupIndex);

                var normalizedGroup = definition.structuredProficiencyPrerequisites().stream()
                        .filter(group -> group.prerequisiteAlternativeIndex() == sourceGroupIndex)
                        .findFirst()
                        .orElseThrow();
                assertEquals(proficiency.size(), normalizedGroup.requirements().size());
                authoredRequirements += proficiency.size();

                for (int requirementIndex = 0; requirementIndex < proficiency.size(); requirementIndex++) {
                    JsonNode expected = proficiency.get(requirementIndex);
                    var actual = normalizedGroup.requirements().get(requirementIndex);
                    assertEquals(text(expected, "armor"), actual.armor());
                    assertEquals(text(expected, "weapon"), actual.weapon());
                    assertEquals(text(expected, "weaponGroup"), actual.weaponGroup());
                    expected.fieldNames().forEachRemaining(category -> categoryCounts.merge(category, 1, Integer::sum));
                }
            }

            if (expectedGroups == 0) {
                assertNull(definition.structuredProficiencyPrerequisites(), sourceFeat.name());
            } else {
                assertEquals(expectedGroups, definition.structuredProficiencyPrerequisites().size(), sourceFeat.name());
            }
        }

        assertEquals(305, source.path("feat").size());
        assertEquals(12, affectedRoots);
        assertEquals(12, authoredRequirements);
        assertEquals(EXPECTED_CATEGORIES, categoryCounts);
    }

    @Test
    void alternativeGroupPositionsPreservePrerequisiteAndRelationshipSemantics() {
        var purpleDragon = catalog.findById("feat:frhof:purple-dragon-commandant");
        assertEquals(1, purpleDragon.structuredProficiencyPrerequisites().getFirst()
                .prerequisiteAlternativeIndex());
        assertEquals("martial", purpleDragon.structuredProficiencyPrerequisites().getFirst()
                .requirements().getFirst().weaponGroup());
        assertEquals("purple dragon rook|frhof", purpleDragon.prerequisites().getFirst().feat().getFirst());

        var strike = catalog.findById("feat:bgg:strike-of-the-giants");
        assertEquals(0, strike.structuredProficiencyPrerequisites().getFirst()
                .prerequisiteAlternativeIndex());
        assertEquals("martial", strike.structuredProficiencyPrerequisites().getFirst()
                .requirements().getFirst().weaponGroup());
        assertEquals("Giant Foundling", strike.prerequisites().get(1).background().getFirst());
    }

    @Test
    void copyEffectiveVersionsInheritStructuredPrerequisitesWithoutChangingIdentityOrLineage() {
        var parent = catalog.findById("feat:bgg:strike-of-the-giants");
        List<?> versions = catalog.definitions().stream()
                .filter(definition -> definition.variant() && definition.parentId().equals(parent.id()))
                .toList();

        assertEquals(6, versions.size());
        for (var definition : catalog.definitions()) {
            assertFalse(definition.id().equals(definition.parentId()));
            if (definition.variant()) {
                var actualParent = catalog.findById(definition.parentId().value());
                assertEquals(actualParent.structuredProficiencyPrerequisites(),
                        definition.structuredProficiencyPrerequisites(), definition.id().value());
            }
        }
        assertEquals(314, catalog.size());
        assertEquals(314, new HashSet<>(catalog.definitions().stream()
                .map(definition -> definition.id().value()).toList()).size());
    }

    @Test
    void structuredPrerequisitesAreSeparateFromFeatGrantMechanics() {
        var crafter = catalog.findById("feat:xphb:crafter");
        assertNull(crafter.structuredProficiencyPrerequisites());
        assertFalse(crafter.mechanics().proficiencies().tools().isEmpty());

        var fightingInitiate = catalog.findById("feat:tce:fighting-initiate");
        assertEquals("martial", fightingInitiate.structuredProficiencyPrerequisites().getFirst()
                .requirements().getFirst().weapon());
        assertNull(fightingInitiate.mechanics().proficiencies());
        assertFalse(fightingInitiate.mechanics().optionalFeatureProgressions().isEmpty());
        assertNull(fightingInitiate.structuredProficiencyPrerequisites().getFirst()
                .requirements().getFirst().armor());
    }

    @Test
    void existingPublicPrerequisiteJsonDoesNotExposeTheInternalStructuredCompanion() throws Exception {
        var purpleDragon = catalog.findById("feat:frhof:purple-dragon-commandant");
        JsonNode publicJson = objectMapper.valueToTree(
                com.thedndpub.data.dto.feat.FeatDto.from(purpleDragon));

        assertFalse(publicJson.has("structuredProficiencyPrerequisites"));
        assertEquals("martial", publicJson.path("prerequisites").get(1).path("proficiency").get(0).textValue());
    }

    @Test
    void normalizedPrerequisiteCollectionsAreImmutable() {
        var definition = catalog.findById("feat:bgg:strike-of-the-giants");

        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> definition.structuredProficiencyPrerequisites().clear());
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> definition.structuredProficiencyPrerequisites().getFirst().requirements().clear());
    }

    private JsonNode sourceJson() throws IOException {
        try (var input = new ClassPathResource("data/feats.json").getInputStream()) {
            return objectMapper.readTree(input);
        }
    }

    private static String text(JsonNode value, String field) {
        JsonNode child = value.get(field);
        return child == null ? null : child.textValue();
    }
}
