package com.thedndpub.data.references;

import com.thedndpub.data.services.OptionalFeatureCatalog;
import com.thedndpub.data.services.SourceRegistry;

public final class OptionalFeatureTargetAdapter extends AbstractIndexedTargetAdapter {
    public OptionalFeatureTargetAdapter(OptionalFeatureCatalog catalog, SourceRegistry sourceRegistry) {
        super(ReferenceTargetKind.OPTIONAL_FEATURE, catalog.definitions().stream().map(definition -> {
            String source = definition.source().identity().code();
            requireRegistered(sourceRegistry, source);
            return new TargetIdentity(
                    ReferenceTargetKind.OPTIONAL_FEATURE, definition.id(), definition.name(), source);
        }).toList());
    }

    private static void requireRegistered(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Unknown optional feature target source: " + source);
        }
    }
}
