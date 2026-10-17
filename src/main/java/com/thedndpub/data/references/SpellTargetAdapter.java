package com.thedndpub.data.references;

import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.SpellCatalog;

import java.util.List;

public final class SpellTargetAdapter extends AbstractIndexedTargetAdapter {
    public SpellTargetAdapter(SpellCatalog catalog, SourceRegistry sourceRegistry) {
        super(ReferenceTargetKind.SPELL, catalog.findAll().stream().map(spell -> {
            String source = spell.source().identity().code();
            requireRegistered(sourceRegistry, source);
            return new TargetIdentity(ReferenceTargetKind.SPELL, spell.id().value(), spell.identity().name(), source);
        }).toList());
    }

    private static void requireRegistered(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Unknown spell target source: " + source);
        }
    }
}
