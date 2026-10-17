package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellNormalizationResult;
import com.thedndpub.data.dto.spell.SpellSchool;
import com.thedndpub.data.importer.spell.SpellDefinitionNormalizer;
import com.thedndpub.data.importer.spell.SpellSourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** Immutable, startup-loaded index over the normalized spell corpus. */
@Component
public final class SpellCatalog {
    private final Map<String, SpellDefinition> byId;
    private final Map<String, List<SpellDefinition>> byName;
    private final Map<String, List<SpellDefinition>> bySource;
    private final List<SpellDefinition> definitions;

    @Autowired
    public SpellCatalog(ObjectMapper objectMapper, SourceRegistry sourceRegistry) {
        this(objectMapper, sourceRegistry, new PathMatchingResourcePatternResolver());
    }

    public SpellCatalog(
            ObjectMapper objectMapper,
            SourceRegistry sourceRegistry,
            ResourcePatternResolver resourceResolver) {
        this(new SpellDefinitionNormalizer(sourceRegistry)
                .normalize(new SpellSourceLoader(objectMapper, resourceResolver).load()));
    }

    public SpellCatalog(SpellNormalizationResult result) {
        Map<String, SpellDefinition> idIndex = new TreeMap<>();
        Map<String, SpellDefinition> identityIndex = new TreeMap<>();
        Map<String, List<SpellDefinition>> nameIndex = new TreeMap<>();
        Map<String, List<SpellDefinition>> sourceIndex = new TreeMap<>();

        for (SpellDefinition definition : result.definitions()) {
            SpellSchool.fromCode(definition.school());
            String id = definition.id().value();
            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate spell ID: " + id);
            }

            String name = normalize(definition.identity().name());
            String source = normalize(definition.source().identity().code());
            String identity = name + "\u0000" + source;
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate spell name/source identity: " + identity);
            }
            nameIndex.computeIfAbsent(name, ignored -> new ArrayList<>()).add(definition);
            sourceIndex.computeIfAbsent(source, ignored -> new ArrayList<>()).add(definition);
        }

        if (idIndex.size() != 969) {
            throw new IllegalStateException("Expected 969 normalized spells, found " + idIndex.size());
        }
        validateReprintTargets(idIndex);

        byId = Collections.unmodifiableMap(idIndex);
        byName = immutableIndex(nameIndex);
        bySource = immutableIndex(sourceIndex);
        definitions = idIndex.values().stream()
                .sorted(Comparator.comparing((SpellDefinition value) -> normalize(value.identity().name()))
                        .thenComparing(value -> normalize(value.source().identity().code()))
                        .thenComparing(value -> value.id().value()))
                .toList();
    }

    public List<SpellDefinition> findAll() {
        return definitions;
    }

    public SpellDefinition findById(String id) {
        return byId.get(id);
    }

    public List<SpellDefinition> findByName(String name) {
        return byName.getOrDefault(normalize(name), List.of());
    }

    public List<SpellDefinition> findBySource(String source) {
        return bySource.getOrDefault(normalize(source), List.of());
    }

    public int size() {
        return byId.size();
    }

    private static void validateReprintTargets(Map<String, SpellDefinition> byId) {
        for (SpellDefinition definition : byId.values()) {
            definition.sourceRelationships().reprintedAs().forEach(reference -> {
                if (!byId.containsKey(reference.targetId().value())) {
                    throw new IllegalStateException("Missing spell reprint target: " + reference.targetId().value());
                }
            });
        }
    }

    private static Map<String, List<SpellDefinition>> immutableIndex(
            Map<String, List<SpellDefinition>> source) {
        Map<String, List<SpellDefinition>> immutable = new TreeMap<>();
        source.forEach((key, values) -> immutable.put(key, List.copyOf(values)));
        return Collections.unmodifiableMap(immutable);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT);
    }
}
