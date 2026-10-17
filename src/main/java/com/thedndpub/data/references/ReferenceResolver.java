package com.thedndpub.data.references;

import com.thedndpub.data.services.SourceRegistry;

import java.util.List;

public final class ReferenceResolver {
    private final AdapterRegistry adapters;
    private final SourceRegistry sourceRegistry;

    public ReferenceResolver(AdapterRegistry adapters, SourceRegistry sourceRegistry) {
        this.adapters = adapters;
        this.sourceRegistry = sourceRegistry;
    }

    public ResolutionResult resolve(AuthoredReference reference) {
        if (reference.parsedSource() != null) {
            validateSource(reference.parsedSource());
        }
        CatalogTargetAdapter adapter = adapters.adapter(reference.targetKind());
        List<TargetIdentity> candidates = candidates(reference, adapter);
        CandidateCardinality cardinality = cardinality(candidates.size());

        if (reference.semanticForm() != ReferenceSemanticForm.DEFINITION) {
            return result(reference, ResolutionOutcome.DEFERRED, cardinality, null,
                    null, candidates, reference.deferredReason());
        }
        if (adapter == null) {
            return result(reference, ResolutionOutcome.DEFERRED, cardinality, null, null, candidates,
                    "No catalog adapter is registered for this target kind");
        }

        if (reference.parsedSource() != null) {
            validateSource(reference.parsedSource());
            List<TargetIdentity> exact = adapter.exact(reference.parsedName(), canonicalSource(reference.parsedSource()));
            if (exact.size() > 1) {
                throw new IllegalStateException("Target adapter returned multiple exact identities for "
                        + reference.originalToken());
            }
            if (exact.isEmpty()) {
                return result(reference, ResolutionOutcome.UNRESOLVED, CandidateCardinality.ZERO,
                        null, null, List.of(), "No exact target matches the authored source-qualified identity");
            }
            return result(reference, ResolutionOutcome.RESOLVED, CandidateCardinality.ONE,
                    exact.getFirst().stableId(), exact.getFirst().targetKind(), exact,
                    "Exact source-qualified identity matched one catalog definition");
        }

        return switch (candidates.size()) {
            case 0 -> result(reference, ResolutionOutcome.UNRESOLVED, CandidateCardinality.ZERO,
                    null, null, List.of(), "No target candidate matches the unqualified authored name");
            case 1 -> result(reference, ResolutionOutcome.DEFERRED, CandidateCardinality.ONE,
                    null, null, candidates, "One current candidate exists, but source identity was not authored");
            default -> result(reference, ResolutionOutcome.AMBIGUOUS, CandidateCardinality.MANY,
                    null, null, candidates, "Multiple source-aware candidates match the unqualified authored name");
        };
    }

    private List<TargetIdentity> candidates(AuthoredReference reference, CatalogTargetAdapter adapter) {
        if (adapter == null || reference.parsedName() == null) {
            return List.of();
        }
        if (reference.parsedSource() != null) {
            validateSource(reference.parsedSource());
            return adapter.exact(reference.parsedName(), canonicalSource(reference.parsedSource()));
        }
        return adapter.candidates(reference.parsedName());
    }

    private void validateSource(String source) {
        if (sourceRegistry.find(source) == null) {
            throw new IllegalStateException("Unknown source identity in reference: " + source);
        }
    }

    private String canonicalSource(String source) {
        var identity = sourceRegistry.find(source);
        if (identity == null) {
            throw new IllegalStateException("Unknown source identity in reference: " + source);
        }
        return identity.identity().code();
    }

    private static ResolutionResult result(
            AuthoredReference reference,
            ResolutionOutcome outcome,
            CandidateCardinality cardinality,
            String targetId,
            ReferenceTargetKind resolvedDefinitionKind,
            List<TargetIdentity> candidates,
            String reason) {
        return new ResolutionResult(reference, outcome, cardinality, targetId, resolvedDefinitionKind,
                candidates, reason);
    }

    private static CandidateCardinality cardinality(int count) {
        if (count == 0) {
            return CandidateCardinality.ZERO;
        }
        return count == 1 ? CandidateCardinality.ONE : CandidateCardinality.MANY;
    }
}
