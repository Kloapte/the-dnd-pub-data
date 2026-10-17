package com.thedndpub.data.services;

import com.thedndpub.data.dto.feat.FeatDto;
import com.thedndpub.data.dto.feat.FeatSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class FeatsService {
    private final FeatCatalog catalog;
    private final PublicReferenceProjection references;

    public FeatsService(FeatCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<FeatSummaryDto> findFeats(String name, String source, String category) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        String normalizedCategory = normalize(category);
        return catalog.summaries().stream()
                .filter(feat -> normalizedName == null
                        || feat.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(feat -> normalizedSource == null
                        || feat.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .filter(feat -> normalizedCategory == null
                        || feat.category() != null && feat.category().toLowerCase(Locale.ROOT).equals(normalizedCategory))
                .toList();
    }

    public FeatDto findById(String id) {
        var feat = catalog.findById(id);
        if (feat == null) {
            throw new FeatNotFoundException("No feat found with ID '" + id + "'");
        }
        return FeatDto.from(feat).withCrossCatalogReferences(references.forOwner("feat", id));
    }

    public List<FeatDto> findByName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Feat name is required");
        }
        String normalizedSource = normalize(source);
        List<FeatDto> matches = catalog.findByName(name).stream()
                .filter(feat -> normalizedSource == null
                        || feat.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .map(feat -> FeatDto.from(feat).withCrossCatalogReferences(
                        references.forOwner("feat", feat.id().value())))
                .toList();
        if (matches.isEmpty()) {
            throw new FeatNotFoundException("No feat found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
