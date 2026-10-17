package com.thedndpub.data.references;

import com.thedndpub.data.services.LanguageCatalog;
import com.thedndpub.data.services.SourceRegistry;

public final class LanguageTargetAdapter extends AbstractIndexedTargetAdapter {
    public LanguageTargetAdapter(LanguageCatalog catalog, SourceRegistry sourceRegistry) {
        super(ReferenceTargetKind.LANGUAGE, catalog.definitions().stream().map(definition -> {
            String source = definition.source().code();
            requireRegistered(sourceRegistry, source);
            return new TargetIdentity(ReferenceTargetKind.LANGUAGE, definition.id().value(), definition.name(), source);
        }).toList());
    }

    private static void requireRegistered(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Unknown language target source: " + source);
        }
    }
}
