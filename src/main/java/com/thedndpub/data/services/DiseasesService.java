package com.thedndpub.data.services;

import com.thedndpub.data.dto.condition.DiseaseDefinition;
import com.thedndpub.data.dto.condition.DiseaseDto;
import com.thedndpub.data.dto.condition.DiseaseSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class DiseasesService {
    private final DiseaseCatalog catalog;

    public DiseasesService(DiseaseCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public List<DiseaseSummaryDto> findDiseases(String name, String source) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        return catalog.summaries().stream()
                .filter(item -> normalizedName == null
                        || item.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(item -> normalizedSource == null
                        || item.source().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .toList();
    }

    public DiseaseDto findById(String id) {
        DiseaseDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new DiseaseNotFoundException("No disease found with ID '" + id + "'");
        }
        return DiseaseDto.from(definition);
    }

    public List<DiseaseDto> findByName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Disease name is required");
        }
        String normalizedSource = normalize(source);
        List<DiseaseDto> matches = catalog.findByName(name).stream()
                .filter(definition -> normalizedSource == null
                        || definition.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .map(DiseaseDto::from)
                .toList();
        if (matches.isEmpty()) {
            throw new DiseaseNotFoundException("No disease found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
