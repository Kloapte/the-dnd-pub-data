package com.thedndpub.data.services;

import com.thedndpub.data.dto.treasure.TreasureTableDefinition;
import com.thedndpub.data.dto.treasure.TreasureTableDetailDto;
import com.thedndpub.data.dto.treasure.TreasureTableSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public final class TreasureTablesService {
    private final TreasureTableCatalog catalog;
    private final PublicReferenceProjection references;

    public TreasureTablesService(TreasureTableCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<TreasureTableSummaryDto> findAll(String source, String collectionType, String category) {
        return catalog.summaries().stream()
                .filter(summary -> matches(summary.source().code(), source))
                .filter(summary -> matches(summary.collectionType(), collectionType))
                .filter(summary -> matches(summary.category(), category))
                .toList();
    }

    public TreasureTableDetailDto findById(String id) {
        TreasureTableDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new TreasureTableNotFoundException(id);
        }
        return TreasureTableDetailDto.from(definition, catalog.dragonMundaneItemRows(),
                references.forOwner("treasure-table", id));
    }

    public List<TreasureTableSummaryDto> findByName(String name, String source, String collectionType) {
        return catalog.findByName(name).stream()
                .filter(definition -> matches(definition.source().identity().code(), source))
                .filter(definition -> matches(definition.kind().sourceCollection(), collectionType))
                .map(TreasureTableSummaryDto::from)
                .toList();
    }

    private static boolean matches(String value, String filter) {
        return filter == null || filter.isBlank() || value.equalsIgnoreCase(filter.trim());
    }
}
