package com.thedndpub.data.dto.deity;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.deity.DeityAdditionalSourceDte;
import com.thedndpub.data.dte.deity.DeityDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class DeityMapper {
    public DeityDefinition map(ResolvedDeityDte resolved) {
        Objects.requireNonNull(resolved, "resolved");
        DeityDte authored = resolved.authored();
        validateIdentity(authored);

        List<SourceReference> additionalSources = authored.additionalSources() == null
                ? List.of()
                : authored.additionalSources().stream().map(DeityMapper::sourceReference).toList();

        return new DeityDefinition(
                idFor(authored.name(), authored.source(), authored.pantheon()),
                authored.name(),
                new SourceReference(authored.source(), authored.page()),
                authored.pantheon(),
                authored.category(),
                resolved.effectiveAlignment(),
                resolved.effectiveDomains(),
                authored.title(),
                authored.altNames(),
                authored.province(),
                authored.plane(),
                authored.worshipers(),
                resolved.effectiveSymbol(),
                authored.symbolImg(),
                additionalSources,
                authored.srd(),
                authored.basicRules(),
                authored.piety(),
                authored.reprintAlias(),
                authored.customExtensionOf(),
                authored.copy(),
                resolved.copyLineage(),
                resolved.inheritedFields(),
                authored.entries(),
                authored);
    }

    public static DeityId idFor(String name, String source, String pantheon) {
        String nameSlug = slug(name);
        String pantheonSlug = slug(pantheon);
        if (nameSlug.isBlank() || pantheonSlug.isBlank() || source == null || source.isBlank()) {
            throw new IllegalArgumentException("Deity identity requires name, source, and pantheon");
        }
        return new DeityId("deity:" + source + ":" + pantheonSlug + ":" + nameSlug);
    }

    private static SourceReference sourceReference(DeityAdditionalSourceDte reference) {
        return new SourceReference(reference.source(), reference.page());
    }

    private static void validateIdentity(DeityDte authored) {
        if (authored == null || authored.name() == null || authored.name().isBlank()
                || authored.source() == null || authored.source().isBlank()
                || authored.pantheon() == null || authored.pantheon().isBlank()
                || authored.page() == null) {
            throw new IllegalArgumentException("Deity requires authored name, source, pantheon, and page");
        }
    }

    private static String slug(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
