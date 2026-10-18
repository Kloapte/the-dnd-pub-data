package com.thedndpub.data.mechanics.model;

import com.thedndpub.data.mechanics.inspection.EvidenceLocatorDto;
import com.thedndpub.data.mechanics.inspection.MechanicsObservationDto;
import com.thedndpub.data.mechanics.inspection.MechanicInspectionDto;
import com.thedndpub.data.mechanics.inspection.MechanicsInspectionAvailability;
import com.thedndpub.data.mechanics.inspection.MechanicsInspectionDiagnostic;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionDto;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionMapper;
import com.thedndpub.data.mechanics.review.MechanicsReviewAction;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.thedndpub.data.mechanics.model.MechanicsModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/** Synthetic model constructions, never production interpretations or independently reviewed gold fixtures. */
class SpellMechanicsInspectionProjectionTests {
    private final SpellMechanicsInspectionMapper mapper = new SpellMechanicsInspectionMapper();

    static Stream<MechanicPayload> payloads() {
        return MechanicsValuesTests.payloads();
    }

    @ParameterizedTest
    @MethodSource("payloads")
    void projectsEveryInitialPayloadWithoutInterpretingOrFilteringIt(MechanicPayload payload) throws Exception {
        var record = proposal(PHB, "synthetic-claim", payload, ac().context(), complete(), shieldBody());
        var inspection = mapper.project(document(List.of(record)));
        var projected = inspection.interpretations().getFirst();
        assertEquals(payload, projected.payload());
        assertEquals(record.context(), projected.context());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, projected.licensing().status());
        assertEquals(record.review().effectiveStatus(), projected.review().effectiveStatus());
        assertEquals(1, projected.revision());
        var codec = MechanicsJson.mapper();
        String json = codec.writeValueAsString(inspection);
        assertEquals(inspection, codec.readValue(json, SpellMechanicsInspectionDto.class));
        assertFalse(json.contains("humanDecision"));
        assertFalse(json.contains("This spell"));
    }

    @Test
    void linkedReactionDurationAndSpecialInteractionRetainEvidenceAssociations() throws Exception {
        var activation = proposal(PHB, "activation", new ReactionActivationMechanic(new ReactionActionCost(1),
                new AnyTrigger(List.of(new HitByAttackTrigger(AttackEventKind.ATTACK_HIT),
                        new TargetedBySpellTrigger(MechanicsValuesTests.unresolvedSpell())))),
                ac().context(), complete(), shieldBody());
        var duration = proposal(PHB, "duration", new EffectDurationMechanic(
                new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN)),
                ac().context(), complete(), shieldBody());
        var context = new MechanicContext(new CasterSubject(), List.of(
                new ActivatedBy(activation.revision().reference(activation.id()), List.of("body")),
                new AppliesForDuration(new ReferencedDuration(duration.revision().reference(duration.id())), List.of("body")),
                new IncludesTriggeringAttack(List.of("body"))));
        var protection = proposal(PHB, "special-interaction",
                new SpellDamagePreventionMechanic(MechanicsValuesTests.unresolvedSpell()),
                context, complete(), shieldBody());
        var result = mapper.project(document(List.of(activation, duration, protection)));
        assertEquals(List.of(activation.id(), duration.id(), protection.id()),
                result.interpretations().stream().map(MechanicInspectionDto::id).toList());
        assertEquals(context, result.interpretations().getLast().context());
        assertEquals("/payload", result.interpretations().getLast().evidenceAssociations().getFirst().claimPath());
        assertEquals("body", result.interpretations().getLast().evidenceAssociations().getFirst().evidenceId());
        var codec = MechanicsJson.mapper();
        assertEquals(result, codec.readValue(codec.writeValueAsBytes(result), SpellMechanicsInspectionDto.class));
    }

    @Test
    void partialInterpretationsAndUnsupportedFindingsAreDifferentFromUnattempted() throws Exception {
        var partial = proposal(PHB, "healing", new HealingMechanic(
                new UnknownAmount(UnknownValueReason.NOT_INTERPRETED), HealingMode.RESTORE_HIT_POINTS),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()),
                new MechanicsAssessment(MechanicsCompleteness.PARTIAL, MechanicsSupport.PROSE_INTERPRETATION,
                        List.of(), List.of()), shieldBody());
        var unsupported = new UnsupportedMechanicsConstruct("unsupported-clause", MechanicFamily.UNDETERMINED,
                UnsupportedReason.NO_SUPPORTED_PATTERN, List.of("body"), List.of());
        var result = mapper.project(document(PHB, List.of(partial), List.of(shieldBody()), List.of(), List.of(unsupported)));
        assertEquals(CoverageStatus.PARTIAL, result.coverage().status());
        assertEquals(1, result.coverage().unsupportedCount());
        assertEquals(MechanicsCompleteness.PARTIAL, result.interpretations().getFirst().assessment().completeness());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW, result.reviewSummary().aggregateStatus());
        assertEquals(MechanicsInspectionAvailability.AVAILABLE, result.availability());
        assertInstanceOf(UnknownAmount.class, ((HealingMechanic) result.interpretations().getFirst().payload()).amount());
        var noSupported = mapper.project(document(PHB, List.of(), List.of(shieldBody()), List.of(), List.of(unsupported)));
        assertEquals(CoverageStatus.NONE_SUPPORTED, noSupported.coverage().status());
        assertEquals(ProcessingState.PROCESSED, noSupported.processing().state());
        var codec = MechanicsJson.mapper();
        assertEquals(result, codec.readValue(codec.writeValueAsBytes(result), SpellMechanicsInspectionDto.class));
    }

    @Test
    void zeroFalseAndUnknownValuesRemainDistinctFromAbsentFields() throws Exception {
        var duration = proposal(PHB, "duration", new EffectDurationMechanic(new FixedDuration(DurationUnit.ROUND, 0, false)),
                ac().context(), complete(), shieldBody());
        var result = mapper.project(document(List.of(duration)));
        var json = MechanicsJson.mapper().readTree(MechanicsJson.mapper().writeValueAsBytes(result));
        assertEquals(0, json.path("interpretations").get(0).path("payload").path("specification").path("amount").intValue());
        assertTrue(json.path("interpretations").get(0).path("payload").path("specification").has("upTo"));
        assertFalse(json.path("interpretations").get(0).path("payload").path("specification").path("upTo").booleanValue());
        assertFalse(json.has("observations"));
        assertFalse(json.has("unsupported"));
        assertEquals(0, json.path("reviewSummary").path("verified").intValue());
    }

    @Test
    void reviewStatesAreComputedButHumanIdentitiesAndJournalsAreNeverProjected() throws Exception {
        for (var action : MechanicsReviewAction.values()) {
            var record = reviewed(ac(), action, false);
            var result = mapper.project(document(List.of(record)));
            var review = result.interpretations().getFirst().review();
            assertEquals(record.review().recordedStatus(), review.recordedStatus());
            assertEquals(record.review().effectiveStatus(), review.effectiveStatus());
            String json = MechanicsJson.mapper().writeValueAsString(result);
            for (String excluded : List.of("fixture-reviewer", "test-only-mechanics-mandate", "humanDecision",
                    "reviewerId", "authorityReference", "reviewedAt", "rationale", "supersedesDecisionId")) {
                assertFalse(json.contains(excluded), excluded);
            }
        }
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW,
                mapper.project(document(List.of(ac()))).interpretations().getFirst().review().effectiveStatus());
    }

    @Test
    void allRightsStatesRemainInspectableWithoutRightsDecisionAuditData() throws Exception {
        var scope = coverage(ac(), List.of("/payload", "/context"));
        var rights = List.of(UnassessedMechanicsLicensing.pending(), new SrdMechanicsLicensing(List.of(scope)),
                new OtherMechanicsLicensing(List.of(scope)), new OpenMechanicsLicensing(
                        new EstablishedReuseBasis("synthetic-open-basis", scope.scope(), scope.evidence(), scope.decision())));
        for (var licensing : rights) {
            var result = mapper.project(document(List.of(withLicensing(ac(), licensing))));
            assertEquals(licensing.status(), result.interpretations().getFirst().licensing().status());
            assertEquals(1, result.interpretations().size());
            String json = MechanicsJson.mapper().writeValueAsString(result);
            assertFalse(json.contains("fixture-rights-decision"));
            assertFalse(json.contains("test-only-rights-mandate"));
            assertFalse(json.contains("priorDeterminations"));
            assertEquals(result, MechanicsJson.mapper().readValue(json, SpellMechanicsInspectionDto.class));
        }
    }

    @Test
    void failedProcessingIsExplicitlyUnavailableWithOnlyAControlledDiagnostic() throws Exception {
        var failed = MechanicsDocument.create(PHB, 1, List.of(artifact("PHB")),
                new MechanicsProcessing(ProcessingState.FAILED, "synthetic-failure", "v1"),
                List.of(), List.of(), List.of(), List.of(),
                new MechanicsCoverage(CoverageStatus.NOT_ASSESSED, null, null, List.of(), List.of(), 0));
        var result = mapper.project(failed);
        assertEquals(MechanicsInspectionAvailability.UNAVAILABLE, result.availability());
        assertEquals(ProcessingState.FAILED, result.processing().state());
        assertEquals(List.of(MechanicsInspectionDiagnostic.INTERPRETATION_PROCESSING_FAILED), result.diagnostics());
        assertTrue(result.interpretations().isEmpty());
        var codec = MechanicsJson.mapper();
        assertEquals(result, codec.readValue(codec.writeValueAsBytes(result), SpellMechanicsInspectionDto.class));
    }

    @Test
    void evidenceKindsHaveNoFabricatedSpansOrFullSourceText() throws Exception {
        var original = shieldBody();
        var field = new StructuredFieldMechanicsEvidence("field", PHB.source(), original.syntax().location().owner(),
                "/spell/294/duration/0", List.of(), HASH, MechanicsOrigin.AUTHORED, PHB);
        var steps = new ArrayList<>(List.of(new MechanicsTransformationReference("fixture-step", "v1", "body")));
        var transformed = new TransformedMechanicsEvidence("transformed", PHB.source(), PHB, "/effective/entries/0", HASH,
                List.of(EvidenceRevisionReference.of(original)), steps);
        steps.clear();
        assertEquals(1, transformed.transformations().size());
        assertThrows(UnsupportedOperationException.class, () -> transformed.transformations().clear());
        var unknown = new UnavailableOriginMechanicsEvidence("unavailable", PHB.source(), PHB,
                "/effective/entries/1", HASH, UnknownValueReason.PROVENANCE_UNAVAILABLE);
        var result = mapper.project(document(PHB, List.of(), List.of(original, field, transformed, unknown), List.of(), List.of()));
        var codec = MechanicsJson.mapper();
        var json = codec.readTree(codec.writeValueAsBytes(result));
        assertEquals("TEXT_SPAN", json.path("evidence").get(0).path("kind").asText());
        assertTrue(json.path("evidence").get(0).has("span"));
        for (int index = 1; index < 4; index++) {
            assertFalse(json.path("evidence").get(index).has("span"));
        }
        assertFalse(json.toString().contains("valueSha256"));
        assertFalse(json.toString().contains("textSha256"));
        assertEquals(result, codec.readValue(codec.writeValueAsBytes(result), SpellMechanicsInspectionDto.class));
    }

    @Test
    void projectionCollectionsAreCopiedAndDeterministicallyOrdered() throws Exception {
        var original = mapper.project(document(List.of(ac())));
        var records = new ArrayList<>(original.interpretations());
        var copied = new SpellMechanicsInspectionDto(1, original.projection(), original.availability(), original.processing(),
                original.sourceRevisions(), records, original.observations(), original.unsupported(), original.evidence(),
                original.coverage(), original.reviewSummary(), original.diagnostics());
        records.clear();
        assertEquals(1, copied.interpretations().size());
        assertThrows(UnsupportedOperationException.class, () -> copied.interpretations().clear());
        assertThrows(UnsupportedOperationException.class, () -> copied.evidence().clear());
        assertThrows(UnsupportedOperationException.class, () -> copied.sourceRevisions().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> copied.interpretations().getFirst().evidenceAssociations().clear());
        assertEquals(MechanicsJson.mapper().writeValueAsString(original), MechanicsJson.mapper().writeValueAsString(copied));
    }

    @Test
    void unknownDtoKindsAndInconsistentAvailabilityCannotMasqueradeAsSuccessfulInspection() {
        var codec = MechanicsJson.mapper();
        assertThrows(Exception.class, () -> codec.readValue("{\"kind\":\"UNKNOWN_KIND\"}", EvidenceLocatorDto.class));
        assertThrows(Exception.class, () -> codec.readValue("{\"kind\":\"UNKNOWN_KIND\"}", MechanicsObservationDto.class));
        var result = mapper.project(document(List.of(ac())));
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsInspectionDto(
                1, result.projection(), MechanicsInspectionAvailability.UNAVAILABLE, result.processing(),
                result.sourceRevisions(), result.interpretations(), result.observations(), result.unsupported(),
                result.evidence(), result.coverage(), result.reviewSummary(), List.of()));
    }
}
