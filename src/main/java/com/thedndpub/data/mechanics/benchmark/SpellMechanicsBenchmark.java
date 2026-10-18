package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Compares supplied claims only. Does not interpret source data or write mechanics/review decisions. */
public final class SpellMechanicsBenchmark {
    private final BenchmarkReviewPolicy reviewPolicy;

    public SpellMechanicsBenchmark(BenchmarkReviewPolicy reviewPolicy) {
        this.reviewPolicy = ModelChecks.required(reviewPolicy, "reviewPolicy");
    }

    public BenchmarkReport compare(
            SpellBenchmarkFixture fixture,
            MechanicsDocument actual,
            ExtractionScope scope,
            BenchmarkMode mode) {
        ModelChecks.required(mode, "mode");
        ModelChecks.required(scope, "scope");
        reviewPolicy.validate(fixture);
        ModelChecks.require(fixture.owner().equals(actual.owner()), "Benchmark owner/edition mismatch");
        ModelChecks.require(actual.sources().contains(fixture.source()), "Benchmark source revision mismatch");
        if (mode == BenchmarkMode.APPROVED_EVALUATION && fixture.review().state() != FixtureLifecycle.APPROVED) {
            return unavailable(fixture, scope);
        }
        ModelChecks.require(mode != BenchmarkMode.CANDIDATE_VALIDATION
                        || fixture.review().state() == FixtureLifecycle.DRAFT
                        || fixture.review().state() == FixtureLifecycle.PENDING_HUMAN_REVIEW,
                "Candidate validation only accepts draft or pending fixtures");

        Map<String, MechanicsEvidence> evidence = new HashMap<>();
        actual.evidence().forEach(value -> evidence.put(value.id(), value));
        List<MechanicRecord> records = actual.interpretations().stream()
                .sorted(Comparator.comparing(value -> value.id().value())).toList();
        List<ExpectedMechanic> expected = fixture.mechanics().stream()
                .sorted(Comparator.comparing(ExpectedMechanic::key)).toList();
        Map<String, MechanicRecord> assigned = assign(expected, records);
        List<BenchmarkClaimResult> results = new ArrayList<>();
        List<String> diagnostics = new ArrayList<>();
        Set<MechanicRecord> matched = new HashSet<>();
        int eligibleExpected = 0;
        int missing = 0;
        int completenessErrors = 0;
        long correctEvidence = 0;
        long actualEvidence = records.stream().mapToLong(record -> record.evidenceAssociations().size()).sum();

        for (ExpectedMechanic expectation : expected) {
            var pattern = new ExtractionPattern(expectation.family(), expectation.patternId());
            boolean eligible = scope.includes(pattern);
            MechanicRecord record = assigned.get(expectation.key());
            if (!eligible) {
                results.add(new BenchmarkClaimResult(expectation.key(), pattern, false, false, false, List.of("OUT_OF_SCOPE")));
                continue;
            }
            eligibleExpected++;
            List<String> findings = new ArrayList<>();
            boolean semantic = record != null && expectation.payload().equals(record.payload())
                    && BenchmarkSemantics.contextMatches(expectation.context(), record.context(), assigned)
                    && expectation.completeness() == record.assessment().completeness()
                    && record.derivation().sources().contains(fixture.source());
            if (record != null) {
                if (!expectation.payload().equals(record.payload())) {
                    findings.add("INCORRECT_PAYLOAD");
                }
                if (!BenchmarkSemantics.contextMatches(expectation.context(), record.context(), assigned)) {
                    findings.add("MISSING_OR_INCORRECT_CONTEXT_QUALIFIER");
                    completenessErrors++;
                }
                if (expectation.completeness() != record.assessment().completeness()) {
                    findings.add("INCORRECT_CLAIM_COMPLETENESS");
                    completenessErrors++;
                }
                if (!record.derivation().sources().contains(fixture.source())) {
                    findings.add("MISSING_DERIVATION_SOURCE_REVISION");
                }
            } else {
                findings.add("MISSING_EXPECTED_CLAIM");
            }
            if (semantic) {
                matched.add(record);
            } else {
                missing++;
            }
            long correct = record == null ? 0 : correctAssociations(expectation, record, evidence);
            correctEvidence += correct;
            boolean exactEvidence = record != null && correct == expectation.evidence().size()
                    && correct == record.evidenceAssociations().size()
                    && bindingEvidenceMatches(expectation, record, evidence);
            if (!exactEvidence) {
                findings.add("MISSING_OR_INCORRECT_EVIDENCE");
            }
            results.add(new BenchmarkClaimResult(expectation.key(), pattern, true, semantic, exactEvidence, findings));
        }

        int assertedEligible = 0;
        int scopeViolations = 0;
        for (MechanicRecord record : records) {
            if (scope.includes(new ExtractionPattern(BenchmarkSemantics.family(record.payload()), record.derivation().patternId()))) {
                assertedEligible++;
            } else {
                scopeViolations++;
                diagnostics.add("OUT_OF_SCOPE_ASSERTION:" + record.id().value());
            }
        }
        // Semantic matches must also have the declared extraction pattern; a different pattern is not silently credited.
        matched.removeIf(record -> !scope.includes(new ExtractionPattern(
                BenchmarkSemantics.family(record.payload()), record.derivation().patternId())));
        int duplicates = duplicates(records);
        int forbidden = prohibitions(fixture, records, matched, evidence, diagnostics);
        int honored = 0;
        Set<UnsupportedMechanicsConstruct> accounted = new HashSet<>();
        for (DeferredExpectation deferred : fixture.deferred()) {
            var matches = actual.unsupported().stream().filter(value -> value.candidateFamily() == deferred.candidateFamily()
                    && value.reason() == deferred.expectedReason()
                    && deferred.evidence().stream().allMatch(expectedEvidence -> value.evidenceIds().stream()
                    .anyMatch(id -> BenchmarkSemantics.evidenceMatches(expectedEvidence, evidence.get(id))))).toList();
            if (matches.size() == 1 && accounted.add(matches.getFirst())) {
                honored++;
            } else {
                diagnostics.add("UNREPORTED_OR_AMBIGUOUS_ABSTENTION:" + deferred.id());
            }
        }
        int unjustified = missing + actual.unsupported().size() - accounted.size();
        if (actual.coverage().status() == CoverageStatus.COMPLETE_WITHIN_SCOPE
                && (missing > 0 || !fixture.deferred().isEmpty() || scopeViolations > 0
                || results.stream().anyMatch(value -> value.eligible() && !value.evidenceMatch()))) {
            completenessErrors++;
            diagnostics.add("FALSE_COMPLETE_COVERAGE");
        }
        int formulas = (int) fixture.formulas().stream()
                .filter(value -> actual.observations().contains(value.reference())).count();
        if (formulas != fixture.formulas().size()) {
            diagnostics.add("MISSING_OR_INCORRECT_FORMULA_CONTROL");
        }
        results.sort(Comparator.comparing((BenchmarkClaimResult value) -> value.pattern().family().name())
                .thenComparing(value -> value.pattern().patternId()).thenComparing(BenchmarkClaimResult::claimKey));
        diagnostics.sort(String::compareTo);
        boolean approved = mode == BenchmarkMode.APPROVED_EVALUATION;
        boolean acceptable = missing == 0 && matched.size() == records.size() && forbidden == 0 && duplicates == 0
                && scopeViolations == 0 && completenessErrors == 0 && honored == fixture.deferred().size()
                && unjustified == 0 && formulas == fixture.formulas().size()
                && results.stream().filter(BenchmarkClaimResult::eligible).allMatch(BenchmarkClaimResult::evidenceMatch)
                && actual.processing().state() == ProcessingState.PROCESSED;
        return new BenchmarkReport(fixture.id(), fixture.revision(), fixture.owner(), scope,
                approved ? BenchmarkRunState.APPROVED_EVALUATION : BenchmarkRunState.CANDIDATE_DIAGNOSTICS,
                BenchmarkMetric.of(matched.size(), assertedEligible), BenchmarkMetric.of(matched.size(), eligibleExpected),
                BenchmarkMetric.of(correctEvidence, actualEvidence), matched.size(), assertedEligible - matched.size(),
                eligibleExpected - matched.size(), fixture.deferred().size(), honored, unjustified, forbidden,
                completenessErrors, duplicates, scopeViolations, formulas,
                patternResults(expected, records, assigned, matched, evidence, scope),
                results, diagnostics, approved ? acceptable : null);
    }

    private static List<BenchmarkPatternResult> patternResults(
            List<ExpectedMechanic> expected,
            List<MechanicRecord> records,
            Map<String, MechanicRecord> assigned,
            Set<MechanicRecord> matched,
            Map<String, MechanicsEvidence> evidence,
            ExtractionScope scope) {
        Set<ExtractionPattern> groups = new HashSet<>(scope.patterns());
        expected.forEach(value -> groups.add(new ExtractionPattern(value.family(), value.patternId())));
        records.forEach(value -> groups.add(new ExtractionPattern(
                BenchmarkSemantics.family(value.payload()), value.derivation().patternId())));
        List<BenchmarkPatternResult> result = new ArrayList<>();
        for (ExtractionPattern group : groups.stream().sorted(Comparator
                .comparing((ExtractionPattern value) -> value.family().name())
                .thenComparing(ExtractionPattern::patternId)).toList()) {
            boolean eligible = scope.includes(group);
            List<MechanicRecord> asserted = records.stream().filter(value ->
                    BenchmarkSemantics.family(value.payload()) == group.family()
                            && value.derivation().patternId().equals(group.patternId())).toList();
            List<ExpectedMechanic> proposed = expected.stream().filter(value ->
                    value.family() == group.family() && value.patternId().equals(group.patternId())).toList();
            long correct = asserted.stream().filter(matched::contains).count();
            long correctEvidence = eligible ? proposed.stream().mapToLong(value -> assigned.containsKey(value.key())
                    ? correctAssociations(value, assigned.get(value.key()), evidence) : 0).sum() : 0;
            result.add(new BenchmarkPatternResult(group, eligible,
                    BenchmarkMetric.of(correct, eligible ? asserted.size() : 0),
                    BenchmarkMetric.of(correct, eligible ? proposed.size() : 0),
                    BenchmarkMetric.of(correctEvidence, asserted.stream().mapToLong(value -> value.evidenceAssociations().size()).sum())));
        }
        return List.copyOf(result);
    }

    private static Map<String, MechanicRecord> assign(List<ExpectedMechanic> expected, List<MechanicRecord> records) {
        Map<String, MechanicRecord> assigned = new LinkedHashMap<>();
        Set<MechanicRecord> used = new HashSet<>();
        // Resolve context-exact candidates in dependency order before assigning diagnostic near-matches.
        boolean progress;
        do {
            progress = false;
            for (ExpectedMechanic expectation : expected) {
                if (assigned.containsKey(expectation.key())) {
                    continue;
                }
                var candidate = records.stream().filter(value -> !used.contains(value)
                                && value.derivation().patternId().equals(expectation.patternId())
                                && expectation.payload().equals(value.payload())
                                && BenchmarkSemantics.contextMatches(expectation.context(), value.context(), assigned))
                        .findFirst();
                if (candidate.isPresent()) {
                    assigned.put(expectation.key(), candidate.get());
                    used.add(candidate.get());
                    progress = true;
                }
            }
        } while (progress);
        // Remaining same-family claims receive diagnostics rather than disappearing.
        for (boolean exact : List.of(true, false)) {
            for (ExpectedMechanic expectation : expected) {
                if (assigned.containsKey(expectation.key())) {
                    continue;
                }
                records.stream().filter(value -> !used.contains(value)
                                && BenchmarkSemantics.family(value.payload()) == expectation.family()
                                && value.derivation().patternId().equals(expectation.patternId())
                                && (!exact || expectation.payload().equals(value.payload())))
                        .findFirst().ifPresent(value -> {
                            assigned.put(expectation.key(), value);
                            used.add(value);
                        });
            }
        }
        return assigned;
    }

    private static long correctAssociations(ExpectedMechanic expected, MechanicRecord actual,
                                            Map<String, MechanicsEvidence> evidence) {
        Set<Integer> used = new HashSet<>();
        long correct = 0;
        for (MechanicEvidenceAssociation association : actual.evidenceAssociations()) {
            for (int index = 0; index < expected.evidence().size(); index++) {
                ExpectedEvidence expectation = expected.evidence().get(index);
                if (!used.contains(index) && expectation.claimPath().equals(association.claimPath())
                        && expectation.role() == association.role()
                        && BenchmarkSemantics.evidenceMatches(expectation.evidence(), evidence.get(association.evidenceId()))
                        && association.evidenceSha256().equals(MechanicsHashes.evidenceHash(evidence.get(association.evidenceId())))
                        && associationBoundToContext(actual, association)) {
                    used.add(index);
                    correct++;
                    break;
                }
            }
        }
        return correct;
    }

    private static boolean associationBoundToContext(MechanicRecord record, MechanicEvidenceAssociation association) {
        String prefix = "/context/bindings/";
        if (!association.claimPath().startsWith(prefix)) {
            return true;
        }
        String component = association.claimPath().substring(prefix.length()).split("/", 2)[0];
        int index = Integer.parseInt(component);
        return index < record.context().bindings().size()
                && BenchmarkSemantics.bindingEvidence(record.context().bindings().get(index)).contains(association.evidenceId());
    }

    private static boolean bindingEvidenceMatches(ExpectedMechanic expected, MechanicRecord actual,
                                                   Map<String, MechanicsEvidence> evidence) {
        for (int index = 0; index < actual.context().bindings().size(); index++) {
            String path = "/context/bindings/" + index;
            List<ExpectedEvidence> locations = expected.evidence().stream()
                    .filter(value -> value.claimPath().equals(path) || value.claimPath().startsWith(path + "/")).toList();
            List<String> ids = BenchmarkSemantics.bindingEvidence(actual.context().bindings().get(index));
            if (locations.isEmpty() || ids.size() != locations.size()
                    || locations.stream().anyMatch(value -> ids.stream().noneMatch(id ->
                    BenchmarkSemantics.evidenceMatches(value.evidence(), evidence.get(id))))) {
                return false;
            }
        }
        return true;
    }

    private static int duplicates(List<MechanicRecord> records) {
        int result = 0;
        for (int index = 0; index < records.size(); index++) {
            MechanicRecord record = records.get(index);
            if (records.subList(0, index).stream().anyMatch(other -> other.payload().equals(record.payload())
                    && BenchmarkSemantics.equivalentContext(other.context(), record.context()))) {
                result++;
            }
        }
        return result;
    }

    private static int prohibitions(SpellBenchmarkFixture fixture, List<MechanicRecord> records,
                                    Set<MechanicRecord> consumed, Map<String, MechanicsEvidence> evidence,
                                    List<String> diagnostics) {
        int violations = 0;
        for (BenchmarkProhibition prohibition : fixture.prohibitions()) {
            for (MechanicRecord record : records) {
                boolean violates = switch (prohibition.rule()) {
                    case NO_FAMILY -> BenchmarkSemantics.family(record.payload()) == prohibition.family();
                    case NO_RESOLVED_SPELL_REFERENCES -> BenchmarkSemantics.hasResolvedReference(record.payload());
                    case NO_UNEXPECTED_CLAIMS -> !consumed.contains(record);
                    case NO_FOREIGN_SOURCE_EVIDENCE -> record.evidenceAssociations().stream().anyMatch(association -> {
                        MechanicsEvidence value = evidence.get(association.evidenceId());
                        return value == null || !value.source().equals(fixture.owner().source());
                    });
                };
                if (violates) {
                    violations++;
                    diagnostics.add("FORBIDDEN:" + prohibition.id() + ":" + record.id().value());
                }
            }
        }
        return violations;
    }

    private static BenchmarkReport unavailable(SpellBenchmarkFixture fixture, ExtractionScope scope) {
        BenchmarkMetric none = BenchmarkMetric.of(0, 0);
        return new BenchmarkReport(fixture.id(), fixture.revision(), fixture.owner(), scope,
                BenchmarkRunState.NO_APPROVED_GOLD_SET, none, none, none,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, List.of(), List.of(), List.of("NO_APPROVED_GOLD_SET"), null);
    }
}
