package com.thedndpub.data.services;

import com.thedndpub.data.dto.bastion.FacilityDefinition;
import com.thedndpub.data.dto.bastion.FacilityDetailDto;
import com.thedndpub.data.dto.bastion.FacilitySummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public final class BastionFacilitiesService {
    private final BastionFacilityCatalog catalog;
    private final PublicReferenceProjection references;

    public BastionFacilitiesService(BastionFacilityCatalog catalog, PublicReferenceProjection references) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.references = Objects.requireNonNull(references, "references");
    }

    public List<FacilitySummaryDto> find(String source, String type, Integer level) {
        String sourceFilter = normalize(source);
        String typeFilter = normalize(type);
        return catalog.summaries().stream()
                .filter(value -> sourceFilter == null || value.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(sourceFilter))
                .filter(value -> typeFilter == null || value.facilityType().equalsIgnoreCase(typeFilter))
                .filter(value -> level == null || Objects.equals(value.level(), level))
                .toList();
    }

    public FacilityDetailDto byId(String id) {
        FacilityDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new BastionFacilityNotFoundException("No Bastion Facility found with ID '" + id + "'");
        }
        return FacilityDetailDto.from(definition)
                .withCrossCatalogReferences(references.forOwner("facility", id));
    }

    public List<FacilityDetailDto> byName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Bastion Facility name is required");
        }
        String sourceFilter = normalize(source);
        List<FacilityDetailDto> matches = catalog.findByName(name).stream()
                .filter(value -> sourceFilter == null || value.source().identity().code()
                        .toLowerCase(Locale.ROOT).equals(sourceFilter))
                .map(value -> FacilityDetailDto.from(value).withCrossCatalogReferences(
                        references.forOwner("facility", value.id())))
                .toList();
        if (matches.isEmpty()) {
            throw new BastionFacilityNotFoundException("No Bastion Facility found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
