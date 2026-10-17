package com.thedndpub.data.services;

import com.thedndpub.data.dto.cultboon.CultDefinition;
import com.thedndpub.data.dto.cultboon.CultDto;
import com.thedndpub.data.dto.cultboon.CultImportRecord;
import com.thedndpub.data.dto.cultboon.CultSummaryDto;
import com.thedndpub.data.dto.cultboon.CultBoonMapper;
import com.thedndpub.data.dte.cultboon.CultDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Immutable catalog index for Cult definitions. */
@Component
public final class CultCatalog {
    private final Map<String, CultDefinition> definitionsById;
    private final Map<String, List<CultDefinition>> definitionsByName;
    private final Map<String, CultImportRecord> importsById;
    private final List<CultSummaryDto> summaries;

    @Autowired
    public CultCatalog(
            CultsAndBoonsSourceLoader sourceLoader,
            CultBoonMapper mapper,
            CultsAndBoonsSourceValidator sourceValidator) {
        this(sourceLoader.source().cult(), mapper, sourceValidator);
    }

    public CultCatalog(
            List<CultDte> source,
            CultBoonMapper mapper,
            CultsAndBoonsSourceValidator sourceValidator) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceValidator, "sourceValidator");
        if (source.isEmpty()) {
            throw new IllegalStateException("Cult source collection must not be empty");
        }

        Map<String, CultDefinition> idIndex = new TreeMap<>();
        Map<String, CultDefinition> identityIndex = new TreeMap<>();
        Map<String, List<CultDefinition>> nameIndex = new TreeMap<>();
        Map<String, CultImportRecord> importIndex = new TreeMap<>();
        List<CultSummaryDto> summaryList = new ArrayList<>(source.size());
        for (CultDte sourceCult : source) {
            if (sourceCult == null) {
                throw new IllegalStateException("Cult source contains a null record");
            }
            CultImportRecord imported = mapper.mapCult(sourceCult);
            CultDefinition definition = imported.definition();
            sourceValidator.validate(imported);
            putUnique(idIndex, definition.id().value(), definition, "Cult ID");
            putUnique(identityIndex, identity(definition.name(), definition.source().identity().code()),
                    definition, "Cult name/source identity");
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            importIndex.put(definition.id().value(), imported);
            summaryList.add(CultSummaryDto.from(definition));
        }

        summaryList.sort(Comparator.comparing(CultSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableNameIndex(nameIndex);
        importsById = Collections.unmodifiableMap(importIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<CultDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public List<CultSummaryDto> summaries() {
        return summaries;
    }

    public CultDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<CultDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public CultImportRecord importById(String id) {
        return importsById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    private static void putUnique(
            Map<String, CultDefinition> index,
            String key,
            CultDefinition definition,
            String identityType) {
        if (index.putIfAbsent(key, definition) != null) {
            throw new IllegalStateException("Duplicate " + identityType + ": " + key);
        }
    }

    private static Map<String, List<CultDefinition>> immutableNameIndex(
            Map<String, List<CultDefinition>> source) {
        Map<String, List<CultDefinition>> result = new TreeMap<>();
        source.forEach((name, definitions) -> result.put(name, definitions.stream()
                .sorted(Comparator.comparing(
                                (CultDefinition definition) -> definition.source().identity().code(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(definition -> definition.id().value()))
                .toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String identity(String name, String source) {
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
