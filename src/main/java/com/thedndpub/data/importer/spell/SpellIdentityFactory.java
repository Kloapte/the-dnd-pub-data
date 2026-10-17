package com.thedndpub.data.importer.spell;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.spell.SpellIdentity;

import java.text.Normalizer;
import java.util.Locale;

public final class SpellIdentityFactory {
    private SpellIdentityFactory() {
    }

    public static SpellIdentity identity(String name, String source) {
        return new SpellIdentity(name, new SourceIdentity(source));
    }

    public static SpellId id(String name, String source) {
        String sourceSlug = slug(source);
        String nameSlug = slug(name);
        if (sourceSlug.isEmpty() || nameSlug.isEmpty()) {
            throw new IllegalArgumentException("Spell identity cannot produce a readable source-aware ID: "
                    + name + "|" + source);
        }
        return new SpellId("spell:" + sourceSlug + ":" + nameSlug);
    }

    static String lookupKey(String name, String source) {
        return name + "\u0000" + source.toLowerCase(Locale.ROOT);
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
