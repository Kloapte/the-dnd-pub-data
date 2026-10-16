package com.thedndpub.data.services;

import com.thedndpub.data.dto.sense.SenseDefinition;
import com.thedndpub.data.dto.sense.SenseDto;
import com.thedndpub.data.dto.sense.SenseSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class SensesService {
    private final SenseCatalog catalog;

    public SensesService(SenseCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public List<SenseSummaryDto> findSenses(String name, String source) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        return catalog.summaries().stream()
                .filter(sense -> normalizedName == null
                        || sense.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(sense -> normalizedSource == null
                        || sense.source().identity().code().equalsIgnoreCase(normalizedSource))
                .toList();
    }

    public SenseDto findById(String id) {
        SenseDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new SenseNotFoundException("No sense found with ID '" + id + "'");
        }
        return SenseDto.from(definition);
    }

    public List<SenseDto> findByName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Sense name is required");
        }
        List<SenseDto> matches = catalog.findByName(name).stream()
                .filter(definition -> source == null || source.isBlank()
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(SenseDto::from)
                .toList();
        if (matches.isEmpty()) {
            String sourceDetail = source == null || source.isBlank() ? "" : " and source '" + source + "'";
            throw new SenseNotFoundException("No sense found with name '" + name + "'" + sourceDetail);
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
