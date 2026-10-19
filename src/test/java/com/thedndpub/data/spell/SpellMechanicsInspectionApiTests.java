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
                    "spell:phb:prayer-of-healing").contains(definition.id().value())) {
                processedOwners++;
                assertEquals(ProcessingState.PROCESSED, result.processing().state());
                assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
                assertTrue(!result.interpretations().isEmpty()
                        || result.coverage().status() == CoverageStatus.NONE_SUPPORTED);
                assertEquals(result.interpretations().size(), result.reviewSummary().needsMechanicsReview());
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
                assertEquals(result.interpretations().size(), result.reviewSummary().needsMechanicsReview());
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
        assertEquals(87, processedOwners);
        assertEquals(882, inspection.size() - processedOwners);
        assertEquals(422, ownersWithObservations);
        assertEquals(547, inspection.size() - ownersWithObservations);
        assertTrue(publication.index().authorizedObservations().isEmpty());
        assertEquals(250, publication.index().candidates().size());
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
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.kind")
                        .value("CONDITION_CHANGE"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.operation")
                        .value("APPLY"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].payload.condition")
                        .value("CHARMED"))
                .andExpect(jsonPath("$.interpretedMechanics.interpretations[0].review.recordedStatus")
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
    void legacyServiceWithoutAnInspectionCatalogCannotPretendInspectionWasSuccessful() {
        var legacy = new SpellsService(spells, new SpellPublicMapper());
        assertNull(legacy.findById("spell:phb:shield").interpretedMechanics());
        assertThrows(IllegalStateException.class,
                () -> legacy.findById("spell:phb:shield", "interpretedMechanics"));
    }
}
