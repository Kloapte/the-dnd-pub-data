package com.thedndpub.data.mechanics.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.spell.SpellMechanicsDocumentFactory;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Candidate diagnostics only; pending expectations cannot establish independently validated accuracy. */
@SpringBootTest
class SpellExpansionCandidateBenchmarkTests {
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SpellFormulaAnnotationCatalog formulas;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void allFourRealOutputsReconcileWithoutPromotingFixturesOrHidingEmittedClaims() throws Exception {
        List<Resource> files = List.of(
                new ClassPathResource("mechanics/benchmarks/spells/v1/spell-phb-shield.json"),
                new ClassPathResource("mechanics/benchmarks/spells/v1/spell-xphb-shield.json"),
                new ClassPathResource("mechanics/benchmarks/spells/v1/spell-phb-fireball.json"),
                new ClassPathResource("mechanics/benchmarks/spells/v1/spell-phb-healing-word.json"));
        var scope = new ExtractionScope("spell-pilot-expansion", "1", List.of(
                new ExtractionPattern(MechanicFamily.REACTION_ACTIVATION, "shield-reaction-v1"),
                new ExtractionPattern(MechanicFamily.ARMOR_CLASS_MODIFIER, "shield-ac-v1"),
                new ExtractionPattern(MechanicFamily.DURATION, "spell-declared-duration-v1"),
                new ExtractionPattern(MechanicFamily.DURATION, "shield-turn-boundary-v1"),
                new ExtractionPattern(MechanicFamily.SPELL_DAMAGE_PREVENTION, "shield-special-interaction-v1"),
                new ExtractionPattern(MechanicFamily.SAVING_THROW_REQUIREMENT, "fireball-save-v1"),
                new ExtractionPattern(MechanicFamily.DAMAGE, "fireball-damage-v1"),
                new ExtractionPattern(MechanicFamily.HEALING, "healing-word-amount-v1")));
        var fixtures = BenchmarkFixtures.load(files, new SpellMechanicsDocumentFactory(mapper).loadOwners(),
                formulas.index(), BenchmarkReviewPolicy.none());
        var engine = new SpellMechanicsBenchmark(BenchmarkReviewPolicy.none());
        List<BenchmarkReport> reports = new ArrayList<>();
        for (var fixture : fixtures) {
            var actual = inspection.documents().stream()
                    .filter(document -> document.owner().id().equals(fixture.owner().id())).findFirst().orElseThrow();
            String before = actual.revision().contentSha256();
            var report = engine.compare(fixture, actual, scope, BenchmarkMode.CANDIDATE_VALIDATION);
            assertEquals(FixtureLifecycle.PENDING_HUMAN_REVIEW, fixture.review().state());
            assertEquals(BenchmarkRunState.CANDIDATE_DIAGNOSTICS, report.state());
            assertNull(report.accepted());
            assertEquals(fixture.mechanics().size(), report.truePositives());
            assertEquals(0, report.falseNegatives());
            assertEquals(0, report.falsePositives());
            assertTrue(report.claims().stream().allMatch(value -> value.semanticMatch() && value.evidenceMatch()));
            assertEquals(report.evidenceAccuracy().denominator(), report.evidenceAccuracy().correct());
            assertEquals(0, report.completenessErrors());
            assertEquals(0, report.forbiddenAssertions());
            assertEquals(0, report.duplicateEffects());
            assertEquals(0, report.unjustifiedAbstentions());
            assertEquals(0, report.scopeViolations());
            assertTrue(report.diagnostics().isEmpty(), report.diagnostics().toString());
            assertEquals(BenchmarkRunState.NO_APPROVED_GOLD_SET,
                    engine.compare(fixture, actual, scope, BenchmarkMode.APPROVED_EVALUATION).state());
            assertEquals(before, actual.revision().contentSha256());
            assertEquals(0, actual.reviewSummary().verified());
            assertTrue(actual.interpretations().stream().allMatch(value ->
                    value.review().humanDecision() == null && value.assessment().benchmarks().isEmpty()));
            reports.add(report);
        }
        Files.writeString(Path.of("target/7.3.3-candidate-benchmarks.json"),
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(BenchmarkReport.ordered(reports)) + "\n");
    }
}
