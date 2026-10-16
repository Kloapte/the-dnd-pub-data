package com.thedndpub.data.dto.sense;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.sense.SenseDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

@Component
public final class SenseMapper {
    public SenseImportRecord mapRecord(SenseDte source) {
        Objects.requireNonNull(source, "source");
        requireValue(source.name(), "name");
        requireValue(source.source(), "source");
        if (source.page() == null || source.page() < 1) {
            throw new IllegalArgumentException("Sense page must be a positive integer");
        }
        if (source.entries() == null || source.entries().isEmpty()) {
            throw new IllegalArgumentException("Sense entries are required and must not be empty");
        }

        SenseDefinition definition = new SenseDefinition(
                new SenseId("sense:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceReference(new SourceIdentity(source.source()), source.page()));
        SenseImportProvenance provenance = new SenseImportProvenance(
                "data/senses.json",
                source.entries(),
                source.reprintedAs(),
                source.srd(),
                source.basicRules(),
                source.srd52());
        return new SenseImportRecord(definition, provenance);
    }

    private static void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Sense " + field + " is required");
        }
    }

    private static String slug(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a stable sense ID from '" + value + "'");
        }
        return slug;
    }
}
