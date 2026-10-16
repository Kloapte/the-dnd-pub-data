package com.thedndpub.data.dto.book;

import com.thedndpub.data.dte.book.BookDte;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@Component
public final class PublicationMapper {
    public PublicationImportRecord map(BookDte source) {
        Objects.requireNonNull(source, "source");
        requireValue(source.source(), "source");
        requireValue(source.id(), "id");
        requireValue(source.name(), "name");
        LocalDate published = parseDate(source.published(), "published");
        LocalDate revised = parseDate(source.revised(), "revised");

        PublicationDefinition definition = new PublicationDefinition(
                new PublicationId(source.source()),
                source.name(),
                source.group(),
                published,
                revised,
                source.author(),
                source.alias(),
                source.parentSource());
        PublicationImportProvenance provenance = new PublicationImportProvenance(
                source.id(),
                source.cover() == null ? null : source.cover().type(),
                source.cover() == null ? null : source.cover().path(),
                source.coverCredit(),
                source.contents());
        return new PublicationImportRecord(definition, provenance);
    }

    private static void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Publication " + field + " is required");
        }
    }

    private static LocalDate parseDate(String value, String field) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Publication " + field + " must be an ISO date: " + value, exception);
        }
    }
}
