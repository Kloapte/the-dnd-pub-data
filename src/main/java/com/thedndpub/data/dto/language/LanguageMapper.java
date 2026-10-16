package com.thedndpub.data.dto.language;

import com.thedndpub.data.dte.language.LanguageDte;
import com.thedndpub.data.dte.language.LanguageSourceReferenceDte;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class LanguageMapper {
    public NormalizedLanguageDefinition map(LanguageDte source) {
        return mapRecord(source).definition();
    }

    public LanguageImportRecord mapRecord(LanguageDte source) {
        Objects.requireNonNull(source, "source");
        if (source.name() == null || source.name().isBlank() || source.source() == null || source.source().isBlank()) {
            throw new IllegalArgumentException("Language name and source are required");
        }

        NormalizedLanguageDefinition definition = new NormalizedLanguageDefinition(
                new LanguageId("language:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceIdentity(source.source()),
                source.type(),
                source.script(),
                values(source.dialects()));
        LanguageImportProvenance provenance = new LanguageImportProvenance(
                "data/languages.json",
                source.page(),
                values(source.entries()),
                values(source.reprintedAs()),
                values(source.typicalSpeakers()),
                source.origin(),
                values(source.fonts()),
                sourceReferences(source.additionalSources()),
                sourceReferences(source.otherSources()),
                source.srd(),
                source.basicRules(),
                source.srd52(),
                source.basicRules2024(),
                source.hasFluffImages());
        return new LanguageImportRecord(definition, provenance);
    }

    private static <T> List<T> values(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static List<SourceReference> sourceReferences(
            List<LanguageSourceReferenceDte> references) {
        if (references == null) {
            return List.of();
        }
        return references.stream()
                .map(reference -> new SourceReference(reference.source(), reference.page()))
                .toList();
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a stable language ID from '" + value + "'");
        }
        return normalized;
    }
}
