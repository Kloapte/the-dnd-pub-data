package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.feat.FeatDefinition;
import com.thedndpub.data.dto.feat.FeatMapper;
import com.thedndpub.data.dto.feat.FeatImportRecord;
import com.thedndpub.data.dto.feat.FeatExpansionProvenance;
import com.thedndpub.data.dto.feat.FeatSummaryDto;
import com.thedndpub.data.dte.feat.FeatDte;
import com.thedndpub.data.dte.feat.ExpandedFeatDte;
import com.thedndpub.data.dte.feat.FeatVersionExpander;
import com.thedndpub.data.dte.feat.FeatsFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Component
public final class FeatCatalog {
    private static final String RESOURCE_PATH = "data/feats.json";
    private final Map<String, FeatDefinition> byId;
    private final Map<String, List<FeatDefinition>> byName;
    private final Map<String, List<FeatDefinition>> bySource;
    private final Map<String, FeatImportRecord> importsById;
    private final List<FeatSummaryDto> summaries;
    private final int rootCount;
    private final int variantCount;

    @Autowired
    public FeatCatalog(
            ObjectMapper objectMapper,
            FeatMapper mapper,
            SourceRegistry sourceRegistry,
            FeatVersionExpander versionExpander) {
        this(load(objectMapper), mapper, sourceRegistry, versionExpander);
    }

    public FeatCatalog(FeatsFileDte source, FeatMapper mapper, SourceRegistry sourceRegistry) {
        this(source, mapper, sourceRegistry, new FeatVersionExpander());
    }

    public FeatCatalog(
            FeatsFileDte source,
            FeatMapper mapper,
            SourceRegistry sourceRegistry,
            FeatVersionExpander versionExpander) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(versionExpander, "versionExpander");
        if (source.feat() == null || source.feat().isEmpty()) {
            throw new IllegalStateException("Feat source contains no feat definitions");
        }
        Map<String, FeatDefinition> idIndex = new TreeMap<>();
        Map<String, FeatDefinition> identities = new TreeMap<>();
        Map<String, List<FeatDefinition>> nameIndex = new TreeMap<>();
        Map<String, List<FeatDefinition>> sourceIndex = new TreeMap<>();
        Map<String, FeatImportRecord> importIndex = new TreeMap<>();
        List<FeatSummaryDto> summaryList = new ArrayList<>();
        int variantDefinitions = 0;
        for (FeatDte feat : source.feat()) {
            validateSources(sourceRegistry, feat);
            List<ExpandedFeatDte> definitions = versionExpander.expand(feat);
            for (ExpandedFeatDte expanded : definitions) {
                FeatDefinition definition = mapper.map(expanded);
                String normalizedName = normalize(definition.name());
                String normalizedSource = normalize(definition.source().identity().code());
                if (idIndex.putIfAbsent(definition.id().value(), definition) != null) {
                    throw new IllegalStateException("Duplicate feat ID: " + definition.id().value());
                }
                String identity = normalizedName + "\u0000" + normalizedSource;
                if (identities.putIfAbsent(identity, definition) != null) {
                    throw new IllegalStateException("Duplicate feat name/source identity: " + identity);
                }
                if (definition.variant() && !idIndex.containsKey(definition.parentId().value())) {
                    throw new IllegalStateException("Feat variant parent is missing: " + definition.parentId().value());
                }
                if (definition.variant()) {
                    requireSource(sourceRegistry, definition.source().identity().code());
                    variantDefinitions++;
                }
                nameIndex.computeIfAbsent(normalizedName, ignored -> new ArrayList<>()).add(definition);
                sourceIndex.computeIfAbsent(normalizedSource, ignored -> new ArrayList<>()).add(definition);
                FeatExpansionProvenance provenance = expanded.isVariant()
                        ? new FeatExpansionProvenance(definition.parentId(), expanded.versionIndex(), expanded.declaration())
                        : null;
                importIndex.put(definition.id().value(), new FeatImportRecord(definition, feat, provenance));
                summaryList.add(FeatSummaryDto.from(definition));
            }
        }
        summaryList.sort(Comparator.comparing(FeatSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        byId = Collections.unmodifiableMap(idIndex);
        Map<String, List<FeatDefinition>> immutableNameIndex = new TreeMap<>();
        nameIndex.forEach((key, values) -> immutableNameIndex.put(key, List.copyOf(values)));
        byName = Collections.unmodifiableMap(immutableNameIndex);
        Map<String, List<FeatDefinition>> immutableSourceIndex = new TreeMap<>();
        sourceIndex.forEach((key, values) -> immutableSourceIndex.put(key, List.copyOf(values)));
        bySource = Collections.unmodifiableMap(immutableSourceIndex);
        importsById = Collections.unmodifiableMap(importIndex);
        summaries = List.copyOf(summaryList);
        rootCount = source.feat().size();
        variantCount = variantDefinitions;
    }

    public List<FeatSummaryDto> summaries() {
        return summaries;
    }

    public FeatDefinition findById(String id) {
        return byId.get(id);
    }

    public List<FeatDefinition> findByName(String name) {
        return byName.getOrDefault(normalize(name), List.of());
    }

    public List<FeatDefinition> findBySource(String source) {
        return bySource.getOrDefault(normalize(source), List.of());
    }

    public FeatImportRecord importById(String id) {
        return importsById.get(id);
    }

    public int size() {
        return byId.size();
    }

    public int rootCount() {
        return rootCount;
    }

    public int variantCount() {
        return variantCount;
    }

    private static FeatsFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(FeatsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load feat resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateSources(SourceRegistry registry, FeatDte feat) {
        requireSource(registry, feat.source());
        if (feat.additionalSources() != null) {
            feat.additionalSources().forEach(reference -> requireSource(registry, reference.source()));
        }
        if (feat.reprintedAs() != null) {
            feat.reprintedAs().forEach(reference -> requireEncodedSource(registry, reference));
        }
        if (feat.versions() != null) {
            feat.versions().forEach(version -> requireSource(registry, version.source()));
        }
    }

    private static void requireEncodedSource(SourceRegistry registry, String value) {
        String[] parts = value.split("\\|", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalStateException("Invalid feat source reference: " + value);
        }
        requireSource(registry, parts[1]);
    }

    private static void requireSource(SourceRegistry registry, String source) {
        if (registry != null && registry.find(source) == null) {
            throw new IllegalStateException("Source code is missing from Source Registry: " + source);
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
