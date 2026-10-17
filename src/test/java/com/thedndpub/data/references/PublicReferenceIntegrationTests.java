package com.thedndpub.data.references;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.background.BackgroundDto;
import com.thedndpub.data.dto.classes.ClassDto;
import com.thedndpub.data.dto.classes.ClassFeatureDto;
import com.thedndpub.data.dto.classes.SubclassDto;
import com.thedndpub.data.dto.classes.SubclassFeatureDto;
import com.thedndpub.data.dto.feat.FeatDto;
import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureDto;
import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.controllers.BackgroundsController;
import com.thedndpub.data.services.BackgroundsService;
import com.thedndpub.data.services.ClassesService;
import com.thedndpub.data.services.FeatsService;
import com.thedndpub.data.services.ItemsService;
import com.thedndpub.data.services.OptionalFeaturesService;
import com.thedndpub.data.services.RacesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PublicReferenceIntegrationTests {
    @Autowired
    private CrossCatalogResolutionCoordinator coordinator;

    @Autowired
    private PublicReferenceProjection projection;

    @Autowired
    private ClassesService classes;

    @Autowired
    private BackgroundsService backgrounds;

    @Autowired
    private RacesService races;

    @Autowired
    private FeatsService feats;

    @Autowired
    private OptionalFeaturesService optionalFeatures;

    @Autowired
    private ItemsService items;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void exposesSnapshotReferencesOnExistingDetailDtoTypes() {
        ResolutionSnapshot snapshot = coordinator.snapshot();

        ResolutionResult classResult = resolvedForOwner(snapshot, "class");
        ClassDto classDto = classes.classById(classResult.reference().owner().stableId());
        assertProjected(classDto.crossCatalogReferences(), classResult);

        ResolutionResult subclassResult = resolvedForOwner(snapshot, "subclass");
        SubclassDto subclassDto = classes.subclassById(subclassResult.reference().owner().stableId());
        assertProjected(subclassDto.crossCatalogReferences(), subclassResult);

        ResolutionResult classFeatureResult = resolvedForOwner(snapshot, "class-feature");
        ClassFeatureDto classFeatureDto = classes.classFeatureById(classFeatureResult.reference().owner().stableId());
        assertProjected(classFeatureDto.crossCatalogReferences(), classFeatureResult);

        ResolutionResult subclassFeatureResult = firstForOwner(snapshot, "subclass-feature");
        SubclassFeatureDto subclassFeatureDto = classes.subclassFeatureById(
                subclassFeatureResult.reference().owner().stableId());
        assertProjected(subclassFeatureDto.crossCatalogReferences(), subclassFeatureResult);

        ResolutionResult backgroundResult = resolvedForOwner(snapshot, "background");
        BackgroundDto backgroundDto = backgrounds.byId(backgroundResult.reference().owner().stableId());
        assertProjected(backgroundDto.crossCatalogReferences(), backgroundResult);

        ResolutionResult raceResult = firstForOwner(snapshot, "race");
        RaceDto raceDto = races.byId(raceResult.reference().owner().stableId());
        assertProjected(raceDto.crossCatalogReferences(), raceResult);

        ResolutionResult subraceResult = firstForOwner(snapshot, "subrace");
        RaceDto subraceDto = races.byId(subraceResult.reference().owner().stableId());
        assertProjected(subraceDto.crossCatalogReferences(), subraceResult);

        ResolutionResult featResult = resolvedForOwner(snapshot, "feat");
        FeatDto featDto = feats.findById(featResult.reference().owner().stableId());
        assertProjected(featDto.crossCatalogReferences(), featResult);

        ResolutionResult optionalFeatureResult = resolvedForOwner(snapshot, "optional-feature");
        OptionalFeatureDto optionalFeatureDto = optionalFeatures.byId(
                optionalFeatureResult.reference().owner().stableId());
        assertProjected(optionalFeatureDto.crossCatalogReferences(), optionalFeatureResult);

        ResolutionResult itemResult = resolvedForOwner(snapshot, "item");
        ItemDto itemDto = items.byId(itemResult.reference().owner().stableId());
        assertProjected(itemDto.crossCatalogReferences(), itemResult);
        assertTrue(itemDto.crossCatalogReferences().stream().anyMatch(value ->
                value.targetKind() == com.thedndpub.data.dto.references.PublicReferenceTargetKind.BASE_ITEM
                        && value.targetId() != null));
    }

    @Test
    void projectsEveryOccurrenceWithoutChangingResolutionResults() {
        ResolutionSnapshot snapshot = coordinator.snapshot();
        assertSame(snapshot, coordinator.snapshot());
        List<ResolutionResult> historical = snapshot.results().stream()
                .filter(result -> !result.reference().owner().catalog().equals("cult")
                        && !result.reference().owner().catalog().equals("boon")
                        && !result.reference().owner().catalog().equals("object")
                        && !result.reference().owner().catalog().equals("facility")
                        && !result.reference().owner().catalog().equals("encounter")
                        && !result.reference().owner().catalog().equals("reward")
                        && !result.reference().owner().catalog().equals("trap")
                        && !result.reference().owner().catalog().equals("hazard")
                        && !result.reference().owner().catalog().equals("vehicle")
                        && !result.reference().owner().catalog().equals("vehicle-upgrade")
                        && !result.reference().owner().catalog().equals("variant-rule")
                        && !result.reference().owner().catalog().equals("deity")
                        && !result.reference().owner().catalog().equals("treasure-table"))
                .toList();
        assertEquals(3_875, historical.size());
        assertEquals(1_697L, historical.stream().filter(result -> result.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(1_108L, historical.stream().filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(30L, historical.stream().filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1_040L, historical.stream().filter(result -> result.outcome() == ResolutionOutcome.DEFERRED).count());
        assertEquals(361, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("variant-rule")).count());
        assertEquals(33, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("deity")).count());
        assertEquals(1_332, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("treasure-table")).count());
        assertEquals(2_371, snapshot.results().size() - historical.size());

        Map<ReferenceOwner, Long> occurrenceCounts = snapshot.results().stream()
                .collect(Collectors.groupingBy(result -> result.reference().owner(), Collectors.counting()));
        long projectedCount = occurrenceCounts.entrySet().stream()
                .mapToLong(entry -> projection.forOwner(entry.getKey().catalog(), entry.getKey().stableId()).size())
                .sum();
        assertEquals(snapshot.results().size(), projectedCount);

        for (ResolutionResult result : snapshot.results()) {
            var reference = projection.forOwner(result.reference().owner().catalog(),
                    result.reference().owner().stableId()).stream()
                    .filter(value -> value.fieldLocator().equals(result.reference().fieldLocator()))
                    .filter(value -> value.authoredToken().equals(result.reference().originalToken()))
                    .findFirst()
                    .orElseThrow();
            if (result.outcome() == ResolutionOutcome.RESOLVED) {
                assertEquals(result.resolvedTargetId(), reference.targetId());
                assertEquals(result.resolvedDefinitionKind().name(), reference.targetKind().name());
                assertNotNull(reference.authoredSource());
            } else {
                assertNull(reference.targetId());
            }
        }
        assertEquals(List.of("BASE_ITEM", "FEAT", "ITEM", "OPTIONAL_FEATURE", "SKILL", "SPELL"),
                snapshot.results().stream()
                        .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED)
                        .map(result -> result.resolvedDefinitionKind().name())
                        .distinct()
                        .sorted()
                        .toList());
        assertTrue(snapshot.results().stream().anyMatch(result ->
                result.reference().targetKind() == ReferenceTargetKind.SKILL));
        assertTrue(snapshot.results().stream().anyMatch(result ->
                result.reference().targetKind() == ReferenceTargetKind.LANGUAGE));
    }

    @Test
    void serializationExposesAuthoredIdentityAndRoleButNoInternalDiagnosticsOrCandidates() throws Exception {
        ResolutionSnapshot snapshot = coordinator.snapshot();
        ResolutionResult ambiguous = snapshot.results().stream()
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS)
                .findFirst()
                .orElseThrow();
        var projected = projection.forOwner(ambiguous.reference().owner().catalog(),
                ambiguous.reference().owner().stableId()).stream()
                .filter(value -> value.fieldLocator().equals(ambiguous.reference().fieldLocator()))
                .filter(value -> value.authoredToken().equals(ambiguous.reference().originalToken()))
                .findFirst()
                .orElseThrow();

        assertNull(projected.targetId());
        assertNotNull(projected.role());
        String json = objectMapper.writeValueAsString(projected);
        assertFalse(json.contains("candidate"));
        assertFalse(json.contains("diagnostic"));
        assertFalse(json.contains("severity"));
        assertFalse(json.contains("reason"));
        assertFalse(json.contains("outcome"));
        assertFalse(json.contains("{@"));
        assertThrows(UnsupportedOperationException.class,
                () -> projection.forOwner(ambiguous.reference().owner().catalog(),
                        ambiguous.reference().owner().stableId()).clear());
    }

    @Test
    void uniqueUnqualifiedSpellAndMissingEquipmentKeepNoTargetId() {
        ResolutionSnapshot snapshot = coordinator.snapshot();
        List<ResolutionResult> uniqueUnqualifiedSpells = snapshot.results().stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SPELL)
                .filter(result -> result.reference().parsedSource() == null)
                .filter(result -> "branding smite".equalsIgnoreCase(result.reference().parsedName()))
                .toList();
        assertEquals(2, uniqueUnqualifiedSpells.size());
        for (ResolutionResult result : uniqueUnqualifiedSpells) {
            var projected = projectedReference(result);
            assertNull(projected.targetId());
            assertNull(projected.authoredSource());
            assertEquals("branding smite", projected.authoredName());
        }

        ResolutionResult missingItem = snapshot.results().stream()
                .filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED)
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.ITEM)
                .findFirst()
                .orElseThrow();
        assertNull(projectedReference(missingItem).targetId());
    }

    @Test
    void distinguishesChoicePrerequisiteFilterAndGrantRoles() {
        ResolutionSnapshot snapshot = coordinator.snapshot();
        assertTrue(snapshot.results().stream()
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.CHOICE)
                .map(result -> projectedRole(result))
                .allMatch(role -> role == com.thedndpub.data.dto.references.PublicReferenceRole.CHOICE));
        assertTrue(snapshot.results().stream()
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.FILTER)
                .map(this::projectedRole)
                .allMatch(role -> role == com.thedndpub.data.dto.references.PublicReferenceRole.FILTER));
        assertTrue(snapshot.results().stream()
                .filter(result -> result.reference().fieldLocator().contains("prerequisites"))
                .map(this::projectedRole)
                .allMatch(role -> role == com.thedndpub.data.dto.references.PublicReferenceRole.PREREQUISITE));
        assertTrue(snapshot.results().stream()
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED)
                .map(this::projectedRole)
                .anyMatch(role -> role == com.thedndpub.data.dto.references.PublicReferenceRole.GRANT));
        assertTrue(snapshot.results().stream()
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.GROUP)
                .map(this::projectedRole)
                .allMatch(role -> role == com.thedndpub.data.dto.references.PublicReferenceRole.GROUP));
        assertTrue(snapshot.results().stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.ITEM
                        && result.reference().fieldLocator().contains("startingEquipment"))
                .map(this::projectedRole)
                .allMatch(role -> role == com.thedndpub.data.dto.references.PublicReferenceRole.CHOICE));
    }

    @Test
    void existingBackgroundDetailEndpointReturnsTheAdditiveProjection() throws Exception {
        ResolutionResult result = resolvedForOwner(coordinator.snapshot(), "background");
        String ownerId = result.reference().owner().stableId();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new BackgroundsController(backgrounds)).build();

        mockMvc.perform(get("/api/backgrounds/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerId))
                .andExpect(jsonPath("$.crossCatalogReferences[0].fieldLocator").exists())
                .andExpect(jsonPath("$.crossCatalogReferences[0].targetKind").exists());
    }

    private com.thedndpub.data.dto.references.PublicReferenceRole projectedRole(ResolutionResult result) {
        return projection.forOwner(result.reference().owner().catalog(), result.reference().owner().stableId())
                .stream()
                .filter(value -> value.fieldLocator().equals(result.reference().fieldLocator()))
                .filter(value -> value.authoredToken().equals(result.reference().originalToken()))
                .map(value -> value.role())
                .findFirst()
                .orElseThrow();
    }

    private com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto projectedReference(
            ResolutionResult result) {
        return projection.forOwner(result.reference().owner().catalog(), result.reference().owner().stableId())
                .stream()
                .filter(value -> value.fieldLocator().equals(result.reference().fieldLocator()))
                .filter(value -> value.authoredToken().equals(result.reference().originalToken()))
                .findFirst()
                .orElseThrow();
    }

    private static ResolutionResult resolvedForOwner(ResolutionSnapshot snapshot, String ownerCatalog) {
        return snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals(ownerCatalog))
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No resolved reference for owner " + ownerCatalog));
    }

    private static ResolutionResult firstForOwner(ResolutionSnapshot snapshot, String ownerCatalog) {
        return snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals(ownerCatalog))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No reference for owner " + ownerCatalog));
    }

    private static void assertProjected(
            List<com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto> values,
            ResolutionResult result) {
        var match = values.stream()
                .filter(value -> value.fieldLocator().equals(result.reference().fieldLocator()))
                .filter(value -> value.authoredToken().equals(result.reference().originalToken()))
                .findFirst()
                .orElseThrow();
        assertEquals(result.resolvedTargetId(), match.targetId());
        assertEquals(result.reference().originalSource(), match.authoredSource());
        assertEquals(result.reference().parsedName(), match.authoredName());
    }
}
