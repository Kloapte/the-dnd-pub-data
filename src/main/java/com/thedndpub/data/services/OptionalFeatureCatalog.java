package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureDefinition;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureMapper;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureSummaryDto;
import com.thedndpub.data.dte.optionalfeature.OptionalFeatureDte;
import com.thedndpub.data.dte.optionalfeature.OptionalFeaturesFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Component
public final class OptionalFeatureCatalog {
    private static final String RESOURCE_PATH = "data/optionalfeatures.json";
    private final Map<String, OptionalFeatureDefinition> byId;
    private final Map<String, List<OptionalFeatureDefinition>> byName;
    private final List<OptionalFeatureSummaryDto> summaries;
    private final int rootCount;
    private final int expandedCount;

    @Autowired
    public OptionalFeatureCatalog(ObjectMapper objectMapper, OptionalFeatureMapper mapper, SourceRegistry registry) {
        this(load(objectMapper), mapper, registry);
    }

    public OptionalFeatureCatalog(OptionalFeaturesFileDte source, OptionalFeatureMapper mapper, SourceRegistry registry) {
        Map<String, OptionalFeatureDefinition> ids = new TreeMap<>();
        Map<String, OptionalFeatureDefinition> identities = new TreeMap<>();
        Map<String, List<OptionalFeatureDefinition>> names = new TreeMap<>();
        List<OptionalFeatureSummaryDto> summaryList = new ArrayList<>();
        if (source.optionalfeature() == null || source.optionalfeature().isEmpty()) {
            throw new IllegalStateException("Optional feature source contains no definitions");
        }
        for (OptionalFeatureDte dte : source.optionalfeature()) {
            requireSource(registry, dte.source());
            if (dte.otherSources() != null) {
                dte.otherSources().forEach(reference -> requireSource(registry, reference.source()));
            }
            if (dte.reprintedAs() != null) {
                dte.reprintedAs().forEach(value -> requireSource(registry, referenceSource(value.uid())));
            }
            validateStructuredReferenceSources(registry, dte);
            OptionalFeatureDefinition definition = mapper.map(dte);
            if (ids.putIfAbsent(definition.id(), definition) != null) {
                throw new IllegalStateException("Duplicate optional feature ID: " + definition.id());
            }
            String identity = normalize(definition.name()) + "\u0000" + normalize(definition.source().identity().code());
            if (identities.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate optional feature name/source: " + identity);
            }
            names.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(OptionalFeatureSummaryDto.from(definition));
        }
        summaryList.sort(Comparator.comparing(OptionalFeatureSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(item -> item.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(OptionalFeatureSummaryDto::id));
        Map<String, List<OptionalFeatureDefinition>> immutableNames = new TreeMap<>();
        names.forEach((key, values) -> immutableNames.put(key, List.copyOf(values)));
        byId = Collections.unmodifiableMap(ids);
        byName = Collections.unmodifiableMap(immutableNames);
        summaries = List.copyOf(summaryList);
        rootCount = source.optionalfeature().size();
        expandedCount = 0;
    }

    public List<OptionalFeatureSummaryDto> summaries() {
        return summaries;
    }

    public int size() {
        return byId.size();
    }

    public int rootCount() {
        return rootCount;
    }

    public int expandedCount() {
        return expandedCount;
    }

    public OptionalFeatureDefinition findById(String id) {
        return byId.get(id);
    }

    public List<OptionalFeatureDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<OptionalFeatureDefinition> findByName(String name) {
        return byName.getOrDefault(normalize(name), List.of());
    }

    private static OptionalFeaturesFileDte load(ObjectMapper mapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return mapper.readerFor(OptionalFeaturesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load optional feature resource " + RESOURCE_PATH, exception);
        }
    }

    private static void requireSource(SourceRegistry registry, String code) {
        if (registry.find(code) == null) {
            throw new IllegalStateException("Source code is missing from Source Registry: " + code);
        }
    }

    private static String referenceSource(String encoded) {
        String[] pieces = encoded.split("\\|", -1);
        if (pieces.length != 2 || pieces[1].isBlank()) {
            throw new IllegalStateException("Invalid reprint source reference: " + encoded);
        }
        return pieces[1];
    }

    private static void validateStructuredReferenceSources(SourceRegistry registry, OptionalFeatureDte feature) {
        if (feature.prerequisite() == null) {
            return;
        }
        for (var prerequisite : feature.prerequisite()) {
            if (prerequisite.level() != null) {
                if (prerequisite.level().classReference() != null) {
                    requireOptionalSource(registry, prerequisite.level().classReference().source());
                }
                if (prerequisite.level().subclass() != null) {
                    requireOptionalSource(registry, prerequisite.level().subclass().source());
                }
            }
            if (prerequisite.optionalfeature() != null) {
                prerequisite.optionalfeature().forEach(value -> validateUidSource(registry, value));
            }
            if (prerequisite.item() != null) {
                prerequisite.item().forEach(value -> validateUidSource(registry, value));
            }
            if (prerequisite.spell() != null) {
                prerequisite.spell().forEach(value -> {
                    validateUidSource(registry, value.spell());
                    validateUidSource(registry, value.choose());
                });
            }
        }
        if (feature.additionalSpells() != null) {
            feature.additionalSpells().forEach(grant -> {
                if (grant.innate() != null) {
                    grant.innate().spells().forEach(value -> validateUidSource(registry, value));
                    grant.innate().daily().values().stream().flatMap(List::stream)
                            .forEach(value -> validateUidSource(registry, value));
                    grant.innate().resource().values().stream().flatMap(List::stream)
                            .forEach(value -> validateUidSource(registry, value));
                    if (grant.innate().ritual() != null) {
                        grant.innate().ritual().forEach(value -> validateUidSource(registry, value.choose()));
                    }
                }
                if (grant.known() != null && grant.known().values() != null) {
                    grant.known().values().forEach(value -> validateUidSource(registry, value.choose()));
                }
                if (grant.prepared() != null && grant.prepared().values() != null) {
                    grant.prepared().values().forEach(value -> validateUidSource(registry, value.choose()));
                }
            });
        }
    }

    private static void validateUidSource(SourceRegistry registry, String value) {
        if (value == null) {
            return;
        }
        String[] parts = value.split("\\|", -1);
        String candidate = parts[parts.length - 1];
        if (candidate.matches("[A-Z][A-Z0-9.]+")) {
            requireSource(registry, candidate);
        }
    }

    private static void requireOptionalSource(SourceRegistry registry, String source) {
        if (source != null) {
            requireSource(registry, source);
        }
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT);
    }
}
