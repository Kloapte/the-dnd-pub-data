package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.object.ObjectDefinition;
import com.thedndpub.data.dto.object.ObjectDefinitionMapper;
import com.thedndpub.data.dto.object.ObjectSummaryDto;
import com.thedndpub.data.dte.object.ObjectFileDte;
import com.thedndpub.data.dte.object.ObjectSourceDte;
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
public final class ObjectCatalog {
    private static final String RESOURCE = "data/objects.json";
    private final Map<String, ObjectDefinition> byId;
    private final Map<String, List<ObjectDefinition>> byName;
    private final List<ObjectSummaryDto> summaries;
    private final Map<String, ObjectSourceDte> sourceById;

    @Autowired
    public ObjectCatalog(ObjectMapper objectMapper, ObjectDefinitionMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public ObjectCatalog(ObjectFileDte source, ObjectDefinitionMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.object() == null || source.object().isEmpty()) {
            throw new IllegalStateException("Object source collection must not be empty");
        }
        Map<String, ObjectDefinition> ids = new TreeMap<>();
        Map<String, ObjectDefinition> identities = new TreeMap<>();
        Map<String, List<ObjectDefinition>> names = new TreeMap<>();
        Map<String, ObjectSourceDte> originals = new TreeMap<>();
        List<ObjectSummaryDto> summariesBuilder = new ArrayList<>();
        for (ObjectSourceDte record : source.object()) {
            if (record == null || record.name() == null || record.name().isBlank()
                    || record.source() == null || record.source().isBlank()) {
                throw new IllegalStateException("Object source contains a record without name/source identity");
            }
            validateSources(record, sourceRegistry);
            ObjectDefinition definition = mapper.map(record);
            putUnique(ids, definition.id(), definition, "Object ID");
            putUnique(identities, normalize(record.source()) + "\u0000" + normalize(record.name()), definition,
                    "Object source/name identity");
            names.computeIfAbsent(normalize(record.name()), ignored -> new ArrayList<>()).add(definition);
            originals.put(definition.id(), record);
            summariesBuilder.add(ObjectSummaryDto.from(definition));
        }
        Comparator<ObjectSummaryDto> ordering = Comparator.comparing(ObjectSummaryDto::name,
                String.CASE_INSENSITIVE_ORDER).thenComparing(summary -> summary.source().identity().code(),
                String.CASE_INSENSITIVE_ORDER).thenComparing(ObjectSummaryDto::id);
        summariesBuilder.sort(ordering);
        byId = Collections.unmodifiableMap(ids);
        byName = immutableNameIndex(names);
        sourceById = Collections.unmodifiableMap(originals);
        summaries = List.copyOf(summariesBuilder);
    }

    public List<ObjectDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<ObjectSummaryDto> summaries() {
        return summaries;
    }

    public ObjectDefinition findById(String id) {
        return byId.get(id);
    }

    public ObjectSourceDte sourceById(String id) {
        return sourceById.get(id);
    }

    public int size() {
        return byId.size();
    }

    public List<ObjectDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return byName.getOrDefault(normalize(name), List.of());
    }

    private static ObjectFileDte load(ObjectMapper mapper) {
        try (InputStream input = new ClassPathResource(RESOURCE).getInputStream()) {
            return mapper.readerFor(ObjectFileDte.class).with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Objects source resource " + RESOURCE, exception);
        }
    }

    private static void validateSources(ObjectSourceDte record, SourceRegistry registry) {
        validateSource(record.source(), registry);
        if (record.token() != null) {
            validateSource(record.token().source(), registry);
        }
        if (record.altArt() != null) {
            record.altArt().forEach(art -> validateSource(art.source(), registry));
        }
        if (record.reprintedAs() != null) {
            for (String uid : record.reprintedAs()) {
                String[] parts = uid.split("\\|");
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new IllegalStateException("Malformed object reprint UID: " + uid);
                }
                validateSource(parts[1], registry);
            }
        }
    }

    private static void validateSource(String code, SourceRegistry registry) {
        if (code == null || registry.find(code) == null) {
            throw new IllegalStateException("Object references unknown source code: " + code);
        }
    }

    private static void putUnique(Map<String, ObjectDefinition> map, String key, ObjectDefinition value, String kind) {
        if (map.putIfAbsent(key, value) != null) {
            throw new IllegalStateException("Duplicate " + kind + ": " + key);
        }
    }

    private static Map<String, List<ObjectDefinition>> immutableNameIndex(Map<String, List<ObjectDefinition>> values) {
        Map<String, List<ObjectDefinition>> result = new TreeMap<>();
        values.forEach((name, matches) -> result.put(name, matches.stream()
                .sorted(Comparator.comparing((ObjectDefinition item) -> item.source().identity().code(),
                        String.CASE_INSENSITIVE_ORDER).thenComparing(ObjectDefinition::id)).toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
