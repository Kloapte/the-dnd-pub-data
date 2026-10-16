package com.thedndpub.data.services;

import com.thedndpub.data.dto.action.ActionDefinition;
import com.thedndpub.data.dto.action.ActionDto;
import com.thedndpub.data.dto.action.ActionSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class ActionsService {
    private final ActionCatalog catalog;

    public ActionsService(ActionCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public List<ActionSummaryDto> findActions(String name, String source) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        return catalog.summaries().stream()
                .filter(action -> normalizedName == null
                        || action.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(action -> normalizedSource == null
                        || action.source().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .toList();
    }

    public ActionDto findById(String id) {
        ActionDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new ActionNotFoundException("No action found with ID '" + id + "'");
        }
        return ActionDto.from(definition);
    }

    public List<ActionDto> findByName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Action name is required");
        }
        String normalizedSource = normalize(source);
        List<ActionDto> matches = catalog.findByName(name).stream()
                .filter(action -> normalizedSource == null
                        || action.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .map(ActionDto::from)
                .toList();
        if (matches.isEmpty()) {
            throw new ActionNotFoundException("No action found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
