package com.thedndpub.data.mechanics.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.mechanics.model.AnyTrigger;
import com.thedndpub.data.mechanics.model.AttackEventKind;
import com.thedndpub.data.mechanics.model.HitByAttackTrigger;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.ReactionActivationMechanic;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.SpellDamagePreventionMechanic;
import com.thedndpub.data.mechanics.model.SpellMentionForm;
import com.thedndpub.data.mechanics.model.TargetedBySpellTrigger;
import com.thedndpub.data.mechanics.spell.SpellMechanicsDocumentFactory;
import com.thedndpub.data.mechanics.spell.SpellMechanicsSourceOwner;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpellBenchmarkFixtureTests {
    @Autowired
    private SpellFormulaAnnotationCatalog annotations;

    private static List<SpellBenchmarkFixture> proposed;

    @BeforeAll
    static void prepare() throws IOException {
        proposed = BenchmarkFixtureAuthoring.proposed();
    }

    private List<SpellBenchmarkFixture> load() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mechanics/benchmarks/spells/v1/*.json");
        return BenchmarkFixtures.load(Arrays.asList(resources), owners(), annotations.index(), BenchmarkReviewPolicy.none());
    }

    private List<SpellMechanicsSourceOwner> owners() {
        return new SpellMechanicsDocumentFactory(new ObjectMapper()).loadOwners();
    }

    @Test
    void allSixPinnedFixturesLoadAndRemainPending() throws IOException {
        List<SpellBenchmarkFixture> fixtures = load();
        assertEquals(6, fixtures.size());
        assertTrue(fixtures.stream().allMatch(value -> value.review().state() == FixtureLifecycle.PENDING_HUMAN_REVIEW));
        assertTrue(fixtures.stream().allMatch(value -> value.review().humanDecision() == null));
        assertEquals(0, fixtures.stream().filter(value -> value.review().state() == FixtureLifecycle.APPROVED).count());
    }

    @Test
    void committedFixturesReproduceExactlyAndRoundTrip() throws IOException {
        List<SpellBenchmarkFixture> loaded = load();
        assertEquals(proposed.stream().sorted(Comparator.comparing(value -> value.owner().id())).toList(), loaded);
        for (SpellBenchmarkFixture fixture : loaded) {
            byte[] bytes = MechanicsJson.mapper().writeValueAsBytes(fixture);
            assertEquals(fixture, BenchmarkFixtures.parse(bytes, BenchmarkReviewPolicy.none()));
            assertEquals(fixture.revision().contentSha256(), BenchmarkFixtures.fingerprint(fixture.content()));
        }
        assertThrows(UnsupportedOperationException.class, () -> loaded.clear());
    }

    @Test
    void sourceAndFormulaDenominatorsRemainUnchanged() throws IOException {
        assertEquals(969, owners().size());
        SpellFormulaAnnotationIndex index = annotations.index();
        assertEquals(930, index.annotations().size());
        assertEquals(422, index.bySpellId().values().stream().filter(value -> !value.isEmpty()).count());
        assertEquals(5, load().stream().mapToInt(value -> value.formulas().size()).sum());
    }

    @Test
    void editionsHaveDistinctAttackEventsReferencesAndEvidence() {
        var phb = proposed.get(0);
        var xphb = proposed.get(1);
        var oldReaction = (ReactionActivationMechanic) phb.mechanics().getFirst().payload();
        var newReaction = (ReactionActivationMechanic) xphb.mechanics().getFirst().payload();
        assertEquals(AttackEventKind.ATTACK_HIT,
                ((HitByAttackTrigger) ((AnyTrigger) oldReaction.trigger()).alternatives().getFirst()).event());
        assertEquals(AttackEventKind.ATTACK_ROLL_HIT,
                ((HitByAttackTrigger) ((AnyTrigger) newReaction.trigger()).alternatives().getFirst()).event());
        var trigger = ((TargetedBySpellTrigger) ((AnyTrigger) newReaction.trigger()).alternatives().get(1)).spell();
        assertNull(trigger.authoredSource());
        assertEquals(SpellMentionForm.PROSE_MENTION, trigger.form());
        var protection = (SpellDamagePreventionMechanic) xphb.mechanics().getLast().payload();
        assertEquals("XPHB", protection.spell().authoredSource().code());
        assertNotEquals(phb.source(), xphb.source());
        assertNotEquals(phb.ownerPointer(), xphb.ownerPointer());
    }

    @Test
    void noUnsupportedControlIsForcedIntoAFabricatedPayload() {
        assertEquals(5, proposed.get(0).mechanics().size());
        assertEquals(5, proposed.get(1).mechanics().size());
        assertTrue(proposed.get(4).mechanics().isEmpty());
        assertTrue(proposed.get(5).mechanics().isEmpty());
        assertEquals(8, proposed.get(5).deferred().size());
        assertTrue(proposed.get(2).mechanics().stream().allMatch(value -> value.completeness() == MechanicsCompleteness.PARTIAL));
    }

    @Test
    void changedSchemaUnknownFieldsAndDiscriminatorsFail() throws IOException {
        for (String field : List.of("schemaVersion", "unexpected")) {
            ObjectNode node = tree();
            node.put(field, "invalid");
            assertThrows(IOException.class, () -> BenchmarkFixtures.parse(node.toString().getBytes(StandardCharsets.UTF_8),
                    BenchmarkReviewPolicy.none()));
        }
        ObjectNode node = tree();
        ((ObjectNode) node.at("/mechanics/0/payload")).put("kind", "EXECUTABLE_EFFECT");
        assertThrows(IOException.class, () -> BenchmarkFixtures.parse(
                MechanicsJson.mapper().writeValueAsBytes(node), BenchmarkReviewPolicy.none()));
    }

    @Test
    void changedAssertionsAndRevisionAreRejected() throws IOException {
        ObjectNode node = tree();
        ((ObjectNode) node.at("/mechanics/1/payload/amount")).put("value", 6);
        byte[] changedAssertion = MechanicsJson.mapper().writeValueAsBytes(node);
        assertThrows(IOException.class, () -> BenchmarkFixtures.parse(changedAssertion, BenchmarkReviewPolicy.none()));
        node = tree();
        ((ObjectNode) node.get("revision")).put("number", 0);
        byte[] invalid = MechanicsJson.mapper().writeValueAsBytes(node);
        assertThrows(IOException.class, () -> BenchmarkFixtures.parse(invalid, BenchmarkReviewPolicy.none()));
    }

    @Test
    void missingDuplicateOrWrongSourceOwnersAreRejected() throws IOException {
        Resource resource = new ClassPathResource("mechanics/benchmarks/spells/v1/spell-phb-shield.json");
        List<SpellMechanicsSourceOwner> owners = owners();
        assertThrows(IllegalArgumentException.class, () -> BenchmarkFixtures.load(List.of(resource, resource), owners,
                annotations.index(), BenchmarkReviewPolicy.none()));
        assertThrows(IllegalArgumentException.class, () -> BenchmarkFixtures.load(List.of(resource), List.of(),
                annotations.index(), BenchmarkReviewPolicy.none()));
        var wrong = owners.stream().map(owner -> owner.id().value().equals("spell:phb:shield")
                ? new SpellMechanicsSourceOwner(owner.id(), owner.source(),
                new SourceArtifactRevision(owner.revision().datasetVersion(), owner.revision().resourcePath(),
                        MechanicsHashes.sha256("changed source")), owner.jsonPointer()) : owner).toList();
        assertThrows(IllegalArgumentException.class, () -> BenchmarkFixtures.load(List.of(resource), wrong,
                annotations.index(), BenchmarkReviewPolicy.none()));
    }

    @Test
    void absenceOfApprovedGoldDoesNotFailTheBuildOrBecomeAcceptance() throws IOException {
        var engine = new SpellMechanicsBenchmark(BenchmarkReviewPolicy.none());
        for (SpellBenchmarkFixture fixture : load()) {
            var report = engine.compare(fixture, BenchmarkTestDocuments.empty(fixture), BenchmarkTestDocuments.scope(fixture),
                    BenchmarkMode.APPROVED_EVALUATION);
            assertEquals(BenchmarkRunState.NO_APPROVED_GOLD_SET, report.state());
            assertNull(report.accepted());
            assertEquals(MetricAvailability.NOT_MEASURED, report.semanticRecall().availability());
        }
    }

    @Test
    void duplicateAndCyclicExpectedClaimsAreRejectedBeforeEvaluation() {
        var fixture = proposed.getFirst();
        assertThrows(IllegalArgumentException.class, () -> BenchmarkTestDocuments.withContent(fixture,
                List.of(fixture.mechanics().getFirst(), fixture.mechanics().getFirst()), List.of()));
        var activation = fixture.mechanics().getFirst();
        var selfDependent = new ExpectedMechanic(activation.key(), activation.family(), activation.patternId(),
                activation.payload(), new ExpectedContext(activation.context().subject(),
                List.of(new ExpectedActivation(activation.key()))), activation.completeness(), activation.evidence());
        assertThrows(IllegalArgumentException.class, () -> BenchmarkTestDocuments.withContent(fixture,
                List.of(selfDependent), List.of()));
        var association = activation.evidence().getFirst();
        assertThrows(IllegalArgumentException.class, () -> new ExpectedMechanic(activation.key(), activation.family(),
                activation.patternId(), activation.payload(), activation.context(), activation.completeness(),
                List.of(new ExpectedEvidence(association.evidence(), "/payload/nonexistent", association.role()))));
    }

    private ObjectNode tree() {
        JsonNode node = MechanicsJson.mapper().valueToTree(proposed.getFirst());
        return (ObjectNode) node;
    }
}
