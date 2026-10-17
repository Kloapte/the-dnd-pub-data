package com.thedndpub.data.services;

import com.thedndpub.data.dto.cultboon.BoonDefinition;
import com.thedndpub.data.dto.cultboon.BoonDto;
import com.thedndpub.data.dto.cultboon.BoonImportRecord;
import com.thedndpub.data.dto.cultboon.BoonSummaryDto;
import com.thedndpub.data.dto.cultboon.CultBoonMapper;
import com.thedndpub.data.dte.cultboon.BoonDte;
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

/** Immutable catalog index for Boon definitions. */
@Component
public final class BoonCatalog {
    private final Map<String, BoonDefinition> definitionsById;
    private final Map<String, List<BoonDefinition>> definitionsByName;
    private final Map<String, BoonImportRecord> importsById;
    private final List<BoonSummaryDto> summaries;

    @Autowired
    public BoonCatalog(
            CultsAndBoonsSourceLoader sourceLoader,
            CultBoonMapper mapper,
            CultsAndBoonsSourceValidator sourceValidator) {
        this(sourceLoader.source().boon(), mapper, sourceValidator);
    }

    public BoonCatalog(
            List<BoonDte> source,
            CultBoonMapper mapper,
            CultsAndBoonsSourceValidator sourceValidator) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceValidator, "sourceValidator");
        if (source.isEmpty()) {
            throw new IllegalStateException("Boon source collection must not be empty");
        }

        Map<String, BoonDefinition> idIndex = new TreeMap<>();
        Map<String, BoonDefinition> identityIndex = new TreeMap<>();
        Map<String, List<BoonDefinition>> nameIndex = new TreeMap<>();
        Map<String, BoonImportRecord> importIndex = new TreeMap<>();
        List<BoonSummaryDto> summaryList = new ArrayList<>(source.size());
        for (BoonDte sourceBoon : source) {
            if (sourceBoon == null) {
                throw new IllegalStateException("Boon source contains a null record");
            }
            BoonImportRecord imported = mapper.mapBoon(sourceBoon);
            BoonDefinition definition = imported.definition();
            sourceValidator.validate(imported);
            putUnique(idIndex, definition.id().value(), definition, "Boon ID");
            putUnique(identityIndex, identity(definition.name(), definition.source().identity().code()),
                    definition, "Boon name/source identity");
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            importIndex.put(definition.id().value(), imported);
            summaryList.add(BoonSummaryDto.from(definition));
        }

        summaryList.sort(Comparator.comparing(BoonSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableNameIndex(nameIndex);
        importsById = Collections.unmodifiableMap(importIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<BoonDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public List<BoonSummaryDto> summaries() {
        return summaries;
    }

    public BoonDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<BoonDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public BoonImportRecord importById(String id) {
        return importsById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    private static void putUnique(
            Map<String, BoonDefinition> index,
            String key,
            BoonDefinition definition,
            String identityType) {
        if (index.putIfAbsent(key, definition) != null) {
            throw new IllegalStateException("Duplicate " + identityType + ": " + key);
        }
    }

    private static Map<String, List<BoonDefinition>> immutableNameIndex(
            Map<String, List<BoonDefinition>> source) {
        Map<String, List<BoonDefinition>> result = new TreeMap<>();
        source.forEach((name, definitions) -> result.put(name, definitions.stream()
                .sorted(Comparator.comparing(
                                (BoonDefinition definition) -> definition.source().identity().code(),
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
