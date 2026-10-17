package com.thedndpub.data.services;

import com.thedndpub.data.dto.traphazard.HazardDefinition;
import com.thedndpub.data.dto.traphazard.HazardDetailDto;
import com.thedndpub.data.dto.traphazard.HazardSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class HazardsService {
    private final HazardCatalog catalog;
    private final PublicReferenceProjection references;

    public HazardsService(HazardCatalog catalog, PublicReferenceProjection references) {
        this.catalog = catalog;
        this.references = references;
    }

    public List<HazardSummaryDto> findAll(String source, String type) {
        return catalog.summaries().stream()
                .filter(value -> source == null || value.source().identity().code().equalsIgnoreCase(source.trim()))
                .filter(value -> type == null || type.equalsIgnoreCase(value.type()))
                .toList();
    }

    public HazardDetailDto findById(String id) {
        HazardDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new HazardNotFoundException(id);
        }
        return HazardDetailDto.from(definition).withCrossCatalogReferences(references.forOwner("hazard", id));
    }

    public List<HazardSummaryDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(HazardSummaryDto::from)
                .toList();
    }
}
