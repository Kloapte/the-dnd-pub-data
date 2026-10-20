package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.CatalogDomain;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record SpellBenchmarkFixture(
        String schemaVersion,
        String id,
        FixtureRevision revision,
        MechanicsOwnerReference owner,
        SourceArtifactRevision source,
        String ownerPointer,
        List<ExpectedMechanic> mechanics,
        List<ExpectedFormula> formulas,
        List<DeferredExpectation> deferred,
        List<BenchmarkProhibition> prohibitions,
        FixtureReview review) {
    public SpellBenchmarkFixture {
        ModelChecks.require("1".equals(schemaVersion), "Unsupported benchmark schema version");
        ModelChecks.text(id, "id");
        ModelChecks.required(owner, "owner");
        ModelChecks.require(owner.domain() == CatalogDomain.SPELL, "Spell benchmark requires a Spell owner");
        ModelChecks.required(source, "source");
        ModelChecks.require(ownerPointer != null && ownerPointer.matches("/spell/[0-9]+"),
                "An authored Spell root pointer is required");
        mechanics = ModelChecks.list(mechanics);
        formulas = ModelChecks.list(formulas);
        deferred = ModelChecks.list(deferred);
        prohibitions = ModelChecks.list(prohibitions);
        ModelChecks.required(review, "review");
        ModelChecks.required(revision, "revision");
        var keys = new HashSet<String>();
        for (ExpectedMechanic mechanic : mechanics) {
            ModelChecks.require(keys.add(mechanic.key()), "Duplicate expected mechanic key");
        }
        for (ExpectedMechanic mechanic : mechanics) {
            for (ExpectedBinding binding : mechanic.context().bindings()) {
                String dependency = switch (binding) {
                    case ExpectedActivation activation -> activation.claimKey();
                    case ExpectedSaveOutcome outcome -> outcome.claimKey();
                    default -> null;
                };
                ModelChecks.require(dependency == null || keys.contains(dependency), "Unknown expected dependency");
                if (dependency != null) {
                    MechanicFamily required = binding instanceof ExpectedActivation
                            ? MechanicFamily.REACTION_ACTIVATION : MechanicFamily.SAVING_THROW_REQUIREMENT;
                    ModelChecks.require(mechanics.stream().anyMatch(value ->
                            value.key().equals(dependency) && value.family() == required),
                            "Expected context dependency has the wrong mechanic family");
                }
            }
        }
        for (ExpectedMechanic mechanic : mechanics) {
            validateAcyclic(mechanic.key(), mechanics, new HashSet<>());
        }
        ModelChecks.require(formulas.stream().map(value -> value.reference().id()).distinct().count() == formulas.size(),
                "Duplicate expected observation");
        ModelChecks.require(deferred.stream().map(DeferredExpectation::id).distinct().count() == deferred.size(),
                "Duplicate deferred expectation");
        ModelChecks.require(prohibitions.stream().map(BenchmarkProhibition::id).distinct().count() == prohibitions.size(),
                "Duplicate prohibition");
        var content = new FixtureContent(schemaVersion, id, owner, source, ownerPointer,
                mechanics, formulas, deferred, prohibitions);
        ModelChecks.require(BenchmarkFixtures.fingerprint(content).equals(revision.contentSha256()),
                "Fixture revision does not bind its assertions and evidence");
    }

    public FixtureContent content() {
        return new FixtureContent(schemaVersion, id, owner, source, ownerPointer,
                mechanics, formulas, deferred, prohibitions);
    }

    private static void validateAcyclic(String key, List<ExpectedMechanic> claims, Set<String> visiting) {
        ModelChecks.require(visiting.add(key), "Cyclic expected context dependencies");
        ExpectedMechanic claim = claims.stream().filter(value -> value.key().equals(key)).findFirst().orElseThrow();
        for (ExpectedBinding binding : claim.context().bindings()) {
            String dependency = switch (binding) {
                case ExpectedActivation activation -> activation.claimKey();
                case ExpectedSaveOutcome outcome -> outcome.claimKey();
                default -> null;
            };
            if (dependency != null) {
                validateAcyclic(dependency, claims, visiting);
            }
        }
        visiting.remove(key);
    }
}
