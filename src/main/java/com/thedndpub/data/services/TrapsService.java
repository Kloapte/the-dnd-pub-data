package com.thedndpub.data.services;

import com.thedndpub.data.dto.traphazard.TrapDefinition;
import com.thedndpub.data.dto.traphazard.TrapDetailDto;
import com.thedndpub.data.dto.traphazard.TrapSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class TrapsService {
    private final TrapCatalog catalog;
    private final PublicReferenceProjection references;

    public TrapsService(TrapCatalog catalog, PublicReferenceProjection references) {
        this.catalog = catalog;
        this.references = references;
    }

    public List<TrapSummaryDto> findAll(String source, String type) {
        return catalog.summaries().stream()
                .filter(value -> source == null || value.source().identity().code().equalsIgnoreCase(source.trim()))
                .filter(value -> type == null || value.type().equalsIgnoreCase(type.trim()))
                .toList();
    }

    public TrapDetailDto findById(String id) {
        TrapDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new TrapNotFoundException(id);
        }
        return TrapDetailDto.from(definition).withCrossCatalogReferences(references.forOwner("trap", id));
    }

    public List<TrapSummaryDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(TrapSummaryDto::from)
                .toList();
    }
}
