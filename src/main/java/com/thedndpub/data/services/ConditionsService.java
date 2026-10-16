package com.thedndpub.data.services;

import com.thedndpub.data.dto.condition.ConditionDefinition;
import com.thedndpub.data.dto.condition.ConditionDto;
import com.thedndpub.data.dto.condition.ConditionSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class ConditionsService {
    private final ConditionCatalog catalog;

    public ConditionsService(ConditionCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public List<ConditionSummaryDto> findConditions(String name, String source) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        return catalog.summaries().stream()
                .filter(item -> normalizedName == null
                        || item.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(item -> normalizedSource == null
                        || item.source().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .toList();
    }

    public ConditionDto findById(String id) {
        ConditionDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new ConditionNotFoundException("No condition found with ID '" + id + "'");
        }
        return ConditionDto.from(definition);
    }

    public List<ConditionDto> findByName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Condition name is required");
        }
        String normalizedSource = normalize(source);
        List<ConditionDto> matches = catalog.findByName(name).stream()
                .filter(definition -> normalizedSource == null
                        || definition.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .map(ConditionDto::from)
                .toList();
        if (matches.isEmpty()) {
            throw new ConditionNotFoundException("No condition found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
