package com.thedndpub.data.mechanics.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
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

/** Real extraction compared to unchanged proposals. These diagnostics are not independent gold accuracy. */
@SpringBootTest
class ShieldMechanicsCandidateBenchmarkTests {
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SpellFormulaAnnotationCatalog formulas;
    @Autowired
    private ObjectMapper mapper;

    private ExtractionScope scope() {
        return new ExtractionScope("shield-pilot", "1", List.of(
                new ExtractionPattern(MechanicFamily.REACTION_ACTIVATION, "shield-reaction-v1"),
                new ExtractionPattern(MechanicFamily.ARMOR_CLASS_MODIFIER, "shield-ac-v1"),
                new ExtractionPattern(MechanicFamily.DURATION, "spell-declared-duration-v1"),
                new ExtractionPattern(MechanicFamily.DURATION, "shield-turn-boundary-v1"),
                new ExtractionPattern(MechanicFamily.SPELL_DAMAGE_PREVENTION, "shield-special-interaction-v1")));
    }

    @Test
    void bothRealExtractorDocumentsMatchThePendingClaimsAndEvidenceWithoutGrantingApproval() throws Exception {
        List<Resource> files = List.of(
                new ClassPathResource("mechanics/benchmarks/spells/v1/spell-phb-shield.json"),
                new ClassPathResource("mechanics/benchmarks/spells/v1/spell-xphb-shield.json"));
        var fixtures = BenchmarkFixtures.load(files, new SpellMechanicsDocumentFactory(mapper).loadOwners(),
                formulas.index(), BenchmarkReviewPolicy.none());
        var engine = new SpellMechanicsBenchmark(BenchmarkReviewPolicy.none());
        List<BenchmarkReport> reports = new ArrayList<>();
        for (SpellBenchmarkFixture fixture : fixtures) {
            assertEquals(FixtureLifecycle.PENDING_HUMAN_REVIEW, fixture.review().state());
            MechanicsDocument actual = inspection.documents().stream()
                    .filter(document -> document.owner().id().equals(fixture.owner().id())).findFirst().orElseThrow();
            String before = actual.revision().contentSha256();
            BenchmarkReport report = engine.compare(fixture, actual, scope(), BenchmarkMode.CANDIDATE_VALIDATION);
            assertEquals(BenchmarkRunState.CANDIDATE_DIAGNOSTICS, report.state());
            assertNull(report.accepted());
            assertEquals(5, report.truePositives());
            assertEquals(0, report.falseNegatives());
            assertEquals(0, report.falsePositives());
            assertEquals(17, report.evidenceAccuracy().denominator());
            assertEquals(17, report.evidenceAccuracy().correct());
            assertTrue(report.claims().stream().allMatch(BenchmarkClaimResult::evidenceMatch));
            assertEquals(0, report.completenessErrors());
            assertEquals(0, report.forbiddenAssertions());
            assertEquals(0, report.duplicateEffects());
            assertEquals(0, report.unjustifiedAbstentions());
            assertEquals(0, report.scopeViolations());
            assertEquals(BenchmarkRunState.NO_APPROVED_GOLD_SET,
                    engine.compare(fixture, actual, scope(), BenchmarkMode.APPROVED_EVALUATION).state());
            assertEquals(before, actual.revision().contentSha256());
            assertEquals(0, actual.reviewSummary().verified());
            assertTrue(actual.interpretations().stream().allMatch(value ->
                    value.assessment().benchmarks().isEmpty() && value.review().humanDecision() == null));
            reports.add(report);
        }
        Path output = Path.of("target/7.3.2-candidate-benchmarks.json");
        Files.createDirectories(output.getParent());
        Files.writeString(output, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(BenchmarkReport.ordered(reports)) + "\n");
    }
}
