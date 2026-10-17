package com.thedndpub.data.dto.object;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.object.ObjectSourceDte;
import java.text.Normalizer;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public final class ObjectDefinitionMapper {
    public ObjectDefinition map(ObjectSourceDte source) {
        String id = "object:" + source.source() + ":" + slug(source.name());
        return new ObjectDefinition(
                id, source.name(), new SourceReference(source.source(), source.page()), source.objectType(),
                source.isNpc(), source.size(), source.ac(), source.hp(), source.speed(), source.str(), source.dex(),
                source.con(), source.intValue(), source.wis(), source.cha(), source.senses(), source.immune(),
                source.resist(), source.vulnerable(), source.conditionImmune(), source.actionEntries(),
                source.entries(), source.reprintedAs(), source.token(), source.altArt(), source.srd());
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
        String slug = normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isBlank()) {
            throw new IllegalArgumentException("Object name cannot produce a stable ID: " + value);
        }
        return slug;
    }
}
