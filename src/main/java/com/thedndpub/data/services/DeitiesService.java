package com.thedndpub.data.services;

import com.thedndpub.data.dto.deity.DeityDefinition;
import com.thedndpub.data.dto.deity.DeityDetailDto;
import com.thedndpub.data.dto.deity.DeitySummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class DeitiesService {
    private final DeityCatalog catalog;
    private final PublicReferenceProjection references;

    public DeitiesService(DeityCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<DeitySummaryDto> find(
            String name,
            String source,
            String pantheon,
            String alignment,
            String domain) {
        String nameFilter = normalize(name);
        String sourceFilter = normalize(source);
        String pantheonFilter = normalize(pantheon);
        String alignmentFilter = normalize(alignment);
        String domainFilter = normalize(domain);
        return catalog.summaries().stream()
                .filter(value -> nameFilter == null || value.name().toLowerCase(Locale.ROOT).contains(nameFilter))
                .filter(value -> sourceFilter == null
                        || value.source().identity().code().toLowerCase(Locale.ROOT).equals(sourceFilter))
                .filter(value -> pantheonFilter == null
                        || value.pantheon().toLowerCase(Locale.ROOT).equals(pantheonFilter))
                .filter(value -> alignmentFilter == null || value.alignment().stream()
                        .anyMatch(code -> code.toLowerCase(Locale.ROOT).equals(alignmentFilter)))
                .filter(value -> domainFilter == null || value.domains().stream()
                        .anyMatch(code -> code.toLowerCase(Locale.ROOT).equals(domainFilter)))
                .toList();
    }

    public DeityDetailDto byId(String id) {
        DeityDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new DeityNotFoundException("No Deity found with ID '" + id + "'");
        }
        return detail(definition);
    }

    public List<DeityDetailDto> byName(String name, String source, String pantheon) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deity name is required");
        }
        String sourceFilter = normalize(source);
        String pantheonFilter = normalize(pantheon);
        List<DeityDetailDto> matches = catalog.findByName(name).stream()
                .filter(value -> sourceFilter == null || value.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(sourceFilter))
                .filter(value -> pantheonFilter == null
                        || value.pantheon().toLowerCase(Locale.ROOT).equals(pantheonFilter))
                .map(this::detail)
                .toList();
        if (matches.isEmpty()) {
            throw new DeityNotFoundException("No Deity found with name '" + name + "'");
        }
        return matches;
    }

    private DeityDetailDto detail(DeityDefinition definition) {
        return DeityDetailDto.from(definition)
                .withCrossCatalogReferences(references.forOwner("deity", definition.id().value()));
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
