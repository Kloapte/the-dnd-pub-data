package com.thedndpub.data.dto.condition;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.condition.ConditionDte;
import com.thedndpub.data.dte.condition.ConditionSourceReferenceDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class ConditionMapper {
    public ConditionImportRecord mapRecord(ConditionDte source) {
        Objects.requireNonNull(source, "source");
        requireIdentity(source.name(), source.source());
        ConditionDefinition definition = new ConditionDefinition(
                new ConditionId("condition:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceReference(source.source(), source.page()),
                reprints(source.reprintedAs()),
                sourceReferences(source.otherSources()));
        ConditionImportProvenance provenance = new ConditionImportProvenance(
                values(source.entries()),
                source.srd(),
                source.basicRules(),
                source.srd52(),
                source.basicRules2024(),
                source.hasFluffImages());
        return new ConditionImportRecord(definition, provenance);
    }

    private static List<DefinitionReprintReference> reprints(List<String> references) {
        if (references == null) {
            return List.of();
        }
        return references.stream().map(reference -> {
            String[] parts = reference.split("\\|", -1);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new IllegalArgumentException("Invalid condition reprint reference: " + reference);
            }
            return new DefinitionReprintReference(parts[0], new SourceIdentity(parts[1]));
        }).toList();
    }

    private static List<SourceReference> sourceReferences(List<ConditionSourceReferenceDte> references) {
        if (references == null) {
            return List.of();
        }
        return references.stream()
                .map(reference -> new SourceReference(reference.source(), reference.page()))
                .toList();
    }

    private static <T> List<T> values(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static void requireIdentity(String name, String source) {
        if (name == null || name.isBlank() || source == null || source.isBlank()) {
            throw new IllegalArgumentException("Condition name and source are required");
        }
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a stable condition ID from '" + value + "'");
        }
        return normalized;
    }
}
