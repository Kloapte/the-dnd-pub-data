package com.thedndpub.data.dto.adventure;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.adventure.AdventureDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class AdventureMapper {
    public AdventureImportRecord mapRecord(AdventureDte source) {
        Objects.requireNonNull(source, "source");
        requireValue(source.name(), "name");
        requireValue(source.id(), "id");
        requireValue(source.source(), "source");
        requireValue(source.group(), "group");
        requireValue(source.published(), "published");
        if (source.level() == null || source.level().start() == null || source.level().end() == null) {
            throw new IllegalArgumentException("Adventure level start and end are required");
        }
        if (source.level().start() < 0 || source.level().end() < source.level().start()) {
            throw new IllegalArgumentException("Adventure level range is invalid");
        }

        LocalDate publishedDate = parseDate(source.published());
        AdventureDefinition definition = new AdventureDefinition(
                new AdventureId("adventure:" + slug(source.source())),
                source.id(),
                source.name(),
                new SourceReference(new SourceIdentity(source.source()), null),
                source.group(),
                publishedDate,
                source.author(),
                source.alias() == null ? List.of() : source.alias(),
                source.parentSource(),
                source.publishedOrder(),
                source.storyline(),
                new AdventureLevelRange(source.level().start(), source.level().end()));
        AdventureImportProvenance provenance = new AdventureImportProvenance(
                "data/adventures.json",
                source.cover(),
                source.contents() == null ? List.of() : source.contents());
        return new AdventureImportRecord(definition, provenance);
    }

    private static void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Adventure " + field + " is required");
        }
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Adventure published must be an ISO date: " + value, exception);
        }
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a stable adventure ID from source '" + value + "'");
        }
        return normalized;
    }
}
