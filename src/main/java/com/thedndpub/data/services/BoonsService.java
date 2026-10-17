package com.thedndpub.data.services;

import com.thedndpub.data.dto.cultboon.BoonDefinition;
import com.thedndpub.data.dto.cultboon.BoonDto;
import com.thedndpub.data.dto.cultboon.BoonSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public final class BoonsService {
    private final BoonCatalog catalog;
    private final PublicReferenceProjection references;

    public BoonsService(BoonCatalog catalog, PublicReferenceProjection references) {
        this.catalog = catalog;
        this.references = references;
    }

    public List<BoonSummaryDto> find(String name, String source, String type) {
        return catalog.summaries().stream()
                .filter(boon -> contains(boon.name(), name))
                .filter(boon -> matches(boon.source().identity().code(), source))
                .filter(boon -> matches(boon.type(), type))
                .toList();
    }

    public BoonDto findById(String id) {
        BoonDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new BoonNotFoundException(id);
        }
        return BoonDto.from(definition, references.forOwner("boon", definition.id().value()));
    }

    public List<BoonDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(boon -> matches(boon.source().identity().code(), source))
                .map(boon -> BoonDto.from(boon, references.forOwner("boon", boon.id().value())))
                .toList();
    }

    private static boolean contains(String value, String filter) {
        return filter == null || filter.isBlank() || normalize(value).contains(normalize(filter));
    }

    private static boolean matches(String value, String filter) {
        return filter == null || filter.isBlank() || normalize(value).equals(normalize(filter));
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
