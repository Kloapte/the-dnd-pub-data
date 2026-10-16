package com.thedndpub.data.services;

import com.thedndpub.data.dto.magicvariant.MagicVariantDto;
import com.thedndpub.data.dto.magicvariant.MagicVariantKind;
import com.thedndpub.data.dto.magicvariant.MagicVariantSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class MagicVariantsService {
    private final MagicVariantCatalog catalog;

    public MagicVariantsService(MagicVariantCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public List<MagicVariantSummaryDto> find(String name, String source, String type, MagicVariantKind kind) {
        return catalog.find(name, source, type, kind).stream()
                .map(variant -> new MagicVariantSummaryDto(variant.id(), variant.name(), variant.source(),
                        variant.type(), variant.kind(), variant.eligibleItemCount()))
                .toList();
    }

    public MagicVariantDto byId(String id) {
        MagicVariantDto variant = catalog.findById(id);
        if (variant == null) {
            throw new MagicVariantNotFoundException("No magic variant found with ID '" + id + "'");
        }
        return variant;
    }

    public List<MagicVariantDto> byName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Magic variant name is required");
        }
        String normalizedSource = source == null || source.isBlank()
                ? null
                : source.trim().toLowerCase(Locale.ROOT);
        List<MagicVariantDto> matches = catalog.findByNameAndSource(name, normalizedSource);
        if (matches.isEmpty()) {
            throw new MagicVariantNotFoundException("No magic variant found with name '" + name + "'");
        }
        return matches;
    }
}
