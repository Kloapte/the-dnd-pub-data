package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.action.ActionDefinition;
import com.thedndpub.data.dto.action.ActionImportRecord;
import com.thedndpub.data.dto.action.ActionMapper;
import com.thedndpub.data.dto.action.ActionSummaryDto;
import com.thedndpub.data.dte.action.ActionDte;
import com.thedndpub.data.dte.action.ActionsFileDte;
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
public final class ActionCatalog {
    private static final String RESOURCE_PATH = "data/actions.json";

    private final Map<String, ActionDefinition> definitionsById;
    private final Map<String, List<ActionDefinition>> definitionsByName;
    private final Map<String, List<ActionDefinition>> definitionsBySource;
    private final List<ActionSummaryDto> summaries;
    private final Map<String, ActionImportRecord> importsById;

    @Autowired
    public ActionCatalog(ObjectMapper objectMapper, ActionMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public ActionCatalog(ActionsFileDte source, ActionMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        if (source.action() == null || source.action().isEmpty()) {
            throw new IllegalStateException("Action source contains no action definitions");
        }

        Map<String, ActionDefinition> idIndex = new TreeMap<>();
        Map<String, ActionDefinition> identityIndex = new TreeMap<>();
        Map<String, List<ActionDefinition>> nameIndex = new TreeMap<>();
        Map<String, List<ActionDefinition>> sourceIndex = new TreeMap<>();
        Map<String, ActionImportRecord> importIndex = new TreeMap<>();
        List<ActionSummaryDto> summaryList = new ArrayList<>(source.action().size());

        for (ActionDte action : source.action()) {
            validateSources(sourceRegistry, action);
            ActionImportRecord imported = mapper.mapRecord(action);
            ActionDefinition definition = imported.definition();
            String id = definition.id().value();
            String name = normalize(definition.name());
            String sourceCode = normalize(definition.source().identity().code());
            String identity = canonicalIdentity(name, sourceCode);

            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate action ID: " + id);
            }
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate action name/source identity: " + identity);
            }
            nameIndex.computeIfAbsent(name, ignored -> new ArrayList<>()).add(definition);
            sourceIndex.computeIfAbsent(sourceCode, ignored -> new ArrayList<>()).add(definition);
            importIndex.put(id, imported);
            summaryList.add(ActionSummaryDto.from(definition));
        }

        summaryList.sort(Comparator
                .comparing(ActionSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableLists(nameIndex);
        definitionsBySource = immutableLists(sourceIndex);
        importsById = Collections.unmodifiableMap(importIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<ActionSummaryDto> summaries() {
        return summaries;
    }

    public ActionDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<ActionDefinition> findByName(String name) {
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public List<ActionDefinition> findBySource(String source) {
        return definitionsBySource.getOrDefault(normalize(source), List.of());
    }

    public ActionImportRecord importById(String id) {
        return importsById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    private static ActionsFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(ActionsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load action resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateSources(SourceRegistry registry, ActionDte action) {
        requireSource(registry, action.source());
        validateEncodedSource(registry, action.fromVariant());
        if (action.seeAlsoAction() != null) {
            action.seeAlsoAction().forEach(reference -> validateEncodedSource(registry, reference));
        }
        if (action.reprintedAs() != null) {
            for (var reference : action.reprintedAs()) {
                String[] parts = reference.uid().split("\\|", -1);
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new IllegalStateException("Invalid action reprint reference: " + reference.uid());
                }
                requireSource(registry, parts[1]);
            }
        }
    }

    private static void validateEncodedSource(SourceRegistry registry, String value) {
        if (value == null) {
            return;
        }
        int separator = value.lastIndexOf('|');
        if (separator >= 0 && separator < value.length() - 1) {
            requireSource(registry, value.substring(separator + 1));
        }
    }

    private static void requireSource(SourceRegistry registry, String source) {
        if (registry != null && registry.find(source) == null) {
            throw new IllegalStateException("Source code is missing from Source Registry: " + source);
        }
    }

    private static <T> Map<String, List<T>> immutableLists(Map<String, List<T>> values) {
        Map<String, List<T>> copy = new TreeMap<>();
        values.forEach((key, records) -> copy.put(key, List.copyOf(records)));
        return Collections.unmodifiableMap(copy);
    }

    private static String canonicalIdentity(String name, String source) {
        return name + "\u0000" + source;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
