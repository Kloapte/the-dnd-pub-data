package com.thedndpub.data.references;

import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.SourceRegistry;

public final class FeatTargetAdapter extends AbstractIndexedTargetAdapter {
    public FeatTargetAdapter(FeatCatalog catalog, SourceRegistry sourceRegistry) {
        super(ReferenceTargetKind.FEAT, catalog.definitions().stream().map(definition -> {
            String source = definition.source().identity().code();
            requireRegistered(sourceRegistry, source);
            return new TargetIdentity(ReferenceTargetKind.FEAT, definition.id().value(), definition.name(), source);
        }).toList());
    }

    private static void requireRegistered(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Unknown feat target source: " + source);
        }
    }
}
