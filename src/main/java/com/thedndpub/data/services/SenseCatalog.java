package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.sense.SenseDefinition;
import com.thedndpub.data.dto.sense.SenseImportProvenance;
import com.thedndpub.data.dto.sense.SenseImportRecord;
import com.thedndpub.data.dto.sense.SenseMapper;
import com.thedndpub.data.dto.sense.SenseSummaryDto;
import com.thedndpub.data.dte.sense.SenseDte;
import com.thedndpub.data.dte.sense.SensesFileDte;
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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Loads, validates, and indexes the complete Senses corpus at startup. */
@Component
public final class SenseCatalog {
    private static final String RESOURCE_PATH = "data/senses.json";

    private final Map<String, SenseDefinition> definitionsById;
    private final Map<String, SenseDefinition> definitionsByNameAndSource;
    private final Map<String, List<SenseDefinition>> definitionsByName;
    private final Map<String, SenseImportProvenance> provenanceById;
    private final List<SenseSummaryDto> summaries;
    private final Set<String> sourceCodes;

    @Autowired
    public SenseCatalog(ObjectMapper objectMapper, SenseMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public SenseCatalog(SensesFileDte source, SenseMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.sense() == null || source.sense().isEmpty()) {
            throw new IllegalStateException("Senses source contains no definitions");
        }

        Map<String, SenseDefinition> idIndex = new TreeMap<>();
        Map<String, SenseDefinition> identityIndex = new TreeMap<>();
        Map<String, SenseImportProvenance> provenanceIndex = new TreeMap<>();
        Map<String, List<SenseDefinition>> nameIndex = new TreeMap<>();
        Set<String> sourceIndex = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        List<SenseSummaryDto> summaryList = new ArrayList<>(source.sense().size());

        for (SenseDte sense : source.sense()) {
            SenseImportRecord imported = mapper.mapRecord(sense);
            SenseDefinition definition = imported.definition();
            String sourceCode = definition.source().identity().code();
            requireRegisteredSource(sourceRegistry, sourceCode);

            String id = definition.id().value();
            String identity = canonicalIdentity(definition.name(), sourceCode);
            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate sense ID: " + id);
            }
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate sense name/source identity: " + identity);
            }
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            sourceIndex.add(sourceCode);
            provenanceIndex.put(id, imported.provenance());
            summaryList.add(SenseSummaryDto.from(definition));
        }

        validateReprintReferences(identityIndex, provenanceIndex, sourceRegistry);
        summaryList.sort(Comparator
                .comparing(SenseSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        Map<String, List<SenseDefinition>> immutableNameIndex = new TreeMap<>();
        nameIndex.forEach((name, definitions) -> {
            definitions.sort(Comparator
                    .comparing((SenseDefinition definition) -> definition.source().identity().code(),
                            String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(definition -> definition.id().value()));
            immutableNameIndex.put(name, List.copyOf(definitions));
        });

        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByNameAndSource = Collections.unmodifiableMap(identityIndex);
        definitionsByName = Collections.unmodifiableMap(immutableNameIndex);
        provenanceById = Collections.unmodifiableMap(provenanceIndex);
        summaries = List.copyOf(summaryList);
        sourceCodes = Collections.unmodifiableSet(sourceIndex);
    }

    public List<SenseSummaryDto> summaries() {
        return summaries;
    }

    public SenseDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<SenseDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public SenseImportProvenance provenanceById(String id) {
        return provenanceById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    public Set<String> sourceCodes() {
        return sourceCodes;
    }

    private static SensesFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(SensesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load senses resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateReprintReferences(
            Map<String, SenseDefinition> definitionsByIdentity,
            Map<String, SenseImportProvenance> provenanceById,
            SourceRegistry sourceRegistry) {
        for (SenseDefinition definition : definitionsByIdentity.values()) {
            SenseImportProvenance provenance = provenanceById.get(definition.id().value());
            for (String reprint : provenance.reprintedAs()) {
                int separator = reprint.lastIndexOf('|');
                if (separator < 1 || separator == reprint.length() - 1) {
                    throw new IllegalStateException("Malformed sense reprint reference: " + reprint);
                }
                String targetName = reprint.substring(0, separator).trim();
                String targetSource = reprint.substring(separator + 1).trim();
                requireRegisteredSource(sourceRegistry, targetSource);
                if (!definitionsByIdentity.containsKey(canonicalIdentity(targetName, targetSource))) {
                    throw new IllegalStateException("Unresolved sense reprint reference: " + reprint);
                }
            }
        }
    }

    private static void requireRegisteredSource(SourceRegistry registry, String sourceCode) {
        if (registry.find(sourceCode) == null) {
            throw new IllegalStateException("Sense source code is missing from Source Registry: " + sourceCode);
        }
    }

    private static String canonicalIdentity(String name, String source) {
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
