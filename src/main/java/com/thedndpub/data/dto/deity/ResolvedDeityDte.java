package com.thedndpub.data.dto.deity;

import com.thedndpub.data.dte.deity.DeityDte;

import java.util.List;

/** Authored deity data plus source-defined inherited metadata and copy lineage. */
public record ResolvedDeityDte(
        DeityDte authored,
        List<String> effectiveAlignment,
        List<String> effectiveDomains,
        String effectiveSymbol,
        List<DeityId> copyLineage,
        List<String> inheritedFields) {
    public ResolvedDeityDte {
        effectiveAlignment = immutable(effectiveAlignment);
        effectiveDomains = immutable(effectiveDomains);
        copyLineage = immutable(copyLineage);
        inheritedFields = immutable(inheritedFields);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
