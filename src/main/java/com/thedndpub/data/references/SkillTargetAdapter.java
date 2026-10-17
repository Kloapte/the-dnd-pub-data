package com.thedndpub.data.references;

import com.thedndpub.data.services.SkillCatalog;
import com.thedndpub.data.services.SourceRegistry;

public final class SkillTargetAdapter extends AbstractIndexedTargetAdapter {
    public SkillTargetAdapter(SkillCatalog catalog, SourceRegistry sourceRegistry) {
        super(ReferenceTargetKind.SKILL, catalog.definitions().stream().map(definition -> {
            String source = definition.source().identity().code();
            requireRegistered(sourceRegistry, source);
            return new TargetIdentity(ReferenceTargetKind.SKILL, definition.id().value(), definition.name(), source);
        }).toList());
    }

    private static void requireRegistered(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Unknown skill target source: " + source);
        }
    }
}
