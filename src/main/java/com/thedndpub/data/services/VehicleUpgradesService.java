package com.thedndpub.data.services;

import com.thedndpub.data.dto.vehicle.VehicleUpgradeDefinition;
import com.thedndpub.data.dto.vehicle.VehicleUpgradeDetailDto;
import com.thedndpub.data.dto.vehicle.VehicleUpgradeSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public final class VehicleUpgradesService {
    private final VehicleUpgradeCatalog catalog;
    private final PublicReferenceProjection references;

    public VehicleUpgradesService(VehicleUpgradeCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<VehicleUpgradeSummaryDto> find(String name, String source, String type) {
        String nameFilter = normalize(name);
        String sourceFilter = normalize(source);
        String typeFilter = normalize(type);
        return catalog.summaries().stream()
                .filter(value -> nameFilter == null || value.name().toLowerCase(Locale.ROOT).contains(nameFilter))
                .filter(value -> sourceFilter == null || value.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(sourceFilter))
                .filter(value -> typeFilter == null || value.upgradeType().stream()
                        .anyMatch(category -> category.toLowerCase(Locale.ROOT).equals(typeFilter)))
                .toList();
    }

    public VehicleUpgradeDetailDto byId(String id) {
        VehicleUpgradeDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new VehicleUpgradeNotFoundException("No Vehicle Upgrade found with ID '" + id + "'");
        }
        return VehicleUpgradeDetailDto.from(definition)
                .withCrossCatalogReferences(references.forOwner("vehicle-upgrade", id));
    }

    public List<VehicleUpgradeDetailDto> byName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Vehicle Upgrade name is required");
        }
        String sourceFilter = normalize(source);
        List<VehicleUpgradeDetailDto> matches = catalog.findByName(name).stream()
                .filter(value -> sourceFilter == null || value.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(sourceFilter))
                .map(value -> VehicleUpgradeDetailDto.from(value)
                        .withCrossCatalogReferences(references.forOwner("vehicle-upgrade", value.id().value())))
                .toList();
        if (matches.isEmpty()) {
            throw new VehicleUpgradeNotFoundException("No Vehicle Upgrade found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
