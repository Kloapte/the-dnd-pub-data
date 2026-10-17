package com.thedndpub.data.services;

import com.thedndpub.data.dto.cultboon.CultDefinition;
import com.thedndpub.data.dto.cultboon.CultDto;
import com.thedndpub.data.dto.cultboon.CultSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public final class CultsService {
    private final CultCatalog catalog;
    private final PublicReferenceProjection references;

    public CultsService(CultCatalog catalog, PublicReferenceProjection references) {
        this.catalog = catalog;
        this.references = references;
    }

    public List<CultSummaryDto> find(String name, String source, String type) {
        return catalog.summaries().stream()
                .filter(cult -> contains(cult.name(), name))
                .filter(cult -> matches(cult.source().identity().code(), source))
                .filter(cult -> matches(cult.type(), type))
                .toList();
    }

    public CultDto findById(String id) {
        CultDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new CultNotFoundException(id);
        }
        return CultDto.from(definition, references.forOwner("cult", definition.id().value()));
    }

    public List<CultDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(cult -> matches(cult.source().identity().code(), source))
                .map(cult -> CultDto.from(cult, references.forOwner("cult", cult.id().value())))
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
