package com.thedndpub.data.services;

import com.thedndpub.data.dto.object.ObjectDefinition;
import com.thedndpub.data.dto.object.ObjectDetailDto;
import com.thedndpub.data.dto.object.ObjectSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class ObjectsService {
    private final ObjectCatalog catalog;
    private final PublicReferenceProjection references;

    public ObjectsService(ObjectCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<ObjectSummaryDto> find(String name, String source, String type) {
        String nameFilter = normalize(name);
        String sourceFilter = normalize(source);
        String typeFilter = normalize(type);
        return catalog.summaries().stream()
                .filter(item -> nameFilter == null || item.name().toLowerCase(Locale.ROOT).contains(nameFilter))
                .filter(item -> sourceFilter == null
                        || item.source().identity().code().toLowerCase(Locale.ROOT).equals(sourceFilter))
                .filter(item -> typeFilter == null || item.category().toLowerCase(Locale.ROOT).equals(typeFilter))
                .toList();
    }

    public ObjectDetailDto byId(String id) {
        ObjectDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new ObjectNotFoundException("No object found with ID '" + id + "'");
        }
        return ObjectDetailDto.from(definition).withCrossCatalogReferences(references.forOwner("object", id));
    }

    public List<ObjectDetailDto> byName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Object name is required");
        }
        String sourceFilter = normalize(source);
        List<ObjectDetailDto> matches = catalog.findByName(name).stream()
                .filter(item -> sourceFilter == null || item.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(sourceFilter))
                .map(item -> ObjectDetailDto.from(item).withCrossCatalogReferences(
                        references.forOwner("object", item.id())))
                .toList();
        if (matches.isEmpty()) {
            throw new ObjectNotFoundException("No object found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
