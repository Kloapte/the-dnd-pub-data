package com.thedndpub.data.services;

import com.thedndpub.data.dto.encounter.EncounterDefinition;
import com.thedndpub.data.dto.encounter.EncounterDetailDto;
import com.thedndpub.data.dto.encounter.EncounterSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class EncountersService {
    private final EncounterCatalog catalog;
    private final PublicReferenceProjection references;

    public EncountersService(EncounterCatalog catalog, PublicReferenceProjection references) {
        this.catalog = catalog;
        this.references = references;
    }

    public List<EncounterSummaryDto> findAll(String source) {
        return catalog.summaries().stream()
                .filter(summary -> source == null || summary.source().code().equalsIgnoreCase(source.trim()))
                .toList();
    }

    public EncounterDetailDto findById(String id) {
        EncounterDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new EncounterNotFoundException(id);
        }
        return EncounterDetailDto.from(definition)
                .withCrossCatalogReferences(references.forOwner("encounter", id));
    }

    public List<EncounterSummaryDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(EncounterSummaryDto::from)
                .toList();
    }
}
