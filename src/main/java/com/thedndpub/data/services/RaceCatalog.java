package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.race.RaceDefinition;
import com.thedndpub.data.dto.race.RaceDefinitionKind;
import com.thedndpub.data.dto.race.RaceDefinitionNormalizer;
import com.thedndpub.data.dto.race.RaceMapper;
import com.thedndpub.data.dto.race.RaceSummaryDto;
import com.thedndpub.data.dte.race.RaceDte;
import com.thedndpub.data.dte.race.RaceReprintDte;
import com.thedndpub.data.dte.race.RaceSourceReferenceDte;
import com.thedndpub.data.dte.race.RacesFileDte;
import com.thedndpub.data.dte.race.SubraceDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Component
public final class RaceCatalog {
    private static final String RESOURCE_PATH = "data/races.json";

    private final Map<String, RaceDefinition> byId;
    private final Map<String, List<RaceDefinition>> byName;
    private final Map<String, List<RaceDefinition>> bySource;
    private final Map<RaceDefinitionKind, List<RaceDefinition>> byKind;
    private final List<RaceSummaryDto> summaries;
    private final int rootCount;
    private final int expandedCount;

    @Autowired
    public RaceCatalog(ObjectMapper objectMapper, RaceMapper mapper, SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public RaceCatalog(RacesFileDte source, RaceMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");

        List<RaceDefinition> definitions = new RaceDefinitionNormalizer().normalize(source, sourceRegistry);
        Map<String, RaceDefinition> ids = new TreeMap<>();
        Map<String, RaceDefinition> identities = new TreeMap<>();
        Map<String, List<RaceDefinition>> names = new TreeMap<>();
        Map<String, List<RaceDefinition>> sources = new TreeMap<>();
        Map<RaceDefinitionKind, List<RaceDefinition>> kinds = new TreeMap<>();
        List<RaceSummaryDto> summaryList = new ArrayList<>();

        for (RaceDefinition definition : definitions) {
            validateDefinitionSources(definition, sourceRegistry);
            if (ids.putIfAbsent(definition.id().value(), definition) != null) {
                throw new IllegalStateException("Duplicate race ID: " + definition.id().value());
            }
            String identity = identity(definition);
            if (identities.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate race definition identity: " + identity);
            }
            names.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            sources.computeIfAbsent(normalize(definition.source().identity().code()), ignored -> new ArrayList<>())
                    .add(definition);
            kinds.computeIfAbsent(definition.kind(), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(mapper.toSummary(definition));
        }

        for (RaceDefinition definition : definitions) {
            if (definition.parentId() != null && !ids.containsKey(definition.parentId().value())) {
                throw new IllegalStateException("Race subrace has unresolved parent: "
                        + definition.parentId().value());
            }
        }

        byId = Collections.unmodifiableMap(ids);
        byName = immutableMultiMap(names);
        bySource = immutableMultiMap(sources);
        byKind = immutableMultiMap(kinds);
        summaries = summaryList.stream().sorted(RaceCatalog::compareSummaries).toList();
        rootCount = source.race().size() + source.subrace().size();
        expandedCount = (int) definitions.stream()
                .filter(definition -> definition.provenance().expandedVersion())
                .count();
    }

    public int size() {
        return byId.size();
    }

    public int rootCount() {
        return rootCount;
    }

    public int expandedCount() {
        return expandedCount;
    }

    public List<RaceDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<RaceSummaryDto> summaries() {
        return summaries;
    }

    public RaceDefinition findById(String id) {
        return byId.get(id);
    }

    public List<RaceDefinition> findByName(String name) {
        return byName.getOrDefault(normalize(name), List.of());
    }

    public List<RaceDefinition> findBySource(String source) {
        return bySource.getOrDefault(normalize(source), List.of());
    }

    public List<RaceDefinition> findByKind(RaceDefinitionKind kind) {
        return byKind.getOrDefault(kind, List.of());
    }

    private static void validateDefinitionSources(RaceDefinition definition, SourceRegistry registry) {
        requireSource(registry, definition.source().identity().code());
        RaceDte race = definition.sourceData().race();
        SubraceDte subrace = definition.sourceData().subrace();
        if (race != null) {
            validateReferences(registry, race.otherSources());
            validateReferences(registry, race.additionalSources());
            validateReprints(registry, race.reprintedAs());
            validateLineage(registry, race.lineage());
        } else {
            validateReferences(registry, subrace.otherSources());
            validateReprints(registry, subrace.reprintedAs());
        }
        if (definition.parentId() != null) {
            if (subrace.raceSource() != null) {
                requireSource(registry, subrace.raceSource());
            }
            if (subrace.copy() != null && subrace.copy().raceSource() != null) {
                requireSource(registry, subrace.copy().raceSource());
            }
        }
    }

    private static void validateReferences(SourceRegistry registry, List<RaceSourceReferenceDte> references) {
        if (references == null) {
            return;
        }
        references.forEach(reference -> requireSource(registry, reference.source()));
    }

    private static void validateReprints(SourceRegistry registry, List<RaceReprintDte> references) {
        if (references == null) {
            return;
        }
        for (RaceReprintDte reference : references) {
            String[] parts = reference.uid().split("\\|", -1);
            if (parts.length > 1) {
                requireSource(registry, parts[parts.length - 1]);
            }
        }
    }

    private static void validateLineage(SourceRegistry registry, com.thedndpub.data.dte.race.RaceLineageDte lineage) {
        if (lineage != null && lineage.description() != null) {
            requireSource(registry, lineage.description());
        }
    }

    private static void requireSource(SourceRegistry registry, String source) {
        if (source == null || registry.find(source) == null) {
            throw new IllegalStateException("Race source code is missing from Source Registry: " + source);
        }
    }

    private static RacesFileDte load(ObjectMapper mapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return mapper.readerFor(RacesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load race resource " + RESOURCE_PATH, exception);
        }
    }

    private static String identity(RaceDefinition definition) {
        return normalize(definition.name()) + "\u0000"
                + normalize(definition.source().identity().code()) + "\u0000"
                + definition.kind() + "\u0000"
                + (definition.parentId() == null ? "" : definition.parentId().value());
    }

    private static <K> Map<K, List<RaceDefinition>> immutableMultiMap(Map<K, List<RaceDefinition>> values) {
        Map<K, List<RaceDefinition>> result = new TreeMap<>((left, right) ->
                String.CASE_INSENSITIVE_ORDER.compare(left.toString(), right.toString()));
        values.forEach((key, definitions) -> result.put(key, definitions.stream().sorted(RaceCatalog::compareDefinitions)
                .toList()));
        return Collections.unmodifiableMap(result);
    }

    private static int compareSummaries(RaceSummaryDto left, RaceSummaryDto right) {
        int name = String.CASE_INSENSITIVE_ORDER.compare(left.name(), right.name());
        if (name != 0) {
            return name;
        }
        int source = String.CASE_INSENSITIVE_ORDER.compare(
                left.source().identity().code(), right.source().identity().code());
        return source != 0 ? source : left.id().compareTo(right.id());
    }

    private static int compareDefinitions(RaceDefinition left, RaceDefinition right) {
        int name = String.CASE_INSENSITIVE_ORDER.compare(left.name(), right.name());
        if (name != 0) {
            return name;
        }
        int source = String.CASE_INSENSITIVE_ORDER.compare(
                left.source().identity().code(), right.source().identity().code());
        return source != 0 ? source : left.id().value().compareTo(right.id().value());
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT);
    }
}
