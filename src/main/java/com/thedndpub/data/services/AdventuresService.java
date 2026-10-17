package com.thedndpub.data.services;

import com.thedndpub.data.dto.adventure.AdventureDefinition;
import com.thedndpub.data.dto.adventure.AdventureDto;
import com.thedndpub.data.dto.adventure.AdventureSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class AdventuresService {
    private final AdventureCatalog catalog;

    public AdventuresService(AdventureCatalog catalog) {
        this.catalog = catalog;
    }

    public List<AdventureSummaryDto> findAll(String source, String group) {
        return catalog.summaries().stream()
                .filter(summary -> source == null || summary.source().code().equalsIgnoreCase(source.trim()))
                .filter(summary -> group == null || summary.group().equalsIgnoreCase(group.trim()))
                .toList();
    }

    public AdventureDto findById(String id) {
        AdventureDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new AdventureNotFoundException(id);
        }
        return AdventureDto.from(definition);
    }

    public List<AdventureSummaryDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(AdventureSummaryDto::from)
                .toList();
    }
}
