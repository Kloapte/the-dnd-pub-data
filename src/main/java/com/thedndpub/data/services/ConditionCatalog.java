package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.condition.ConditionDefinition;
import com.thedndpub.data.dto.condition.ConditionImportRecord;
import com.thedndpub.data.dto.condition.ConditionMapper;
import com.thedndpub.data.dto.condition.ConditionSummaryDto;
import com.thedndpub.data.dte.condition.ConditionDte;
import com.thedndpub.data.dte.condition.ConditionsDiseasesFileDte;
import com.thedndpub.data.dte.condition.StatusDte;
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
public final class ConditionCatalog {
    private static final String RESOURCE_PATH = "data/conditionsdiseases.json";

    private final Map<String, ConditionDefinition> definitionsById;
    private final Map<String, List<ConditionDefinition>> definitionsByName;
    private final List<ConditionSummaryDto> summaries;
    private final Map<String, ConditionImportRecord> importsById;
    private final int statusRecordCount;

    @Autowired
    public ConditionCatalog(ObjectMapper objectMapper, ConditionMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public ConditionCatalog(ConditionsDiseasesFileDte source, ConditionMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        List<ConditionDte> conditions = requireRecords(source.condition(), "condition");
        List<StatusDte> statuses = requireRecords(source.status(), "status");
        Map<String, ConditionDefinition> idIndex = new TreeMap<>();
        Map<String, ConditionDefinition> identityIndex = new TreeMap<>();
        Map<String, List<ConditionDefinition>> nameIndex = new TreeMap<>();
        Map<String, ConditionImportRecord> importIndex = new TreeMap<>();
        List<ConditionSummaryDto> summaryList = new ArrayList<>(conditions.size());

        for (ConditionDte condition : conditions) {
            validateSources(sourceRegistry, condition);
            ConditionImportRecord imported = mapper.mapRecord(condition);
            ConditionDefinition definition = imported.definition();
            String id = definition.id().value();
            String identity = canonicalIdentity(definition.name(), definition.source().identity().code());
            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate condition ID: " + id);
            }
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate condition name/source identity: " + identity);
            }
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            importIndex.put(id, imported);
            summaryList.add(ConditionSummaryDto.from(definition));
        }

        for (StatusDte status : statuses) {
            requireSource(sourceRegistry, status.source());
            validateReprints(sourceRegistry, status.reprintedAs());
        }
        summaryList.sort(Comparator
                .comparing(ConditionSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableLists(nameIndex);
        importsById = Collections.unmodifiableMap(importIndex);
        summaries = List.copyOf(summaryList);
        statusRecordCount = statuses.size();
    }

    public List<ConditionSummaryDto> summaries() {
        return summaries;
    }

    public ConditionDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<ConditionDefinition> findByName(String name) {
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public ConditionImportRecord importById(String id) {
        return importsById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    public int statusRecordCount() {
        return statusRecordCount;
    }

    static ConditionsDiseasesFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(ConditionsDiseasesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load condition resource " + RESOURCE_PATH, exception);
        }
    }

    private static <T> List<T> requireRecords(List<T> records, String collection) {
        if (records == null || records.isEmpty()) {
            throw new IllegalStateException("Condition source contains no " + collection + " records");
        }
        return records;
    }

    private static void validateSources(SourceRegistry registry, ConditionDte condition) {
        requireSource(registry, condition.source());
        if (condition.otherSources() != null) {
            condition.otherSources().forEach(reference -> requireSource(registry, reference.source()));
        }
        validateReprints(registry, condition.reprintedAs());
    }

    static void validateReprints(SourceRegistry registry, List<String> references) {
        if (references == null) {
            return;
        }
        for (String reference : references) {
            String[] parts = reference.split("\\|", -1);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new IllegalStateException("Invalid reprint reference: " + reference);
            }
            requireSource(registry, parts[1]);
        }
    }

    static void requireSource(SourceRegistry registry, String source) {
        if (registry != null && registry.find(source) == null) {
            throw new IllegalStateException("Source code is missing from Source Registry: " + source);
        }
    }

    private static Map<String, List<ConditionDefinition>> immutableLists(
            Map<String, List<ConditionDefinition>> definitions) {
        Map<String, List<ConditionDefinition>> copy = new TreeMap<>();
        definitions.forEach((name, records) -> copy.put(name, List.copyOf(records)));
        return Collections.unmodifiableMap(copy);
    }

    private static String canonicalIdentity(String name, String source) {
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
