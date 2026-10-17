package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.treasure.ArtObjectTableDefinition;
import com.thedndpub.data.dto.treasure.GemTableDefinition;
import com.thedndpub.data.dto.treasure.TreasureTableDefinition;
import com.thedndpub.data.dto.treasure.TreasureTableMapper;
import com.thedndpub.data.dto.treasure.TreasureTableSummaryDto;
import com.thedndpub.data.dte.treasure.DragonMundaneItemRowDte;
import com.thedndpub.data.dte.treasure.LootFileDte;
import com.thedndpub.data.references.ReferenceUidParser;
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
import java.util.Objects;
import java.util.TreeMap;

@Component
public final class TreasureTableCatalog {
    private static final String RESOURCE_PATH = "data/loot.json";

    private final Map<String, TreasureTableDefinition> definitionsById;
    private final Map<String, List<TreasureTableDefinition>> definitionsByName;
    private final List<TreasureTableSummaryDto> summaries;
    private final List<DragonMundaneItemRowDte> dragonMundaneItemRows;
    private final LootFileDte sourceData;

    @Autowired
    public TreasureTableCatalog(
            ObjectMapper objectMapper,
            TreasureTableMapper mapper,
            SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public TreasureTableCatalog(
            LootFileDte source,
            TreasureTableMapper mapper,
            SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        validateCollections(source);
        sourceData = source;
        dragonMundaneItemRows = List.copyOf(source.dragonMundaneItems());
        validateDragonMundaneItems(dragonMundaneItemRows);

        List<TreasureTableDefinition> definitions = new ArrayList<>();
        source.individual().forEach(value -> definitions.add(mapper.map(value)));
        source.hoard().forEach(value -> definitions.add(mapper.map(value)));
        source.dragon().forEach(value -> definitions.add(mapper.map(value, dragonMundaneItemRows.size())));
        source.gems().forEach(value -> definitions.add(mapper.map(value)));
        source.artObjects().forEach(value -> definitions.add(mapper.map(value)));
        source.magicItems().forEach(value -> definitions.add(mapper.map(value)));

        Map<String, TreasureTableDefinition> idIndex = new TreeMap<>();
        Map<String, List<TreasureTableDefinition>> nameIndex = new TreeMap<>();
        List<TreasureTableSummaryDto> summaryList = new ArrayList<>(definitions.size());
        for (TreasureTableDefinition definition : definitions) {
            validateSource(definition.source().identity().code(), sourceRegistry);
            if (idIndex.putIfAbsent(definition.id().value(), definition) != null) {
                throw new IllegalStateException("Duplicate treasure table ID: " + definition.id().value());
            }
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(TreasureTableSummaryDto.from(definition));
        }
        validateReprints(definitions, idIndex, sourceRegistry);

        summaryList.sort(summaryOrder());
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableNameIndex(nameIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<TreasureTableDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public List<TreasureTableSummaryDto> summaries() {
        return summaries;
    }

    public List<DragonMundaneItemRowDte> dragonMundaneItemRows() {
        return dragonMundaneItemRows;
    }

    public LootFileDte sourceData() {
        return sourceData;
    }

    public TreasureTableDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<TreasureTableDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return definitionsById.size();
    }

    private static LootFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(LootFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load treasure tables resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateCollections(LootFileDte source) {
        if (source.individual() == null || source.individual().isEmpty()
                || source.hoard() == null || source.hoard().isEmpty()
                || source.dragon() == null || source.dragon().isEmpty()
                || source.gems() == null || source.gems().isEmpty()
                || source.artObjects() == null || source.artObjects().isEmpty()
                || source.magicItems() == null || source.magicItems().isEmpty()
                || source.dragonMundaneItems() == null || source.dragonMundaneItems().isEmpty()) {
            throw new IllegalStateException("Treasure source must contain all seven non-empty collections");
        }
    }

    private static void validateDragonMundaneItems(List<DragonMundaneItemRowDte> rows) {
        for (DragonMundaneItemRowDte row : rows) {
            if (row == null || row.min() == null || row.max() == null || row.item() == null || row.item().isBlank()) {
                throw new IllegalStateException("Dragon mundane item row is missing an authored field");
            }
        }
    }

    private static void validateSource(String source, SourceRegistry sourceRegistry) {
        if (sourceRegistry.find(source) == null) {
            throw new IllegalStateException("Treasure table uses unknown source identity: " + source);
        }
    }

    private static void validateReprints(
            List<TreasureTableDefinition> definitions,
            Map<String, TreasureTableDefinition> idIndex,
            SourceRegistry sourceRegistry) {
        for (TreasureTableDefinition definition : definitions) {
            List<String> targets = null;
            if (definition instanceof GemTableDefinition gems) {
                targets = gems.authoredData().reprintedAs();
            } else if (definition instanceof ArtObjectTableDefinition art) {
                targets = art.authoredData().reprintedAs();
            }
            if (targets == null) {
                continue;
            }
            for (String target : targets) {
                var parsed = ReferenceUidParser.parse(target);
                if (parsed.source() == null) {
                    throw new IllegalStateException("Treasure reprint UID lacks authored source: " + target);
                }
                validateSource(parsed.source(), sourceRegistry);
                String targetId = "treasure-table:" + parsed.source() + ":"
                        + definition.kind().sourceCollection() + ":" + slug(parsed.name());
                if (!idIndex.containsKey(targetId)) {
                    throw new IllegalStateException("Treasure reprint UID does not identify a table: " + target);
                }
            }
        }
    }

    private static Map<String, List<TreasureTableDefinition>> immutableNameIndex(
            Map<String, List<TreasureTableDefinition>> values) {
        Map<String, List<TreasureTableDefinition>> result = new TreeMap<>();
        values.forEach((name, definitions) -> result.put(name, definitions.stream().sorted(definitionOrder()).toList()));
        return Collections.unmodifiableMap(result);
    }

    private static Comparator<TreasureTableSummaryDto> summaryOrder() {
        return Comparator.comparing(TreasureTableSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(TreasureTableSummaryDto::collectionType)
                .thenComparing(value -> value.id().value());
    }

    private static Comparator<TreasureTableDefinition> definitionOrder() {
        return Comparator.comparing(TreasureTableDefinition::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.kind().sourceCollection())
                .thenComparing(value -> value.id().value());
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }

    private static String slug(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
