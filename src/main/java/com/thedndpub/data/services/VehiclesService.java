package com.thedndpub.data.services;

import com.thedndpub.data.dto.vehicle.VehicleDefinition;
import com.thedndpub.data.dto.vehicle.VehicleDetailDto;
import com.thedndpub.data.dto.vehicle.VehicleSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class VehiclesService {
    private final VehicleCatalog catalog;
    private final PublicReferenceProjection references;

    public VehiclesService(VehicleCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<VehicleSummaryDto> find(String name, String source, String type) {
        String nameFilter = normalize(name);
        String sourceFilter = normalize(source);
        String typeFilter = normalize(type);
        return catalog.summaries().stream()
                .filter(value -> nameFilter == null || value.name().toLowerCase(Locale.ROOT).contains(nameFilter))
                .filter(value -> sourceFilter == null || value.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(sourceFilter))
                .filter(value -> typeFilter == null || value.vehicleType().toLowerCase(Locale.ROOT).equals(typeFilter))
                .toList();
    }

    public VehicleDetailDto byId(String id) {
        VehicleDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new VehicleNotFoundException("No Vehicle found with ID '" + id + "'");
        }
        return VehicleDetailDto.from(definition).withCrossCatalogReferences(references.forOwner("vehicle", id));
    }

    public List<VehicleDetailDto> byName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Vehicle name is required");
        }
        String sourceFilter = normalize(source);
        List<VehicleDetailDto> matches = catalog.findByName(name).stream()
                .filter(value -> sourceFilter == null || value.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(sourceFilter))
                .map(value -> VehicleDetailDto.from(value)
                        .withCrossCatalogReferences(references.forOwner("vehicle", value.id().value())))
                .toList();
        if (matches.isEmpty()) {
            throw new VehicleNotFoundException("No Vehicle found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
