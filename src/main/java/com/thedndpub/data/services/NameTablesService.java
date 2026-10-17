package com.thedndpub.data.services;

import com.thedndpub.data.dto.nametable.NameTableDefinition;
import com.thedndpub.data.dto.nametable.NameTableDto;
import com.thedndpub.data.dto.nametable.NameTableSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class NameTablesService {
    private final NameTableCatalog catalog;

    public NameTablesService(NameTableCatalog catalog) {
        this.catalog = catalog;
    }

    public List<NameTableSummaryDto> findAll(String source) {
        if (source == null) {
            return catalog.summaries();
        }
        return catalog.summaries().stream()
                .filter(summary -> summary.source().code().equalsIgnoreCase(source.trim()))
                .toList();
    }

    public NameTableDto findById(String id) {
        NameTableDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new NameTableNotFoundException(id);
        }
        return NameTableDto.from(definition);
    }

    public List<NameTableSummaryDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(NameTableSummaryDto::from)
                .toList();
    }
}
