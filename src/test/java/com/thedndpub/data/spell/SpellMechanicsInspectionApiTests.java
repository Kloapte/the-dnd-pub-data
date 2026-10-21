package com.thedndpub.data.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.controllers.SpellsController;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.spell.api.SpellDto;
import com.thedndpub.data.dto.spell.api.SpellPublicMapper;
import com.thedndpub.data.mechanics.inspection.FormulaObservationInspectionDto;
import com.thedndpub.data.mechanics.inspection.MechanicsInspectionAvailability;
import com.thedndpub.data.mechanics.inspection.TextEvidenceLocatorDto;
import com.thedndpub.data.mechanics.model.CatalogDomain;
import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionChoiceMechanic;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.spell.SpellMechanicsDocumentFactory;
import com.thedndpub.data.mechanics.spell.SpellMechanicsSourceOwner;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import com.thedndpub.data.services.SpellPublicationCatalog;
import com.thedndpub.data.services.SpellsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SpellMechanicsInspectionApiTests {
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private SpellFormulaAnnotationCatalog formulas;
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SourceRegistry sources;
    @Autowired
    private SpellsService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private SpellPublicationCatalog publication;

    private MockMvc mvc;

    @BeforeEach
    void configureRoutes() {
        mvc = MockMvcBuilders.standaloneSetup(new SpellsController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler()).build();
    }

    @Test
    void explicitConditionMechanicsAreAvailableThroughPrivateInspectionWithExactEvidence() {
        var conditionClaims = inspection.documents().stream()
                .flatMap(document -> document.interpretations().stream())
                .filter(claim -> claim.payload() instanceof ConditionChangeMechanic)
                .toList();
        assertFalse(conditionClaims.isEmpty());
        assertEquals(conditionClaims.size(), conditionClaims.stream().map(claim -> claim.id().value()).distinct().count());
        assertTrue(conditionClaims.stream().anyMatch(claim ->
                ((ConditionChangeMechanic) claim.payload()).operation()
                        == com.thedndpub.data.mechanics.model.ConditionOperation.APPLY));
        assertTrue(conditionClaims.stream().anyMatch(claim ->
                ((ConditionChangeMechanic) claim.payload()).operation()
                        == com.thedndpub.data.mechanics.model.ConditionOperation.GRANT_IMMUNITY));
        assertTrue(conditionClaims.stream().flatMap(claim ->
                        ((ConditionChangeMechanic) claim.payload()).qualifiers().stream())
                .anyMatch(value -> value.toLowerCase(java.util.Locale.ROOT).contains("saving throw")));
        assertTrue(conditionClaims.stream().flatMap(claim ->
                        ((ConditionChangeMechanic) claim.payload()).qualifiers().stream())
                .anyMatch(value -> value.toLowerCase(java.util.Locale.ROOT).contains("until")
                        || value.toLowerCase(java.util.Locale.ROOT).contains("for the duration")));
        for (var claim : conditionClaims) {
            var condition = (ConditionChangeMechanic) claim.payload();
            assertNotNull(condition.operation());
            assertNotNull(condition.condition());
            if (!condition.qualifiers().isEmpty()) {
                assertTrue(claim.evidenceAssociations().stream().anyMatch(association ->
                        association.role() == com.thedndpub.data.mechanics.model.EvidenceRole.QUALIFIER));
            }
            assertTrue(claim.evidenceAssociations().stream().anyMatch(association ->
                    association.role() == com.thedndpub.data.mechanics.model.EvidenceRole.EFFECT_ROLE));
            assertEquals(com.thedndpub.data.mechanics.review.MechanicsReviewStatus.PENDING_REVIEW,
                    claim.review().recordedStatus());
            assertEquals(com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW,
                    claim.licensing().status());
        }
    }

    @Test
    void blindnessDeafnessChoiceAppearsInPrivateSpellInspection() throws Exception {
        String response = mvc.perform(get("/api/spells/spell:phb:blindness-deafness")
                        .param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode envelope = mapper.readTree(response).get("interpretedMechanics");
        JsonNode choice = null;
        for (JsonNode claim : envelope.get("interpretations")) {
            if (claim.at("/payload/kind").asText().equals("CONDITION_CHOICE")) {
                choice = claim;
                break;
            }
        }
        assertNotNull(choice);
        assertEquals(1, choice.at("/payload/choicesToSelect").intValue());
        assertEquals(List.of("BLINDED", "DEAFENED"), mapper.convertValue(choice.at("/payload/alternatives"),
                mapper.getTypeFactory().constructCollectionType(List.class, String.class)));
        assertEquals("PENDING_REVIEW", choice.at("/review/recordedStatus").asText());
        assertEquals("NEEDS_REVIEW", choice.at("/licensing/status").asText());
    }

    @Test
    void conditionDependenciesAppearOnlyInPrivateInspectionProjection() throws Exception {
        String privateResponse = mvc.perform(get("/api/spells/spell:phb:hold-person")
                        .param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode envelope = mapper.readTree(privateResponse).get("interpretedMechanics");
        assertTrue(envelope.get("interpretations").toString().contains("CONDITION_ON_SAVE_OUTCOME"));
        assertTrue(envelope.get("interpretations").toString().contains("CONDITION_ENDS_ON_SAVE_SUCCESS"));
        assertTrue(envelope.get("interpretations").toString().contains("REQUIRES_CONCENTRATION"));
        assertTrue(envelope.get("interpretations").toString().contains("MULTIPLE_TARGET_CREATURES"));

        JsonNode defaultResponse = mapper.readTree(mvc.perform(get("/api/spells/spell:phb:hold-person"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertFalse(defaultResponse.has("interpretedMechanics"));
    }

    @Test
    void registeredOwnersAreProcessedAndOtherOwnersRemainUnattemptedWithAllObservations() throws Exception {
        assertEquals(969, inspection.size());
        assertEquals(969, inspection.documents().size());
        int observations = 0;
        int ownersWithObservations = 0;
        int processedOwners = 0;
        for (var definition : spells.findAll()) {
            var result = inspection.findById(definition.id());
            boolean shield = definition.id().value().equals("spell:phb:shield")
                    || definition.id().value().equals("spell:xphb:shield");
            if (shield) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(5, result.interpretations().size());
                assertFalse(result.evidence().isEmpty());
                assertEquals(5, result.reviewSummary().total());
                assertEquals(2, result.reviewSummary().pendingReview());
                assertEquals(3, result.reviewSummary().needsMechanicsReview());
            } else if (definition.id().value().equals("spell:phb:fire-bolt")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(2, result.interpretations().size());
                assertEquals(1, result.unsupported().size());
                assertEquals("fire-bolt-object-ignition", result.unsupported().getFirst().id());
                assertEquals(1, result.unsupported().getFirst().evidenceIds().size());
                assertEquals(2, result.reviewSummary().pendingReview());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (java.util.Set.of("spell:phb:chill-touch", "spell:phb:shocking-grasp")
                    .contains(definition.id().value())) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.COMPLETE_WITHIN_SCOPE, result.coverage().status());
                assertEquals(4, result.interpretations().size());
                assertEquals(1, result.unsupported().size());
                assertEquals(4, result.reviewSummary().pendingReview());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:phb:fireball")
                    || definition.id().value().equals("spell:phb:healing-word")) {
                processedOwners++;
                int expected = definition.id().value().equals("spell:phb:fireball") ? 3 : 2;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(expected, result.interpretations().size());
                assertEquals(expected, result.reviewSummary().needsMechanicsReview());
                assertEquals(0, result.reviewSummary().pendingReview());
                assertEquals(1, result.unsupported().size());
            } else if (java.util.Set.of("spell:phb:burning-hands", "spell:phb:cone-of-cold",
                    "spell:phb:shatter", "spell:phb:lightning-bolt", "spell:phb:circle-of-death",
                    "spell:phb:blight", "spell:phb:chain-lightning", "spell:phb:cloudkill",
                    "spell:phb:fire-storm", "spell:phb:hellish-rebuke", "spell:phb:incendiary-cloud",
                    "spell:phb:insect-plague", "spell:phb:moonbeam", "spell:phb:flaming-sphere",
                    "spell:phb:wind-wall",
                    "spell:phb:cure-wounds", "spell:phb:mass-cure-wounds", "spell:phb:mass-healing-word",
                    "spell:phb:prayer-of-healing",
                    "spell:phb:shield-of-faith", "spell:phb:mage-armor", "spell:phb:barkskin",
                    "spell:phb:longstrider", "spell:phb:bless", "spell:phb:bane",
                    "spell:phb:protection-from-energy", "spell:phb:stoneskin",
                    "spell:phb:acid-splash", "spell:phb:arms-of-hadar", "spell:phb:call-lightning",
                    "spell:phb:calm-emotions", "spell:phb:disintegrate", "spell:phb:dissonant-whispers",
                    "spell:phb:ice-storm", "spell:phb:thunderwave",
                    "spell:phb:greater-restoration", "spell:phb:lesser-restoration",
                    "spell:phb:power-word-heal", "spell:phb:eldritch-blast",
                    "spell:phb:scorching-ray").contains(definition.id().value())) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertTrue(!result.interpretations().isEmpty()
                        || result.coverage().status() == CoverageStatus.NONE_SUPPORTED);
                assertEquals(result.interpretations().size(), result.reviewSummary().total());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.source().identity().code().equalsIgnoreCase("PHB")
                    && (!definition.mechanics().conditionsInflicted().isEmpty()
                    || !definition.mechanics().conditionImmunities().isEmpty())) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertTrue(result.coverage().status() == CoverageStatus.PARTIAL
                        || result.coverage().status() == CoverageStatus.NONE_SUPPORTED);
                assertTrue(!result.interpretations().isEmpty()
                        || result.coverage().status() == CoverageStatus.NONE_SUPPORTED);
                assertEquals(result.interpretations().size(), result.reviewSummary().total());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:phb:inflict-wounds")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(2, result.interpretations().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:phb:magic-missile")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(1, result.interpretations().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:phb:melf-s-acid-arrow")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(4, result.interpretations().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:phb:ray-of-frost")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.COMPLETE_WITHIN_SCOPE, result.coverage().status());
                assertEquals(3, result.interpretations().size());
                assertEquals(1, result.unsupported().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:xge:steel-wind-strike")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(2, result.interpretations().size());
                assertEquals(1, result.unsupported().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:phb:spiritual-weapon")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(3, result.interpretations().size());
                assertEquals(2, result.unsupported().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:xge:magic-stone")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(3, result.interpretations().size());
                assertEquals(1, result.unsupported().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:phb:witch-bolt")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(4, result.interpretations().size());
                assertEquals(1, result.unsupported().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:xge:ice-knife")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(5, result.interpretations().size());
                assertEquals(2, result.unsupported().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (definition.id().value().equals("spell:xphb:chromatic-orb")) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(3, result.interpretations().size());
                assertEquals(4, result.unsupported().size());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else if (java.util.Set.of("spell:au:iron-body", "spell:xphb:freedom-of-movement",
                    "spell:xphb:gaseous-form", "spell:xphb:heroes-feast", "spell:xphb:heroism",
                    "spell:xphb:feign-death", "spell:xphb:mind-blank",
                    "spell:tce:tasha-s-otherworldly-guise").contains(definition.id().value())) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(result.interpretations().isEmpty() ? CoverageStatus.NONE_SUPPORTED
                        : CoverageStatus.PARTIAL, result.coverage().status());
                assertEquals(result.interpretations().size(), result.reviewSummary().total());
                assertEquals(0, result.reviewSummary().verified());
                assertTrue(result.interpretations().stream().allMatch(value -> value.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));
            } else {
                assertEquals(ProcessingState.NOT_ATTEMPTED, result.processing().state());
                assertEquals(CoverageStatus.NOT_ASSESSED, result.coverage().status());
                assertTrue(result.interpretations().isEmpty());
                assertTrue(result.evidence().isEmpty());
                assertEquals(0, result.reviewSummary().total());
                assertNull(result.reviewSummary().aggregateStatus());
            }
            assertEquals(MechanicsInspectionAvailability.AVAILABLE, result.availability());
            assertEquals(result.unsupported().size(), result.coverage().unsupportedCount());
            assertEquals(0, result.reviewSummary().verified());
            assertTrue(result.diagnostics().isEmpty());
            observations += result.observations().size();
            if (!result.observations().isEmpty()) {
                ownersWithObservations++;
            }
            for (int ordinal = 0; ordinal < result.observations().size(); ordinal++) {
                var observation = (FormulaObservationInspectionDto) result.observations().get(ordinal);
                var authored = formulas.index().bySpellId().get(definition.id()).get(ordinal);
                assertEquals(definition.id().value(), observation.spellId());
                assertEquals(ordinal, observation.occurrenceIndex());
                assertEquals(definition.id().value() + ":formula:" + ordinal, observation.id());
                var locator = (TextEvidenceLocatorDto) observation.evidence();
                assertEquals(authored.observation().tag().evidence().location().jsonPointer(), locator.jsonPointer());
                assertEquals(authored.observation().tag().evidence().span(), locator.span());
                assertEquals(result.sourceRevisions().getFirst().id(), locator.sourceRevisionId());
            }
        }
        assertEquals(930, observations);
        assertEquals(129, processedOwners);
        assertEquals(840, inspection.size() - processedOwners);
        assertEquals(422, ownersWithObservations);
        assertEquals(547, inspection.size() - ownersWithObservations);
        assertTrue(publication.index().authorizedObservations().isEmpty());
        assertEquals(250, publication.index().candidates().size());
    }

    @Test
    void experimentalInterpretationIsSeparateForCanonicalAndPreviouslyUnprocessedOwners() throws Exception {
        JsonNode canonicalResponse = mapper.readTree(mvc.perform(
                        get("/api/spells/spell:phb:fireball").param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        JsonNode canonicalEnvelope = canonicalResponse.get("interpretedMechanics");
        assertEquals("CANONICAL", canonicalEnvelope.path("origin").asText());
        assertEquals("PROCESSED", canonicalEnvelope.at("/processing/state").asText());
        assertTrue(canonicalEnvelope.path("interpretations").size() > 0);
        assertEquals("EXPERIMENTAL", canonicalEnvelope.at("/experimentalInterpretation/origin").asText());
        assertFalse(canonicalEnvelope.at("/experimentalInterpretation/reviewed").asBoolean());
        assertFalse(canonicalEnvelope.at("/experimentalInterpretation/publicationAuthorized").asBoolean());
        assertTrue(canonicalEnvelope.at("/experimentalInterpretation/canonicalComparison/reproducedClaimIds")
                .size() > 0);

        JsonNode candidateResponse = mapper.readTree(mvc.perform(
                        get("/api/spells/spell:phb:poison-spray").param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        JsonNode candidateEnvelope = candidateResponse.get("interpretedMechanics");
        assertEquals("NOT_ATTEMPTED", candidateEnvelope.at("/processing/state").asText());
        assertTrue(candidateEnvelope.path("interpretations").isEmpty());
        assertEquals("CANDIDATE_PARTIAL", candidateEnvelope.at("/experimentalInterpretation/outcome").asText());
        assertTrue(candidateEnvelope.at("/experimentalInterpretation/candidateClaims").size() > 0);
        assertTrue(candidateEnvelope.at("/experimentalInterpretation/evidence").size() > 0);
        assertEquals("PENDING_REVIEW", candidateEnvelope
                .at("/experimentalInterpretation/candidateClaims/0/review/recordedStatus").asText());
    }

    @Test
    void noPatternMatchIsExplicitlyReturnedWithoutClaimingTheSpellHasNoMechanics() throws Exception {
        JsonNode response = mapper.readTree(mvc.perform(
                        get("/api/spells/spell:aag:air-bubble").param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        JsonNode experimental = response.at("/interpretedMechanics/experimentalInterpretation");
        assertEquals("NO_PATTERN_MATCH", experimental.path("outcome").asText());
        assertTrue(experimental.path("candidateClaims").isEmpty());
        assertTrue(experimental.path("unaccountedSourcePaths").size() > 0);
        assertTrue(experimental.path("diagnostics").toString().contains("SOURCE_MECHANICS_UNASSESSED"));
    }

    @Test
    void generalizedDamageAndHealingPatternsAreAvailableThroughTheExistingOptInRoute() throws Exception {
        mvc.perform(get("/api/spells/spell:phb:burning-hands").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.processing.state").value("PROCESSED"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.kind")
                        .value("SAVING_THROW_REQUIREMENT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.kind").value("DAMAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.amount.count").value(3))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.damageType.authoredCode")
                        .value("fire"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].outcome")
                        .value("FAILURE"));
        mvc.perform(get("/api/spells/spell:phb:cure-wounds").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.kind").value("HEALING"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.kind").value("SUM"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.terms[0].count")
                        .value(1))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.terms[0].sides")
                        .value(8))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.terms[1].symbol")
                        .value("CASTER_SPELLCASTING_ABILITY_MODIFIER"));
    }

    @Test
    void inflictWoundsAttackRollIsAvailableOnlyInPrivateInspection() throws Exception {
        String ordinary = mvc.perform(get("/api/spells/spell:phb:inflict-wounds"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(mapper.readTree(ordinary).has("interpretedMechanics"));

        mvc.perform(get("/api/spells/spell:phb:inflict-wounds").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(2))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.kind").value("ATTACK_ROLL"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.attackKind")
                        .value("MELEE_SPELL"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.actorRole").value("CASTER"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].kind")
                        .value("APPLIES_ON_ATTACK_ROLL_OUTCOME"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].outcome")
                        .value("HIT"))
                .andExpect(jsonPath("$.interpretedMechanics.reviewSummary.verified").value(0));
    }

    @Test
    void witchBoltPersistentAndActionActivatedMechanicsStayInPrivateInspection() throws Exception {
        String ordinary = mvc.perform(get("/api/spells/spell:phb:witch-bolt"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(mapper.readTree(ordinary).has("interpretedMechanics"));

        mvc.perform(get("/api/spells/spell:phb:witch-bolt").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(4))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.kind")
                        .value("PERSISTENT_SPELL_EFFECT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.establishment.outcome")
                        .value("HIT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.actionActivation.optional")
                        .value(true))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.actionActivation.timing")
                        .value("ACTION_ON_SUBSEQUENT_TURN"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.actionActivation.resolution")
                        .value("AUTOMATIC_WITHOUT_ATTACK_OR_SAVE"))
                .andExpect(jsonPath("$.interpretedMechanics.reviewSummary.verified").value(0));
    }

    @Test
    void iceKnifeOutcomeIndependentExplosionStaysInPrivateInspection() throws Exception {
        String ordinary = mvc.perform(get("/api/spells/spell:xge:ice-knife"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(mapper.readTree(ordinary).has("interpretedMechanics"));

        mvc.perform(get("/api/spells/spell:xge:ice-knife").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(5))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].payload.kind")
                        .value("ATTACK_TRIGGERED_AREA_EFFECT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].payload.trigger")
                        .value("AFTER_ATTACK_REGARDLESS_OF_OUTCOME"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].context.bindings[0].kind")
                        .value("APPLIES_TO_AREA_EFFECT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[4].context.bindings[1].damageOutcome")
                        .value("FULL_DAMAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[4].context.bindings[2].damageOutcome")
                        .value("NO_DAMAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.reviewSummary.verified").value(0));
    }

    @Test
    void rayOfFrostAttackAndSpeedEffectAreAvailableOnlyThroughPrivateInspection() throws Exception {
        String ordinary = mvc.perform(get("/api/spells/spell:phb:ray-of-frost"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(mapper.readTree(ordinary).has("interpretedMechanics"));

        mvc.perform(get("/api/spells/spell:phb:ray-of-frost").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(3))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.kind").value("ATTACK_ROLL"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.attackKind")
                        .value("RANGED_SPELL"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].kind")
                        .value("APPLIES_ON_ATTACK_ROLL_OUTCOME"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].revision").value(2))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.characterLevelScaling.tiers[0].minimumCharacterLevel")
                        .value(5))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.characterLevelScaling.tiers[2].amount.count")
                        .value(4))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].payload.statistics[0]")
                        .value("MOVEMENT_SPEED"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].context.bindings[0].outcome")
                        .value("HIT"))
                .andExpect(jsonPath("$.interpretedMechanics.unsupported[0].id")
                        .value("spell:phb:ray-of-frost:unsupported:attack-range"))
                .andExpect(jsonPath("$.interpretedMechanics.reviewSummary.verified").value(0));
    }

    @Test
    void fireBoltTargetAlternativesAndIgnitionDiagnosticArePrivateOnly() throws Exception {
        String ordinary = mvc.perform(get("/api/spells/spell:phb:fire-bolt"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(mapper.readTree(ordinary).has("interpretedMechanics"));

        mvc.perform(get("/api/spells/spell:phb:fire-bolt").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(2))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.kind")
                        .value("ATTACK_ROLL"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].context.subject.scope")
                        .value("SINGLE_TARGET"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].context.subject.eligibleTargetCategories[0]")
                        .value("CREATURE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].context.subject.eligibleTargetCategories[1]")
                        .value("OBJECT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.subject.scope")
                        .value("SINGLE_TARGET"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].outcome")
                        .value("HIT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.characterLevelScaling.tiers[2].amount.count")
                        .value(4))
                .andExpect(jsonPath("$.interpretedMechanics.unsupported[0].id")
                        .value("fire-bolt-object-ignition"))
                .andExpect(jsonPath("$.interpretedMechanics.coverage.scopeExtent").value("DECLARED_SUBSET"))
                .andExpect(jsonPath("$.interpretedMechanics.readyForHumanReviewWithinScope").doesNotExist())
                .andExpect(jsonPath("$.interpretedMechanics.reviewSummary.verified").value(0));
    }

    @Test
    void conditionalAttackEffectsAreAvailableOnlyThroughPrivateInspection() throws Exception {
        for (String id : java.util.List.of("spell:phb:chill-touch", "spell:phb:shocking-grasp")) {
            String ordinary = mvc.perform(get("/api/spells/" + id))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            assertFalse(mapper.readTree(ordinary).has("interpretedMechanics"), id);
        }

        mvc.perform(get("/api/spells/spell:phb:chill-touch").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(4))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.attackKind")
                        .value("RANGED_SPELL"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].outcome")
                        .value("HIT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].payload.kind")
                        .value("HIT_POINT_RECOVERY_PREVENTION"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].context.bindings[1].duration.actor")
                        .value("CASTER"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.kind")
                        .value("ATTACK_ROLL_MODIFIER"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.modifier")
                        .value("DISADVANTAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.affectedRolls.actor")
                        .value("AFFECTED_SUBJECT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.affectedRolls.target")
                        .value("CASTER"))
                .andExpect(jsonPath("$.interpretedMechanics.coverage.status").value("COMPLETE_WITHIN_SCOPE"))
                .andExpect(jsonPath("$.interpretedMechanics.readyForHumanReviewWithinScope").value(true))
                .andExpect(jsonPath("$.interpretedMechanics.unsupported[0].id")
                        .value("spell:phb:chill-touch:unsupported:attack-range"));

        mvc.perform(get("/api/spells/spell:phb:shocking-grasp").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(4))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.attackKind")
                        .value("MELEE_SPELL"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].outcome")
                        .value("HIT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].payload.kind")
                        .value("REACTION_PREVENTION"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].context.bindings[1].duration.actor")
                        .value("AFFECTED_SUBJECT"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.kind")
                        .value("ATTACK_ROLL_MODIFIER"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].payload.modifier")
                        .value("ADVANTAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[3].context.bindings[0].condition")
                        .value("ATTACK_ROLL_TARGET_WEARING_ARMOR_MADE_OF_METAL"))
                .andExpect(jsonPath("$.interpretedMechanics.coverage.status").value("COMPLETE_WITHIN_SCOPE"))
                .andExpect(jsonPath("$.interpretedMechanics.unsupported[0].id")
                        .value("spell:phb:shocking-grasp:unsupported:attack-range"));
    }

    @Test
    void newModifierAndResistanceClaimsRemainPrivateAndInspectable() throws Exception {
        mvc.perform(get("/api/spells/spell:phb:mage-armor").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.kind")
                        .value("STATISTIC_MODIFIER"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.operation")
                        .value("SET_BASE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.terms[0].value")
                        .value(13))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].context.subject.maximumTargets")
                        .doesNotExist());
        mvc.perform(get("/api/spells/spell:phb:protection-from-energy").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.kind")
                        .value("DAMAGE_RESISTANCE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.damageTypes.count").value(1))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].context.bindings[1].kind")
                        .value("REQUIRES_CONCENTRATION"));
        mvc.perform(get("/api/spells/spell:phb:bane").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[2].kind")
                        .value("MODIFIER_ON_SAVE_OUTCOME"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[2].outcome")
                        .value("FAILURE"));
    }

    @Test
    void formulaLocatorsReconstructEveryOriginalSpanWithoutPublishingExpressions() throws Exception {
        Map<String, JsonNode> documents = new HashMap<>();
        for (var document : inspection.documents()) {
            var result = inspection.findById(new SpellId(document.owner().id()));
            for (var item : result.observations()) {
                var observation = (FormulaObservationInspectionDto) item;
                var locator = (TextEvidenceLocatorDto) observation.evidence();
                String resource = observation.sourceRevisionId();
                if (!documents.containsKey(resource)) {
                    byte[] bytes = new ClassPathResource(resource).getContentAsByteArray();
                    assertEquals(result.sourceRevisions().getFirst().resourceSha256(), MechanicsHashes.sha256(bytes));
                    documents.put(resource, mapper.readTree(bytes));
                }
                String original = documents.get(resource).at(locator.jsonPointer()).textValue();
                String token = locator.span().extract(original);
                var authoritative = formulas.findBySpellId(new SpellId(observation.spellId()))
                        .get(observation.occurrenceIndex());
                assertEquals(authoritative.observation().tag().originalText(), token);
            }
        }
        String fireball = mapper.writeValueAsString(service.findById("spell:phb:fireball", "interpretedMechanics"));
        for (String excluded : List.of("{@damage", "{@dice", "rawToken", "arguments", "reviewerId", "humanDecision")) {
            assertFalse(fireball.contains(excluded), excluded);
        }
    }

    @Test
    void ordinaryFieldsAndOrderingRemainIdenticalAcrossAll969OptedInResponses() throws Exception {
        Map<String, Integer> formulaStatuses = new HashMap<>();
        for (var spell : spells.findAll()) {
            var ordinary = service.findById(spell.id().value());
            var optedIn = service.findById(spell.id().value(), "interpretedMechanics");
            assertNull(ordinary.interpretedMechanics());
            formulaStatuses.merge(ordinary.formulaProjectionStatus().name(), 1, Integer::sum);
            var projected = (ObjectNode) mapper.valueToTree(optedIn);
            assertTrue(projected.has("interpretedMechanics"));
            projected.remove("interpretedMechanics");
            assertEquals(mapper.writeValueAsString(ordinary), mapper.writeValueAsString(projected));
        }
        assertEquals(Map.of("NONE_OBSERVED", 547, "WITHHELD", 422), formulaStatuses);
    }

    @Test
    void detailAndByNameOptInPreserveEditionMultiplicityAndOmitEmptyLists() throws Exception {
        mvc.perform(get("/api/spells/spell:phb:shield"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.interpretedMechanics").doesNotExist());
        mvc.perform(get("/api/spells/spell:phb:shield").param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("spell:phb:shield"))
                .andExpect(jsonPath("$.formulaProjectionStatus").value("NONE_OBSERVED"))
                .andExpect(jsonPath("$.interpretedMechanics.processing.state").value("PROCESSED"))
                .andExpect(jsonPath("$.interpretedMechanics.coverage.status").value("PARTIAL"))
                .andExpect(jsonPath("$.interpretedMechanics.reviewSummary.verified").value(0))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(5));
        mvc.perform(get("/api/spells/by-name/Shield").param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].interpretedMechanics.projection").value("PRIVATE_INSPECTION"))
                .andExpect(jsonPath("$[1].interpretedMechanics.projection").value("PRIVATE_INSPECTION"));
        mvc.perform(get("/api/spells/by-name/Shield").param("source", "XPHB").param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("spell:xphb:shield"));
    }

    @Test
    void bothShieldInspectionResponsesContainRealTypedClaimsAndNoInventedApprovals() throws Exception {
        for (String edition : List.of("phb", "xphb")) {
            String id = "spell:" + edition + ":shield";
            String response = mvc.perform(get("/api/spells/" + id).param("include", "interpretedMechanics"))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            JsonNode envelope = mapper.readTree(response).get("interpretedMechanics");
            assertEquals(5, envelope.get("interpretations").size());
            assertEquals(13, envelope.get("evidence").size());
            assertEquals("PARTIAL", envelope.at("/coverage/status").textValue());
            boolean foundBonus = false;
            for (JsonNode claim : envelope.get("interpretations")) {
                assertEquals("NEEDS_REVIEW", claim.at("/licensing/status").textValue());
                assertEquals("PENDING_REVIEW", claim.at("/review/recordedStatus").textValue());
                assertFalse(claim.at("/review/effectiveStatus").textValue().equals("VERIFIED"));
                assertTrue(claim.get("evidenceAssociations").size() > 0);
                if (claim.at("/payload/kind").textValue().equals("ARMOR_CLASS_MODIFIER")) {
                    foundBonus = true;
                    assertEquals(5, claim.at("/payload/amount/value").intValue());
                    assertEquals("+5", claim.at("/payload/amount/authoredToken").textValue());
                    assertEquals("CASTER", claim.at("/context/subject/kind").textValue());
                }
            }
            assertTrue(foundBonus);
            assertFalse(response.contains("{@spell"));
            assertFalse(response.contains("humanDecision"));
            assertFalse(response.contains("reviewerId"));
            assertFalse(response.contains("An invisible barrier"));
            assertFalse(response.contains("An imperceptible barrier"));
            assertSame(inspection.findById(new SpellId(id)), service.findById(id, "interpretedMechanics").interpretedMechanics());
        }
        assertTrue(publication.index().authorizedObservations().isEmpty());
    }

    @Test
    void byNameReturnsTwoProcessedSourceDistinctDocumentsAndFireballIsPartiallyProcessed() throws Exception {
        String byName = mvc.perform(get("/api/spells/by-name/Shield").param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode details = mapper.readTree(byName);
        assertEquals(2, details.size());
        assertNotEquals(details.get(0).at("/interpretedMechanics/sourceRevisions/0/resourceSha256").textValue(),
                details.get(1).at("/interpretedMechanics/sourceRevisions/0/resourceSha256").textValue());
        for (JsonNode detail : details) {
            assertEquals("PROCESSED", detail.at("/interpretedMechanics/processing/state").textValue());
            assertEquals(5, detail.at("/interpretedMechanics/interpretations").size());
        }
        mvc.perform(get("/api/spells/spell:phb:fireball").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.processing.state").value("PROCESSED"))
                .andExpect(jsonPath("$.interpretedMechanics.coverage.status").value("PARTIAL"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(3))
                .andExpect(jsonPath("$.interpretedMechanics.observations.length()").value(2));
    }

    @Test
    void fireballInspectionExposesFailedSaveDamageAndExplicitDeferredPortions() throws Exception {
        mvc.perform(get("/api/spells/spell:phb:fireball").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.processing.state").value("PROCESSED"))
                .andExpect(jsonPath("$.interpretedMechanics.coverage.status").value("PARTIAL"))
                .andExpect(jsonPath("$.interpretedMechanics.unsupported.length()").value(1))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.ability.ability").value("DEXTERITY"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.difficulty.kind").value("UNKNOWN"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.amount.count").value(8))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.amount.sides").value(6))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.damageType.authoredCode").value("fire"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].outcome").value("FAILURE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].damageOutcome")
                        .value("FULL_DAMAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[1].outcome")
                        .value("SUCCESS"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[1].damageOutcome")
                        .value("HALF_DAMAGE"));
    }

    @Test
    void expandedSpellInspectionExposesTypedDamageSaveOutcomeAndScaling() throws Exception {
        mvc.perform(get("/api/spells/spell:phb:blight").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.processing.state").value("PROCESSED"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.ability.ability")
                        .value("CONSTITUTION"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.amount.count").value(8))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[0].damageOutcome")
                        .value("FULL_DAMAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].context.bindings[1].damageOutcome")
                        .value("HALF_DAMAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].payload.effect").value("DAMAGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].payload.increment.count").value(1))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[2].payload.increment.sides").value(8));
    }

    @Test
    void conditionApplicationAndImmunityAppearInPrivateSpellInspection() throws Exception {
        mvc.perform(get("/api/spells/spell:phb:charm-person").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.kind")
                        .value("CONDITION_CHANGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.operation")
                        .value("APPLY"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].payload.condition")
                        .value("CHARMED"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[1].review.recordedStatus")
                        .value("PENDING_REVIEW"));
        mvc.perform(get("/api/spells/spell:phb:freedom-of-movement")
                        .param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.operation")
                        .value("GRANT_IMMUNITY"));
    }

    @Test
    void healingWordInspectionRetainsBothAmountTermsWithoutEvaluatingTheModifier() throws Exception {
        mvc.perform(get("/api/spells/spell:phb:healing-word").param("include", "interpretedMechanics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedMechanics.interpretations.length()").value(2))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.kind").value("SUM"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.terms[0].count").value(1))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.terms[0].sides").value(4))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.amount.terms[1].symbol")
                        .value("CASTER_SPELLCASTING_ABILITY_MODIFIER"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].review.recordedStatus").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].licensing.status").value("NEEDS_REVIEW"))
                .andExpect(jsonPath("$.interpretedMechanics.unsupported.length()").value(1));
    }

    @Test
    void invalidIncludesUseExisting400AndUnknownOwnersRetain404() throws Exception {
        for (String include : List.of("", "invalid", "InterpretedMechanics", "interpretedMechanics,invalid")) {
            mvc.perform(get("/api/spells/spell:phb:shield").param("include", include)).andExpect(status().isBadRequest());
            mvc.perform(get("/api/spells/by-name/Shield").param("include", include)).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/spells/spell:phb:not-a-spell").param("include", "interpretedMechanics"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/spells/by-name/NotASpell").param("include", "interpretedMechanics"))
                .andExpect(status().isNotFound());
    }

    @Test
    void collectionsKeepExistingFiltersAndNeverAcquireInspectionFields() throws Exception {
        String ordinary = mvc.perform(get("/api/spells").param("source", "PHB").param("level", "1"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String included = mvc.perform(get("/api/spells").param("source", "PHB").param("level", "1")
                        .param("include", "interpretedMechanics"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(ordinary, included);
        assertFalse(ordinary.contains("interpretedMechanics"));
        assertFalse(ordinary.contains("formulaProjectionStatus"));
    }

    @Test
    void bothLegacyDtoConstructorsRemainSourceCompatibleWithOmittedInspection() throws Exception {
        var value = service.findById("spell:phb:shield");
        var p1a = new SpellDto(value.id(), value.name(), value.source(), value.aliases(), value.level(), value.school(),
                value.castingTimes(), value.ritual(), value.range(), value.components(), value.durations(), value.mechanics(),
                value.scaling(), value.sourceRelationships(), value.formulaProjectionStatus());
        var original = new SpellDto(value.id(), value.name(), value.source(), value.aliases(), value.level(), value.school(),
                value.castingTimes(), value.ritual(), value.range(), value.components(), value.durations(), value.mechanics(),
                value.scaling(), value.sourceRelationships());
        assertEquals(value, p1a);
        assertNull(original.interpretedMechanics());
        assertNull(original.formulaProjectionStatus());
        assertFalse(mapper.writeValueAsString(original).contains("interpretedMechanics"));
    }

    @Test
    void catalogRejectsMissingDuplicateUnknownAndIncompleteObservationOwners() {
        var manifest = new SpellMechanicsDocumentFactory(mapper).loadOwners();
        var documents = inspection.documents();
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsInspectionCatalog(
                documents.subList(1, documents.size()), spells.findAll(), formulas.index(), sources, manifest));
        var duplicate = new ArrayList<>(documents);
        duplicate.add(documents.getFirst());
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsInspectionCatalog(
                duplicate, spells.findAll(), formulas.index(), sources, manifest));
        var existing = documents.stream().filter(document -> document.owner().id().equals("spell:phb:magic-missile"))
                .findFirst().orElseThrow();
        var withoutObservations = MechanicsDocument.create(existing.owner(), 1, existing.sources(), existing.processing(),
                List.of(), List.of(), List.of(), List.of(), existing.coverage());
        var missing = new ArrayList<>(documents);
        missing.set(missing.indexOf(existing), withoutObservations);
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsInspectionCatalog(
                missing, spells.findAll(), formulas.index(), sources, manifest));
        var unknownOwner = new MechanicsOwnerReference(CatalogDomain.SPELL, "spell:phb:unknown", existing.owner().source());
        var unknown = MechanicsDocument.create(unknownOwner, 1, existing.sources(), existing.processing(),
                List.of(), List.of(), List.of(), List.of(), existing.coverage());
        var unknownDocuments = new ArrayList<>(documents);
        unknownDocuments.add(unknown);
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsInspectionCatalog(
                unknownDocuments, spells.findAll(), formulas.index(), sources, manifest));
        assertThrows(IllegalStateException.class, () -> inspection.findById(new SpellId("spell:phb:unknown")));
    }

    @Test
    void factoryRejectsSourceOwnerAndFormulaIndexMismatchesWithoutAnEmptyFallback() {
        var factory = new SpellMechanicsDocumentFactory(mapper);
        var manifest = factory.loadOwners();
        var incomplete = new ArrayList<>(manifest);
        incomplete.removeFirst();
        assertThrows(IllegalArgumentException.class, () -> factory.unattempted(spells.findAll(), formulas.index(), incomplete));
        var duplicate = new ArrayList<>(manifest);
        duplicate.add(manifest.getFirst());
        assertThrows(IllegalArgumentException.class, () -> factory.unattempted(spells.findAll(), formulas.index(), duplicate));
        var unknown = new ArrayList<>(manifest);
        var first = manifest.getFirst();
        unknown.set(0, new SpellMechanicsSourceOwner(new SpellId("spell:phb:unknown"), first.source(),
                first.revision(), first.jsonPointer()));
        assertThrows(IllegalArgumentException.class, () -> factory.unattempted(spells.findAll(), formulas.index(), unknown));
        var index = new SpellFormulaAnnotationIndex(spells.findAll().subList(1, spells.size()),
                formulas.index().sourceFileCount(), List.of(), List.of());
        assertThrows(IllegalArgumentException.class, () -> factory.unattempted(spells.findAll(), index, manifest));
    }

    @Test
    void catalogRejectsChangedSourceFingerprintsEvenForSpellsWithoutFormulas() {
        var manifest = new SpellMechanicsDocumentFactory(mapper).loadOwners();
        var shield = inspection.documents().stream().filter(document -> document.owner().id().equals("spell:phb:shield"))
                .findFirst().orElseThrow();
        var actual = shield.sources().getFirst();
        var changed = new SourceArtifactRevision(actual.datasetVersion(), actual.resourcePath(), "0".repeat(64));
        var stale = MechanicsDocument.create(shield.owner(), 1, List.of(changed), shield.processing(),
                List.of(), List.of(), List.of(), List.of(), shield.coverage());
        var documents = new ArrayList<>(inspection.documents());
        documents.set(documents.indexOf(shield), stale);
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsInspectionCatalog(
                documents, spells.findAll(), formulas.index(), sources, manifest));
    }

    @Test
    void indexesAndResponsesAreImmutableCachedAndDeterministic() throws Exception {
        var id = new SpellId("spell:phb:fireball");
        assertSame(inspection.findById(id), inspection.findById(id));
        assertSame(inspection.findById(id), service.findById(id.value(), "interpretedMechanics").interpretedMechanics());
        assertThrows(UnsupportedOperationException.class, () -> inspection.documents().clear());
        assertThrows(UnsupportedOperationException.class, () -> inspection.findById(id).observations().clear());
        assertEquals(inspection.documents().stream().map(document -> document.owner().id()).sorted().toList(),
                inspection.documents().stream().map(document -> document.owner().id()).toList());
        assertEquals(mapper.writeValueAsString(service.findById(id.value(), "interpretedMechanics")),
                mapper.writeValueAsString(service.findById(id.value(), "interpretedMechanics")));
    }

    @Test
    void genericExperimentalV2SeparatesMechanicsEvidenceDiagnosticsAndMetadata() throws Exception {
        String ordinary = mvc.perform(get("/api/spells/spell:phb:fireball"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(mapper.readTree(ordinary).has("interpretedMechanics"));

        JsonNode response = mapper.readTree(mvc.perform(get("/api/spells/spell:phb:fireball")
                .param("include", "interpretedMechanics")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        JsonNode canonical = response.path("interpretedMechanics");
        JsonNode experimental = canonical.path("experimentalInterpretationV2");
        assertEquals("EXPERIMENTAL", experimental.path("origin").asText());
        assertEquals(2, experimental.path("apiVersion").asInt());
        assertTrue(experimental.path("mechanics").isArray());
        assertEquals(2, experimental.path("relationships").size());
        assertTrue(experimental.path("evidence").isArray());
        assertTrue(experimental.path("diagnostics").isArray());
        assertEquals("PENDING_REVIEW", experimental.path("metadata").path("reviewStatus").asText());
        assertFalse(experimental.path("metadata").path("publicationAuthorized").asBoolean());
        assertTrue(canonical.path("interpretations").isArray());
        assertFalse(experimental.path("mechanics").toString().contains("evidenceAssociations"));
        assertFalse(experimental.path("mechanics").toString().contains("licensing"));
        JsonNode relationship = StreamSupport.stream(experimental.path("relationships").spliterator(), false)
                .filter(item -> item.path("type").asText().equals("SAVING_THROW_DAMAGE_OUTCOME"))
                .findFirst().orElseThrow();
        assertEquals("SAVING_THROW_DAMAGE_OUTCOME", relationship.path("type").asText());
        assertEquals("FAILURE", relationship.path("outcomes").get(0).path("outcome").asText());
        assertEquals("FULL_DAMAGE", relationship.path("outcomes").get(0).path("application").asText());
        assertEquals("SUCCESS", relationship.path("outcomes").get(1).path("outcome").asText());
        assertEquals("HALF_DAMAGE", relationship.path("outcomes").get(1).path("application").asText());
        assertTrue(experimental.path("mechanics").toString().contains(relationship.path("sourceMechanicId").asText()));
        assertTrue(experimental.path("mechanics").toString().contains(relationship.path("targetMechanicId").asText()));
        assertTrue(experimental.path("evidence").toString().contains(relationship.path("id").asText()));
        assertTrue(StreamSupport.stream(experimental.path("relationships").spliterator(), false)
                .anyMatch(item -> item.path("type").asText().equals("SCALING_APPLIES_TO")
                        && item.path("scalingRule").path("affectedQuantity").asText().equals("DAMAGE_AMOUNT")));

        JsonNode xphb = mapper.readTree(mvc.perform(get("/api/spells/spell:xphb:fireball")
                .param("include", "interpretedMechanics")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        assertEquals("data/spells/spells-phb.json",
                canonical.path("experimentalInterpretationV2").path("metadata").path("sourceResource").asText());
        assertEquals("data/spells/spells-xphb.json", xphb.path("interpretedMechanics")
                .path("experimentalInterpretationV2").path("metadata").path("sourceResource").asText());
        assertNotEquals(canonical.path("experimentalInterpretationV2").path("metadata").path("sourceSha256").asText(),
                xphb.path("interpretedMechanics").path("experimentalInterpretationV2")
                        .path("metadata").path("sourceSha256").asText());
    }

    @Test
    void genericScalingIsProjectedAsIndependentMechanicsAndTypedRelationships() throws Exception {
        assertScaling("spell:phb:fireball", "SPELL_SLOT_LEVEL", "DAMAGE_AMOUNT", "DAMAGE");
        assertScaling("spell:phb:healing-word", "SPELL_SLOT_LEVEL", "HEALING_AMOUNT", "HEALING");
        assertScaling("spell:phb:scorching-ray", "SPELL_SLOT_LEVEL", "PROJECTILE_COUNT", "DAMAGE");
        assertScaling("spell:phb:eldritch-blast", "CHARACTER_LEVEL", "ATTACK_COUNT", "ATTACK");
        assertScaling("spell:phb:ray-of-frost", "CHARACTER_LEVEL", "DAMAGE_AMOUNT", "DAMAGE");
        assertScaling("spell:phb:magic-missile", "SPELL_SLOT_LEVEL", "PROJECTILE_COUNT", "DAMAGE");
        assertScaling("spell:xge:ice-knife", "SPELL_SLOT_LEVEL", "DAMAGE_AMOUNT", "DAMAGE");
    }

    @Test
    void genericConditionOperationsAndLifecycleRelationshipsAreProjectedSeparately() throws Exception {
        JsonNode hold = experimentalSpell("spell:phb:hold-person");
        JsonNode holdMechanics = hold.path("mechanics");
        JsonNode paralysis = StreamSupport.stream(holdMechanics.spliterator(), false)
                .filter(item -> item.path("properties").path("kind").asText().equals("CONDITION_CHANGE")
                        && item.path("properties").path("condition").asText().equals("PARALYZED"))
                .findFirst().orElseThrow(() -> new AssertionError(hold.toPrettyString()));
        assertEquals("APPLY", paralysis.path("properties").path("operation").asText());
        assertTrue(StreamSupport.stream(hold.path("relationships").spliterator(), false)
                .anyMatch(item -> item.path("type").asText().equals("SAVE_CONDITION_OUTCOME")
                        && item.path("conditionOutcomes").toString().contains("FAILURE")
                        && item.path("conditionOutcomes").toString().contains("APPLY")));
        assertTrue(StreamSupport.stream(hold.path("relationships").spliterator(), false)
                .anyMatch(item -> item.path("type").asText().equals("CONDITION_TERMINATION")
                        && item.path("terminationRule").path("trigger").asText().equals("REPEATED_SAVE_SUCCESS")));
        assertTrue(StreamSupport.stream(hold.path("relationships").spliterator(), false)
                .anyMatch(item -> item.path("type").asText().equals("CONDITION_DURATION")));

        JsonNode lesser = experimentalSpell("spell:phb:lesser-restoration");
        assertTrue(StreamSupport.stream(lesser.path("mechanics").spliterator(), false)
                .anyMatch(item -> item.path("properties").path("kind").asText().equals("CONDITION_CHOICE")
                        && item.path("properties").path("operation").asText().equals("REMOVE")
                        && item.path("properties").path("alternatives").size() == 4));

        JsonNode protection = experimentalSpell("spell:phb:protection-from-evil-and-good");
        assertTrue(StreamSupport.stream(protection.path("mechanics").spliterator(), false)
                .filter(item -> item.path("properties").path("kind").asText().equals("CONDITION_CHANGE"))
                .anyMatch(item -> item.path("properties").path("operation").asText().equals("PREVENT")));

        JsonNode ray = experimentalSpell("spell:phb:ray-of-sickness");
        JsonNode poisoned = StreamSupport.stream(ray.path("mechanics").spliterator(), false)
                .filter(item -> item.path("properties").path("kind").asText().equals("CONDITION_CHANGE")
                        && item.path("properties").path("condition").asText().equals("POISONED"))
                .findFirst().orElseThrow(() -> new AssertionError(ray.toPrettyString()));
        assertTrue(StreamSupport.stream(ray.path("relationships").spliterator(), false)
                .anyMatch(item -> item.path("type").asText().equals("SAVE_CONDITION_OUTCOME")
                        && item.path("targetMechanicId").asText().equals(poisoned.path("id").asText())
                        && item.path("conditionOutcomes").toString().contains("FAILURE")
                        && item.path("conditionOutcomes").toString().contains("APPLY")), ray.toPrettyString());

        JsonNode web = experimentalSpell("spell:phb:web");
        assertTrue(StreamSupport.stream(web.path("relationships").spliterator(), false)
                .anyMatch(item -> item.path("type").asText().equals("CONDITION_TERMINATION")
                        && item.path("terminationRule").path("trigger").asText()
                                .equals("ACTION_SUCCESSFUL_CHECK")), web.toPrettyString());

        JsonNode chill = experimentalSpell("spell:phb:chill-touch");
        assertFalse(StreamSupport.stream(chill.path("mechanics").spliterator(), false)
                .anyMatch(item -> item.path("properties").path("kind").asText().startsWith("CONDITION_")));

        JsonNode xphbHold = experimentalSpell("spell:xphb:hold-person");
        assertTrue(StreamSupport.stream(xphbHold.path("mechanics").spliterator(), false)
                .anyMatch(item -> item.path("properties").path("kind").asText().equals("CONDITION_CHANGE")
                        && item.path("properties").path("condition").asText().equals("PARALYZED")));

        JsonNode xphbHallow = experimentalSpell("spell:xphb:hallow");
        assertFalse(StreamSupport.stream(xphbHallow.path("mechanics").spliterator(), false)
                .anyMatch(item -> item.path("properties").path("kind").asText().equals("CONDITION_CHANGE")
                        && item.path("properties").path("condition").asText().equals("CHARMED")
                        && item.path("properties").path("operation").asText().equals("APPLY")));
        assertTrue(StreamSupport.stream(xphbHallow.path("mechanics").spliterator(), false)
                .anyMatch(item -> item.path("properties").path("kind").asText().equals("CONDITION_CHANGE")
                        && item.path("properties").path("condition").asText().equals("FRIGHTENED")
                        && item.path("properties").path("operation").asText().equals("PREVENT")));
    }

    private JsonNode experimentalSpell(String ownerId) throws Exception {
        JsonNode response = mapper.readTree(mvc.perform(get("/api/spells/" + ownerId)
                .param("include", "interpretedMechanics")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        return response.path("interpretedMechanics").path("experimentalInterpretationV2");
    }

    private void assertScaling(String ownerId, String axis, String quantity, String targetFamily) throws Exception {
        JsonNode response = mapper.readTree(mvc.perform(get("/api/spells/" + ownerId)
                .param("include", "interpretedMechanics")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        JsonNode experimental = response.path("interpretedMechanics").path("experimentalInterpretationV2");
        assertTrue(StreamSupport.stream(experimental.path("mechanics").spliterator(), false)
                .anyMatch(item -> item.path("type").asText().equals("SCALING")
                        && item.path("properties").path("axis").asText().equals(axis)), ownerId);
        assertTrue(StreamSupport.stream(experimental.path("relationships").spliterator(), false)
                .anyMatch(item -> item.path("type").asText().equals("SCALING_APPLIES_TO")
                        && item.path("scalingRule").path("affectedQuantity").asText().equals(quantity)
                        && StreamSupport.stream(experimental.path("mechanics").spliterator(), false)
                                .anyMatch(mechanic -> mechanic.path("id").asText()
                                        .equals(item.path("targetMechanicId").asText())
                                        && mechanic.path("type").asText().equals(targetFamily))), ownerId);
    }

    @Test
    void legacyServiceWithoutAnInspectionCatalogCannotPretendInspectionWasSuccessful() {
        var legacy = new SpellsService(spells, new SpellPublicMapper());
        assertNull(legacy.findById("spell:phb:shield").interpretedMechanics());
        assertThrows(IllegalStateException.class,
                () -> legacy.findById("spell:phb:shield", "interpretedMechanics"));
    }
}
