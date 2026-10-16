package com.thedndpub.data.dto.condition;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.condition.DiseaseDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class DiseaseMapper {
    public DiseaseImportRecord mapRecord(DiseaseDte source) {
        Objects.requireNonNull(source, "source");
        if (source.name() == null || source.name().isBlank()
                || source.source() == null || source.source().isBlank()) {
            throw new IllegalArgumentException("Disease name and source are required");
        }
        DiseaseDefinition definition = new DiseaseDefinition(
                new DiseaseId("disease:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceReference(source.source(), source.page()),
                reprints(source.reprintedAs()),
                source.type());
        return new DiseaseImportRecord(
                definition,
                new DiseaseImportProvenance(
                        source.entries() == null ? List.of() : source.entries(),
                        source.srd(),
                        source.srd52()));
    }

    private static List<DefinitionReprintReference> reprints(List<String> references) {
        if (references == null) {
            return List.of();
        }
        return references.stream().map(reference -> {
            String[] parts = reference.split("\\|", -1);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new IllegalArgumentException("Invalid disease reprint reference: " + reference);
            }
            return new DefinitionReprintReference(parts[0], new SourceIdentity(parts[1]));
        }).toList();
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a stable disease ID from '" + value + "'");
        }
        return normalized;
    }
}
