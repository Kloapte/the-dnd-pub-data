package com.thedndpub.data.services;

import com.thedndpub.data.dto.book.PublicationDefinition;
import com.thedndpub.data.dto.book.PublicationDto;
import com.thedndpub.data.dto.book.PublicationSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class PublicationsService {
    private final PublicationCatalog catalog;

    public PublicationsService(PublicationCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public List<PublicationSummaryDto> findPublications(String name, String source) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        return catalog.summaries().stream()
                .filter(publication -> normalizedName == null
                        || publication.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(publication -> normalizedSource == null
                        || publication.id().value().equalsIgnoreCase(normalizedSource))
                .toList();
    }

    public PublicationDto findById(String id) {
        PublicationDefinition publication = catalog.findById(id);
        if (publication == null) {
            throw new PublicationNotFoundException("No publication found with ID '" + id + "'");
        }
        return PublicationDto.from(publication);
    }

    public List<PublicationDto> findByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Publication name is required");
        }
        List<PublicationDefinition> matches = catalog.findByName(name);
        if (matches.isEmpty()) {
            throw new PublicationNotFoundException("No publication found with name '" + name + "'");
        }
        return matches.stream().map(PublicationDto::from).toList();
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
