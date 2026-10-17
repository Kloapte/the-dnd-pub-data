package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.deity.DeityCopyResolver;
import com.thedndpub.data.dto.deity.DeityDefinition;
import com.thedndpub.data.dto.deity.DeityMapper;
import com.thedndpub.data.dto.deity.DeitySummaryDto;
import com.thedndpub.data.dte.deity.DeitiesFileDte;
import com.thedndpub.data.dte.deity.DeityAdditionalSourceDte;
import com.thedndpub.data.dte.deity.DeityDte;
import com.thedndpub.data.services.SourceRegistry;
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

/** Immutable startup catalog for source-authored deity definitions. */
@Component
public final class DeityCatalog {
    private static final String RESOURCE_PATH = "data/deities.json";

    private final Map<String, DeityDefinition> definitionsById;
    private final Map<String, List<DeityDefinition>> definitionsByName;
    private final List<DeitySummaryDto> summaries;

    @Autowired
    public DeityCatalog(ObjectMapper objectMapper, DeityMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public DeityCatalog(DeitiesFileDte source, DeityMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        validateMetadata(source.metadata());

        List<DeityDefinition> definitions = new DeityCopyResolver().resolve(source).stream()
                .map(resolved -> {
                    validateAuthoredSources(resolved.authored(), sourceRegistry);
                    return mapper.map(resolved);
                })
                .toList();

        Map<String, DeityDefinition> idIndex = new TreeMap<>();
        Map<String, DeityDefinition> identityIndex = new TreeMap<>();
        Map<String, List<DeityDefinition>> nameIndex = new TreeMap<>();
        List<DeitySummaryDto> summaryValues = new ArrayList<>(definitions.size());

        for (DeityDefinition definition : definitions) {
            String id = definition.id().value();
            String identity = identity(definition.name(), definition.source().identity().code(),
                    definition.pantheon());
            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate Deity ID: " + id);
            }
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate Deity source/name/pantheon identity: " + identity);
            }

            addName(nameIndex, definition.name(), definition);
            definition.alternateNames().forEach(alias -> addName(nameIndex, alias, definition));
            summaryValues.add(DeitySummaryDto.from(definition));
        }

        Comparator<DeityDefinition> definitionOrder = Comparator
                .comparing((DeityDefinition value) -> value.source().identity().code(),
                        String.CASE_INSENSITIVE_ORDER)
                .thenComparing(DeityDefinition::pantheon, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(DeityDefinition::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value());
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableNameIndex(nameIndex, definitionOrder);
        summaries = summaryValues.stream()
                .sorted(Comparator.comparing(DeitySummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(value -> value.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(DeitySummaryDto::pantheon, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(value -> value.id().value()))
                .toList();
    }

    public List<DeityDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public List<DeitySummaryDto> summaries() {
        return summaries;
    }

    public DeityDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<DeityDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return definitionsById.size();
    }

    private static DeitiesFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(DeitiesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Deities resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateMetadata(com.thedndpub.data.dte.deity.DeitiesMetadataDte metadata) {
        if (metadata == null || !metadata.internalCopies().contains("deity")) {
            throw new IllegalStateException("Deities source metadata must declare the deity internal copy type");
        }
    }

    private static void validateAuthoredSources(DeityDte deity, SourceRegistry registry) {
        requireSource(registry, deity.source(), "primary deity source");
        if (deity.copy() != null) {
            requireSource(registry, deity.copy().source(), "deity copy source");
        }
        if (deity.additionalSources() != null) {
            for (DeityAdditionalSourceDte reference : deity.additionalSources()) {
                requireSource(registry, reference.source(), "deity additional source");
            }
        }
        if (deity.customExtensionOf() != null) {
            String[] parts = deity.customExtensionOf().split("\\|", -1);
            if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank() || parts[2].isBlank()) {
                throw new IllegalStateException("Malformed deity customExtensionOf UID: " + deity.customExtensionOf());
            }
            requireSource(registry, parts[2], "deity custom extension source");
        }
    }

    private static void requireSource(SourceRegistry registry, String code, String role) {
        if (code == null || registry.find(code) == null) {
            throw new IllegalStateException("Deity " + role + " is missing from Source Registry: " + code);
        }
    }

    private static void addName(
            Map<String, List<DeityDefinition>> index,
            String name,
            DeityDefinition definition) {
        if (name != null && !name.isBlank()) {
            List<DeityDefinition> matches = index.computeIfAbsent(normalize(name), ignored -> new ArrayList<>());
            if (matches.stream().noneMatch(existing -> existing.id().equals(definition.id()))) {
                matches.add(definition);
            }
        }
    }

    private static Map<String, List<DeityDefinition>> immutableNameIndex(
            Map<String, List<DeityDefinition>> values,
            Comparator<DeityDefinition> order) {
        Map<String, List<DeityDefinition>> copy = new TreeMap<>();
        values.forEach((key, definitions) -> copy.put(key, definitions.stream().sorted(order).toList()));
        return Collections.unmodifiableMap(copy);
    }

    private static String identity(String name, String source, String pantheon) {
        return normalize(source) + "\u0000" + normalize(pantheon) + "\u0000" + normalize(name);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
