package com.thedndpub.data.references;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AdapterRegistry {
    private final Map<ReferenceTargetKind, CatalogTargetAdapter> adapters;

    public AdapterRegistry(List<CatalogTargetAdapter> adapters) {
        Map<ReferenceTargetKind, CatalogTargetAdapter> indexed = new EnumMap<>(ReferenceTargetKind.class);
        for (CatalogTargetAdapter adapter : adapters) {
            Objects.requireNonNull(adapter, "adapter");
            if (indexed.putIfAbsent(adapter.targetKind(), adapter) != null) {
                throw new IllegalStateException("Duplicate target adapter: " + adapter.targetKind());
            }
        }
        this.adapters = Collections.unmodifiableMap(indexed);
    }

    public CatalogTargetAdapter adapter(ReferenceTargetKind kind) {
        return adapters.get(kind);
    }

    public Map<ReferenceTargetKind, CatalogTargetAdapter> adapters() {
        return adapters;
    }
}
