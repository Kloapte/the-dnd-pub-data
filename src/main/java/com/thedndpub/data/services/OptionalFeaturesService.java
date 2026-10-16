package com.thedndpub.data.services;

import com.thedndpub.data.dto.optionalfeature.OptionalFeatureDto;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public final class OptionalFeaturesService {
    private final OptionalFeatureCatalog catalog;

    public OptionalFeaturesService(OptionalFeatureCatalog catalog) {
        this.catalog = catalog;
    }

    public List<OptionalFeatureSummaryDto> find(String name, String source, String featureType) {
        return catalog.summaries().stream()
                .filter(item -> name == null
                        || item.name().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))
                .filter(item -> source == null || item.source().identity().code().equalsIgnoreCase(source))
                .filter(item -> featureType == null || item.featureTypes().stream()
                        .anyMatch(type -> type.equalsIgnoreCase(featureType)))
                .toList();
    }

    public OptionalFeatureDto byId(String id) {
        var value = catalog.findById(id);
        if (value == null) {
            throw new OptionalFeatureNotFoundException("No optional feature found with ID '" + id + "'");
        }
        return OptionalFeatureDto.from(value);
    }

    public List<OptionalFeatureDto> byName(String name, String source) {
        List<OptionalFeatureDto> matches = catalog.findByName(name).stream()
                .filter(value -> source == null || value.source().identity().code().equalsIgnoreCase(source))
                .map(OptionalFeatureDto::from).toList();
        if (matches.isEmpty()) {
            throw new OptionalFeatureNotFoundException("No optional feature found with name '" + name + "'");
        }
        return matches;
    }
}
