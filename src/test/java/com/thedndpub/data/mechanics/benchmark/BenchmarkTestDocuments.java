package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ActivatedBy;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.DerivationMethod;
import com.thedndpub.data.mechanics.model.IncludesTriggeringAttack;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicContextBinding;
import com.thedndpub.data.mechanics.model.MechanicDerivation;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsAssessment;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsObservationReference;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.MechanicsSupport;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.model.UnsupportedReason;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Synthetic actual documents for comparator tests. These are NOT extracted or reviewed Spell mechanics. */
final class BenchmarkTestDocuments {
    private BenchmarkTestDocuments() {
    }

    static ExtractionScope scope(SpellBenchmarkFixture fixture) {
        return new ExtractionScope("synthetic-comparator-test", "1", fixture.mechanics().stream()
                .map(value -> new ExtractionPattern(value.family(), value.patternId())).distinct().toList());
    }

    static MechanicsDocument materialize(SpellBenchmarkFixture fixture) {
        Map<String, MechanicRecord> records = new LinkedHashMap<>();
        List<ExpectedMechanic> remaining = new ArrayList<>(fixture.mechanics());
        while (!remaining.isEmpty()) {
            int previous = remaining.size();
            for (ExpectedMechanic expected : List.copyOf(remaining)) {
                List<MechanicContextBinding> bindings = bindings(expected, records);
                if (bindings == null) {
                    continue;
                }
                var context = new MechanicContext(expected.context().subject(), bindings);
                records.put(expected.key(), record(fixture, expected, expected.payload(), context, expected.evidence()));
                remaining.remove(expected);
            }
            if (remaining.size() == previous) {
                throw new IllegalArgumentException("Synthetic fixture contains a dependency cycle");
            }
        }
        List<UnsupportedMechanicsConstruct> unsupported = fixture.deferred().stream().map(value ->
                new UnsupportedMechanicsConstruct(value.id(), value.candidateFamily(), UnsupportedReason.FAMILY_NOT_IMPLEMENTED,
                        value.evidence().stream().map(MechanicsEvidence::id).toList(), List.of())).toList();
        return document(fixture, List.copyOf(records.values()), evidence(fixture), unsupported, CoverageStatus.PARTIAL);
    }

    private static List<MechanicContextBinding> bindings(ExpectedMechanic expected, Map<String, MechanicRecord> records) {
        List<MechanicContextBinding> result = new ArrayList<>();
        for (int index = 0; index < expected.context().bindings().size(); index++) {
            String prefix = "/context/bindings/" + index;
            List<String> ids = expected.evidence().stream()
                    .filter(value -> value.claimPath().equals(prefix) || value.claimPath().startsWith(prefix + "/"))
                    .map(value -> value.evidence().id()).toList();
            ExpectedBinding binding = expected.context().bindings().get(index);
            if (binding instanceof ExpectedActivation activation && !records.containsKey(activation.claimKey())
                    || binding instanceof ExpectedSaveOutcome outcome && !records.containsKey(outcome.claimKey())) {
                return null;
            }
            result.add(switch (binding) {
                case ExpectedActivation activation -> new ActivatedBy(reference(records.get(activation.claimKey())), ids);
                case ExpectedDuration duration -> new AppliesForDuration(duration.duration(), ids);
                case ExpectedSaveOutcome outcome -> outcome.damageOutcome() == null
                        ? new AppliesOnSaveOutcome(reference(records.get(outcome.claimKey())), outcome.outcome(), ids)
                        : new AppliesOnSaveOutcome(reference(records.get(outcome.claimKey())), outcome.outcome(),
                                outcome.damageOutcome(), ids);
                case ExpectedTriggeringAttack ignored -> new IncludesTriggeringAttack(ids);
            });
        }
        return List.copyOf(result);
    }

    private static MechanicRevisionReference reference(MechanicRecord record) {
        return record.revision().reference(record.id());
    }

    static MechanicRecord record(SpellBenchmarkFixture fixture, ExpectedMechanic expected,
                                 MechanicPayload payload, MechanicContext context, List<ExpectedEvidence> evidence) {
        return MechanicRecord.propose(MechanicId.of(fixture.owner(), expected.key()), payload, context,
                evidence.stream().map(value -> MechanicEvidenceAssociation.of(value.evidence(), value.claimPath(), value.role())).toList(),
                new MechanicDerivation(DerivationMethod.PROSE_INTERPRETATION, expected.patternId(), "1",
                        "synthetic-comparator-test-only", List.of(), List.of(fixture.source())),
                new MechanicsAssessment(expected.completeness(), MechanicsSupport.PROSE_INTERPRETATION, List.of(), List.of()));
    }

    static List<MechanicsEvidence> evidence(SpellBenchmarkFixture fixture) {
        Map<String, MechanicsEvidence> evidence = new LinkedHashMap<>();
        fixture.mechanics().forEach(claim -> claim.evidence().forEach(value -> evidence.put(value.evidence().id(), value.evidence())));
        fixture.deferred().forEach(value -> value.evidence().forEach(item -> evidence.put(item.id(), item)));
        return List.copyOf(evidence.values());
    }

    static MechanicsDocument document(SpellBenchmarkFixture fixture, List<MechanicRecord> records,
                                      List<MechanicsEvidence> evidence, List<UnsupportedMechanicsConstruct> unsupported,
                                      CoverageStatus coverage) {
        List<MechanicsObservationReference> observations = fixture.formulas().stream()
                .map(value -> (MechanicsObservationReference) value.reference()).toList();
        return MechanicsDocument.create(fixture.owner(), 1, List.of(fixture.source()),
                new MechanicsProcessing(ProcessingState.PROCESSED, "synthetic-comparator-test-only", "1"),
                records, observations, unsupported, evidence,
                new MechanicsCoverage(coverage, "synthetic-comparator-test-only", "1", List.of(), List.of(), unsupported.size()));
    }

    static MechanicsDocument empty(SpellBenchmarkFixture fixture) {
        return MechanicsDocument.create(fixture.owner(), 1, List.of(fixture.source()),
                new MechanicsProcessing(ProcessingState.NOT_ATTEMPTED, null, null), List.of(), List.of(), List.of(), List.of(),
                new MechanicsCoverage(CoverageStatus.NOT_ASSESSED, null, null, List.of(), List.of(), 0));
    }

    static SpellBenchmarkFixture withContent(SpellBenchmarkFixture original, List<ExpectedMechanic> claims,
                                             List<DeferredExpectation> deferred) {
        var content = new FixtureContent(original.schemaVersion(), original.id(), original.owner(), original.source(),
                original.ownerPointer(), claims, original.formulas(), deferred, original.prohibitions());
        return new SpellBenchmarkFixture(content.schemaVersion(), content.id(),
                new FixtureRevision(original.revision().number() + 1, BenchmarkFixtures.fingerprint(content)),
                content.owner(), content.source(), content.ownerPointer(), claims, content.formulas(), deferred,
                content.prohibitions(), new FixtureReview(FixtureLifecycle.PENDING_HUMAN_REVIEW, null));
    }
}
