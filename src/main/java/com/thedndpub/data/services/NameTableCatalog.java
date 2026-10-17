package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.nametable.NameTableDefinition;
import com.thedndpub.data.dto.nametable.NameTableMapper;
import com.thedndpub.data.dto.nametable.NameTableSummaryDto;
import com.thedndpub.data.dte.nametable.NameTableDte;
import com.thedndpub.data.dte.nametable.NameTablesFileDte;
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

/** Immutable name-table catalog; table results remain internal pending publication review. */
@Component
public final class NameTableCatalog {
    private static final String RESOURCE_PATH = "data/names.json";

    private final Map<String, NameTableDefinition> definitionsById;
    private final Map<String, List<NameTableDefinition>> definitionsByName;
    private final List<NameTableSummaryDto> summaries;

    @Autowired
    public NameTableCatalog(ObjectMapper objectMapper, NameTableMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public NameTableCatalog(NameTablesFileDte source, NameTableMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.name() == null || source.name().isEmpty()) {
            throw new IllegalStateException("Name-table source contains no definitions");
        }

        Map<String, NameTableDefinition> idIndex = new TreeMap<>();
        Map<String, List<NameTableDefinition>> nameIndex = new TreeMap<>();
        List<NameTableSummaryDto> summaryList = new ArrayList<>(source.name().size());
        for (NameTableDte nameTable : source.name()) {
            if (nameTable == null) {
                throw new IllegalStateException("Name-table source contains a null definition");
            }
            if (sourceRegistry.find(nameTable.source()) == null) {
                throw new IllegalStateException(
                        "Name-table source is missing from Source Registry: " + nameTable.source());
            }
            NameTableDefinition definition = mapper.map(nameTable);
            String id = definition.id().value();
            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate name-table ID '" + id + "'");
            }
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(NameTableSummaryDto.from(definition));
        }

        summaryList.sort(Comparator
                .comparing(NameTableSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableLists(nameIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<NameTableSummaryDto> summaries() {
        return summaries;
    }

    public List<NameTableDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public NameTableDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<NameTableDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public List<NameTableDefinition> findBySource(String source) {
        if (source == null || source.isBlank()) {
            return List.of();
        }
        return definitionsById.values().stream()
                .filter(definition -> definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .sorted(definitionOrder())
                .toList();
    }

    public int size() {
        return definitionsById.size();
    }

    private static NameTablesFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(NameTablesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load name-table resource " + RESOURCE_PATH, exception);
        }
    }

    private static Map<String, List<NameTableDefinition>> immutableLists(
            Map<String, List<NameTableDefinition>> values) {
        Map<String, List<NameTableDefinition>> copy = new TreeMap<>();
        values.forEach((key, definitions) -> copy.put(key, definitions.stream().sorted(definitionOrder()).toList()));
        return Collections.unmodifiableMap(copy);
    }

    private static Comparator<NameTableDefinition> definitionOrder() {
        return Comparator.comparing(NameTableDefinition::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(definition -> definition.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(definition -> definition.id().value());
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
