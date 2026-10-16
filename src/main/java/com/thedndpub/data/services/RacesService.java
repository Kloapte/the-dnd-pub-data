package com.thedndpub.data.services;

import com.thedndpub.data.dto.race.RaceDefinitionKind;
import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.dto.race.RaceMapper;
import com.thedndpub.data.dto.race.RaceSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public final class RacesService {
    private final RaceCatalog catalog;
    private final RaceMapper mapper;

    public RacesService(RaceCatalog catalog, RaceMapper mapper) {
        this.catalog = catalog;
        this.mapper = mapper;
    }

    public List<RaceSummaryDto> find(String name, String source, String definitionKind) {
        RaceDefinitionKind kind = parseKind(definitionKind);
        return catalog.summaries().stream()
                .filter(value -> name == null || value.name().toLowerCase(Locale.ROOT)
                        .contains(name.toLowerCase(Locale.ROOT)))
                .filter(value -> source == null || value.source().identity().code().equalsIgnoreCase(source))
                .filter(value -> kind == null || value.definitionKind() == kind)
                .toList();
    }

    public RaceDto byId(String id) {
        var definition = catalog.findById(id);
        if (definition == null) {
            throw new RaceNotFoundException("No race definition found with ID '" + id + "'");
        }
        return mapper.toDto(definition);
    }

    public List<RaceDto> byName(String name, String source) {
        List<RaceDto> matches = catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source))
                .map(mapper::toDto)
                .toList();
        if (matches.isEmpty()) {
            throw new RaceNotFoundException("No race definition found with name '" + name + "'");
        }
        return matches;
    }

    private static RaceDefinitionKind parseKind(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return RaceDefinitionKind.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("definitionKind must be RACE or SUBRACE", exception);
        }
    }
}
