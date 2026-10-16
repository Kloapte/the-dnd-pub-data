package com.thedndpub.data.services;

import com.thedndpub.data.dto.background.BackgroundDto;
import com.thedndpub.data.dto.background.BackgroundMapper;
import com.thedndpub.data.dto.background.BackgroundSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public final class BackgroundsService {
    private final BackgroundCatalog catalog;
    private final BackgroundMapper mapper;

    public BackgroundsService(BackgroundCatalog catalog, BackgroundMapper mapper) {
        this.catalog = catalog;
        this.mapper = mapper;
    }

    public List<BackgroundSummaryDto> find(String name, String source) {
        return catalog.summaries().stream()
                .filter(item -> name == null || item.name().toLowerCase(Locale.ROOT)
                        .contains(name.toLowerCase(Locale.ROOT)))
                .filter(item -> source == null || item.source().identity().code().equalsIgnoreCase(source))
                .toList();
    }

    public BackgroundDto byId(String id) {
        var definition = catalog.findById(id);
        if (definition == null) {
            throw new BackgroundNotFoundException("No background found with ID '" + id + "'");
        }
        return mapper.toDto(definition);
    }

    public List<BackgroundDto> byName(String name, String source) {
        List<BackgroundDto> matches = catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source))
                .map(mapper::toDto)
                .toList();
        if (matches.isEmpty()) {
            throw new BackgroundNotFoundException("No background found with name '" + name + "'");
        }
        return matches;
    }
}
