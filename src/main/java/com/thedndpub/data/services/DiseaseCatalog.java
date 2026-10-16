package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.condition.DiseaseDefinition;
import com.thedndpub.data.dto.condition.DiseaseImportRecord;
import com.thedndpub.data.dto.condition.DiseaseMapper;
import com.thedndpub.data.dto.condition.DiseaseSummaryDto;
import com.thedndpub.data.dte.condition.ConditionsDiseasesFileDte;
import com.thedndpub.data.dte.condition.DiseaseDte;
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
public final class DiseaseCatalog {
    private static final String RESOURCE_PATH = "data/conditionsdiseases.json";

    private final Map<String, DiseaseDefinition> definitionsById;
    private final Map<String, List<DiseaseDefinition>> definitionsByName;
    private final List<DiseaseSummaryDto> summaries;
    private final Map<String, DiseaseImportRecord> importsById;

    @Autowired
    public DiseaseCatalog(ObjectMapper objectMapper, DiseaseMapper mapper, SourceRegistry sourceRegistry) {
        this(ConditionCatalog.load(objectMapper), mapper, sourceRegistry);
    }

    public DiseaseCatalog(ConditionsDiseasesFileDte source, DiseaseMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        if (source.disease() == null || source.disease().isEmpty()) {
            throw new IllegalStateException("Disease source contains no disease records");
        }
        Map<String, DiseaseDefinition> idIndex = new TreeMap<>();
        Map<String, DiseaseDefinition> identityIndex = new TreeMap<>();
        Map<String, List<DiseaseDefinition>> nameIndex = new TreeMap<>();
        Map<String, DiseaseImportRecord> importIndex = new TreeMap<>();
        List<DiseaseSummaryDto> summaryList = new ArrayList<>(source.disease().size());

        for (DiseaseDte disease : source.disease()) {
            ConditionCatalog.requireSource(sourceRegistry, disease.source());
            ConditionCatalog.validateReprints(sourceRegistry, disease.reprintedAs());
            DiseaseImportRecord imported = mapper.mapRecord(disease);
            DiseaseDefinition definition = imported.definition();
            String id = definition.id().value();
            String identity = canonicalIdentity(definition.name(), definition.source().identity().code());
            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate disease ID: " + id);
            }
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate disease name/source identity: " + identity);
            }
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            importIndex.put(id, imported);
            summaryList.add(DiseaseSummaryDto.from(definition));
        }

        summaryList.sort(Comparator
                .comparing(DiseaseSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableLists(nameIndex);
        importsById = Collections.unmodifiableMap(importIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<DiseaseSummaryDto> summaries() {
        return summaries;
    }

    public DiseaseDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<DiseaseDefinition> findByName(String name) {
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public DiseaseImportRecord importById(String id) {
        return importsById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    private static Map<String, List<DiseaseDefinition>> immutableLists(
            Map<String, List<DiseaseDefinition>> definitions) {
        Map<String, List<DiseaseDefinition>> copy = new TreeMap<>();
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
