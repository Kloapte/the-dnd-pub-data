package com.thedndpub.data.services;

import com.thedndpub.data.dto.language.LanguageDto;
import com.thedndpub.data.dto.language.LanguageSummaryDto;
import com.thedndpub.data.dto.language.NormalizedLanguageDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class LanguagesService {
    private final LanguageCatalog catalog;

    public LanguagesService(LanguageCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public List<LanguageSummaryDto> findLanguages(String name, String source) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        return catalog.summaries().stream()
                .filter(language -> normalizedName == null
                        || language.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(language -> normalizedSource == null
                        || language.source().equalsIgnoreCase(normalizedSource))
                .toList();
    }

    public LanguageDto findById(String id) {
        NormalizedLanguageDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new LanguageNotFoundException("No language found with ID '" + id + "'");
        }
        return LanguageDto.from(definition);
    }

    public LanguageDto findByNameAndSource(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Language name is required");
        }
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Language source is required");
        }
        NormalizedLanguageDefinition definition = catalog.findByNameAndSource(name, source);
        if (definition == null) {
            throw new LanguageNotFoundException(
                    "No language found with name '" + name + "' and source '" + source + "'");
        }
        return LanguageDto.from(definition);
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
