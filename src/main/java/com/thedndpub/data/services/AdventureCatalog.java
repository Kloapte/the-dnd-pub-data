package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.adventure.AdventureDefinition;
import com.thedndpub.data.dto.adventure.AdventureImportRecord;
import com.thedndpub.data.dto.adventure.AdventureMapper;
import com.thedndpub.data.dto.adventure.AdventureSummaryDto;
import com.thedndpub.data.dte.adventure.AdventureDte;
import com.thedndpub.data.dte.adventure.AdventureFileDte;
import com.thedndpub.data.dte.book.BookContentDte;
import com.thedndpub.data.dte.book.BookHeaderDte;
import com.thedndpub.data.dte.book.StructuredBookHeaderDte;
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

/** Loads and validates publication/index metadata for the complete Adventure corpus. */
@Component
public final class AdventureCatalog {
    private static final String RESOURCE_PATH = "data/adventures.json";

    private final Map<String, AdventureDefinition> definitionsById;
    private final Map<String, AdventureDefinition> definitionsBySource;
    private final Map<String, List<AdventureDefinition>> definitionsByName;
    private final Map<String, AdventureImportRecord> importsById;
    private final List<AdventureSummaryDto> summaries;

    @Autowired
    public AdventureCatalog(ObjectMapper objectMapper, AdventureMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public AdventureCatalog(AdventureFileDte source, AdventureMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.adventure() == null || source.adventure().isEmpty()) {
            throw new IllegalStateException("Adventure source contains no publication records");
        }

        Map<String, AdventureDefinition> idIndex = new TreeMap<>();
        Map<String, AdventureDefinition> sourceIndex = new TreeMap<>();
        Map<String, List<AdventureDefinition>> nameIndex = new TreeMap<>();
        Map<String, AdventureImportRecord> importIndex = new TreeMap<>();
        List<AdventureSummaryDto> summaryList = new ArrayList<>(source.adventure().size());

        for (AdventureDte adventure : source.adventure()) {
            requireRegisteredSource(sourceRegistry, adventure.source(), "adventure source");
            AdventureImportRecord imported = mapper.mapRecord(adventure);
            validateNavigationSources(sourceRegistry, imported);
            AdventureDefinition definition = imported.definition();
            String id = definition.id().value();
            String sourceCode = normalize(definition.source().identity().code());

            putUnique(idIndex, id, definition, "adventure ID");
            putUnique(sourceIndex, sourceCode, definition, "adventure source identity");
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            importIndex.put(id, imported);
            summaryList.add(AdventureSummaryDto.from(definition));
        }

        Comparator<AdventureSummaryDto> summaryOrder = Comparator
                .comparing(AdventureSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value());
        summaryList.sort(summaryOrder);

        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsBySource = Collections.unmodifiableMap(sourceIndex);
        definitionsByName = immutableLists(nameIndex);
        importsById = Collections.unmodifiableMap(importIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<AdventureSummaryDto> summaries() {
        return summaries;
    }

    public List<AdventureDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public AdventureDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public AdventureDefinition findBySource(String source) {
        return source == null ? null : definitionsBySource.get(normalize(source));
    }

    public List<AdventureDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public AdventureImportRecord importById(String id) {
        return importsById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    private static AdventureFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(AdventureFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load adventure resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateNavigationSources(SourceRegistry sourceRegistry, AdventureImportRecord imported) {
        for (BookContentDte content : imported.provenance().contents()) {
            if (content.headers() == null) {
                continue;
            }
            for (BookHeaderDte header : content.headers()) {
                if (header instanceof StructuredBookHeaderDte structured) {
                    if (structured.source() != null) {
                        requireRegisteredSource(sourceRegistry, structured.source(), "navigation header source");
                    }
                    validateNavigationUidSource(sourceRegistry, structured.uid());
                }
            }
        }
    }

    private static void validateNavigationUidSource(SourceRegistry sourceRegistry, String uid) {
        if (uid == null) {
            return;
        }
        String[] parts = uid.split("\\|", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalStateException("Malformed adventure navigation UID: " + uid);
        }
        requireRegisteredSource(sourceRegistry, parts[1], "navigation UID source");
    }

    private static void requireRegisteredSource(SourceRegistry registry, String source, String field) {
        if (source == null || registry.find(source) == null) {
            throw new IllegalStateException("Adventure " + field + " is missing from Source Registry: " + source);
        }
    }

    private static void putUnique(
            Map<String, AdventureDefinition> index,
            String key,
            AdventureDefinition value,
            String kind) {
        AdventureDefinition existing = index.putIfAbsent(key, value);
        if (existing != null) {
            throw new IllegalStateException("Duplicate " + kind + " '" + key + "'");
        }
    }

    private static Map<String, List<AdventureDefinition>> immutableLists(
            Map<String, List<AdventureDefinition>> values) {
        Map<String, List<AdventureDefinition>> copy = new TreeMap<>();
        values.forEach((key, definitions) -> copy.put(key, definitions.stream()
                .sorted(Comparator.comparing(
                                (AdventureDefinition definition) -> definition.source().identity().code(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(definition -> definition.id().value()))
                .toList()));
        return Collections.unmodifiableMap(copy);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
